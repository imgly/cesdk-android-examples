package ly.img.editor.configuration.videomoderation.moderation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * The IMG.LY gateway API key the starter kit moderates with.
 *
 * Create one in your IMG.LY dashboard and paste it here. This is the single place the kit reads
 * the key from, so every screen picks it up.
 */
const val GATEWAY_API_KEY = ""

/**
 * Default [ContentModerationProvider] backed by the IMG.LY gateway.
 *
 * This is the only class that knows about the moderation service: the endpoint, the response shape,
 * the categories and the thresholds all live here. It uses [HttpURLConnection] and [JSONObject]
 * from the Android platform, so the starter kit does not pull in any extra dependency.
 *
 * A request goes to `POST /v1/responses` with the `imgly/detection` model and one `video_url`,
 * `image_urls` entry or `text`, and streams its answer back as server-sent events. For a video, the
 * model samples the file and scores each sampled frame. Videos and images that the gateway cannot
 * fetch are uploaded first through `POST /v1/uploads`.
 *
 * @param apiKey the IMG.LY gateway API key. Create one in your IMG.LY dashboard.
 * @param failedThreshold confidence above which a category is reported as [ModerationState.FAILED].
 * @param warningThreshold confidence above which a category is reported as [ModerationState.WARNING].
 * @param gatewayUrl the gateway base URL. Override it to route through your own proxy.
 */
class GatewayModerationProvider(
    private val apiKey: String,
    private val failedThreshold: Double = 0.8,
    private val warningThreshold: Double = 0.4,
    private val gatewayUrl: String = DEFAULT_GATEWAY_URL,
) : ContentModerationProvider {
    override suspend fun moderate(content: ModerationContent): List<ModerationFrame> = withContext(Dispatchers.IO) {
        // Fail loudly instead of reporting fabricated (defaulted) scores for an unusable key.
        if (apiKey.isBlank()) {
            throw IOException("Missing IMG.LY gateway API key. Set GATEWAY_API_KEY in GatewayModerationProvider.kt.")
        }
        val detection = requestDetection(content)
        if (content !is ModerationContent.VideoUrl && content !is ModerationContent.VideoFile) {
            val scores = detection.optJSONObject("scores")
                ?: throw IOException("Content moderation returned no scores")
            return@withContext listOf(ModerationFrame(position = 0.0, categories = categoriesOf(scores)))
        }
        // The gateway also reports its own "safe" flag and the highest score per category over the
        // whole file. The kit ignores both and maps the scores of each frame itself, so that frames the
        // user trimmed away do not flag the clip.
        val frames = detection.optJSONArray("frames")
            ?.takeIf { it.length() > 0 }
            ?: throw IOException("Content moderation returned no frames for this video")
        (0 until frames.length()).map { index ->
            val frame = frames.getJSONObject(index)
            val scores = frame.optJSONObject("scores")
                ?: throw IOException("Content moderation returned a frame without scores")
            ModerationFrame(
                position = numberOf(frame, "position") / MILLISECONDS_PER_SECOND,
                categories = categoriesOf(scores),
            )
        }
    }

    /**
     * Maps one frame's scores to categories.
     *
     * Every category the service scored is reported, not only the ones this file knows about, so a
     * category added to the model later is surfaced instead of silently dropped. Known ids lead, in
     * a fixed order, so the results panel stays stable.
     */
    private fun categoriesOf(scores: JSONObject): List<ModerationCategory> {
        val reported = scores.keys().asSequence().toList()
        val known = CATEGORIES.filter { it.id in reported }
        val unknown = reported.filterNot { id -> CATEGORIES.any { it.id == id } }.sorted().map(::describe)
        return (known + unknown).map { category ->
            ModerationCategory(
                id = category.id,
                displayName = category.displayName,
                description = category.description,
                state = confidenceToState(numberOf(scores, category.id)),
            )
        }
    }

    /**
     * Reads a number the response must carry. A missing or malformed value fails the check, because
     * reading it as `0` would report the content as clean.
     */
    private fun numberOf(
        json: JSONObject,
        key: String,
    ): Double = (json.opt(key) as? Number)?.toDouble()?.takeIf { it.isFinite() }
        ?: throw IOException("Content moderation returned no number for \"$key\"")

    /**
     * Uploads a local video and returns the gateway URL the detection model can read it from.
     */
    private suspend fun upload(video: ModerationContent.VideoFile): String = upload(video.mimeType) { connection ->
        // Without a streaming mode HttpURLConnection buffers the whole body in memory first.
        if (video.length >= 0) {
            connection.setFixedLengthStreamingMode(video.length)
        } else {
            connection.setChunkedStreamingMode(UPLOAD_CHUNK_BYTES)
        }
        video.openStream().use { input -> connection.outputStream.use { input.copyTo(it) } }
    }

    /**
     * Uploads rendered image bytes and returns the gateway URL the detection model can read them from.
     */
    private suspend fun upload(image: ModerationContent.ImageBytes): String = upload(image.mimeType) { connection ->
        connection.writeBody(image.bytes)
    }

    /**
     * Asks the gateway for an upload slot, then writes the file to it with [writeBody].
     */
    private suspend fun upload(
        mimeType: String,
        writeBody: (HttpURLConnection) -> Unit,
    ): String {
        val response = request("$gatewayUrl/v1/uploads", method = "POST", authorized = true) { connection ->
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.writeBody(JSONObject().put("content_type", mimeType).toString().toByteArray())
            JSONObject(connection.readBodyOrThrow())
        }
        val uploadUrl = response.optString("upload_url").ifEmpty {
            throw IOException("Content moderation upload was not accepted")
        }
        val assetUrl = response.optString("asset_url").ifEmpty {
            throw IOException("Content moderation upload returned no asset URL")
        }
        // The presigned URL carries its own credentials — sending ours as well makes it reject the PUT.
        request(uploadUrl, method = "PUT", authorized = false) { connection ->
            connection.setRequestProperty("Content-Type", mimeType)
            connection.doOutput = true
            writeBody(connection)
            connection.readBodyOrThrow()
        }
        return assetUrl
    }

    /**
     * Runs the detection model on one video, image or text and returns the `data` object of its result.
     */
    private suspend fun requestDetection(content: ModerationContent): JSONObject {
        val body = JSONObject().put("model", DETECTION_MODEL)
        when (content) {
            is ModerationContent.VideoUrl -> body.put("video_url", content.url)
            is ModerationContent.VideoFile -> body.put("video_url", upload(content))
            is ModerationContent.ImageUrl -> body.put("image_urls", JSONArray().put(content.url))
            is ModerationContent.ImageBytes -> body.put("image_urls", JSONArray().put(upload(content)))
            is ModerationContent.Text -> body.put("text", content.text)
        }
        return request("$gatewayUrl/v1/responses", method = "POST", authorized = true) { connection ->
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "text/event-stream")
            connection.doOutput = true
            connection.writeBody(body.toString().toByteArray())
            if (connection.responseCode !in SUCCESS_CODES) throw IOException(connection.readErrorMessage())
            readDetection(connection)
        }
    }

    /**
     * Reads the server-sent event stream until the run completes.
     *
     * The detection model answers with one output per input, so a request for a single video, image
     * or text has exactly one JSON output. Status events are progress only and are skipped.
     */
    private fun readDetection(connection: HttpURLConnection): JSONObject {
        val reader = connection.inputStream.bufferedReader()
        var event: String? = null
        while (true) {
            val line = reader.readLine() ?: break
            when {
                line.startsWith(EVENT_PREFIX) -> event = line.removePrefix(EVENT_PREFIX).trim()
                line.startsWith(DATA_PREFIX) -> {
                    val data = line.removePrefix(DATA_PREFIX).trim()
                    when (event) {
                        EVENT_COMPLETED -> return firstOutput(JSONObject(data))
                        EVENT_FAILED -> throw IOException(
                            errorMessageOf(JSONObject(data)) ?: "Content moderation failed",
                        )
                    }
                }
            }
        }
        throw IOException("Content moderation stream ended without a result")
    }

    private fun firstOutput(completed: JSONObject): JSONObject = completed
        .optJSONArray("output")
        ?.optJSONObject(0)
        ?.optJSONObject("data")
        ?: throw IOException("Content moderation returned no result")

    /**
     * Runs one blocking HTTP exchange so that cancelling the check also aborts it.
     *
     * [HttpURLConnection] has no abort method, so a request already in flight would otherwise run
     * to completion after the user taps Cancel — finishing an upload and spending gateway credits
     * for a result nobody reads. Disconnecting from the cancelling thread unblocks the read instead.
     */
    private suspend fun <T> request(
        url: String,
        method: String,
        authorized: Boolean,
        block: (HttpURLConnection) -> T,
    ): T {
        // Do not even open a connection for a check that was cancelled while queued.
        currentCoroutineContext().ensureActive()
        return suspendCancellableCoroutine { continuation ->
            val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = method
                if (authorized) setRequestProperty("Authorization", "Bearer $apiKey")
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
            }
            continuation.invokeOnCancellation { runCatching { connection.disconnect() } }
            try {
                continuation.resume(block(connection))
            } catch (throwable: Throwable) {
                continuation.resumeWithException(throwable)
            } finally {
                connection.disconnect()
            }
        }
    }

    private fun HttpURLConnection.writeBody(bytes: ByteArray) = outputStream.use { it.write(bytes) }

    private fun HttpURLConnection.readBodyOrThrow(): String {
        if (responseCode !in SUCCESS_CODES) throw IOException(readErrorMessage())
        return inputStream.bufferedReader().use { it.readText() }
    }

    /**
     * Reads the gateway's `{ "error": { "code", "message" } }` body from the error stream, falling
     * back to the status code when the body is empty or is not the documented shape.
     */
    private fun HttpURLConnection.readErrorMessage(): String {
        val body = errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
        val message = runCatching { errorMessageOf(JSONObject(body)) }.getOrNull()
        return message ?: "Content moderation request failed with HTTP $responseCode"
    }

    private fun errorMessageOf(payload: JSONObject): String? {
        val error = payload.opt("error")
        val message = when (error) {
            is JSONObject -> error.optString("message")
            is String -> error
            else -> null
        }
        return message?.takeIf { it.isNotEmpty() }
    }

    /**
     * Builds a fallback entry for a category the service scored but this file has no copy for.
     *
     * "recreational_drug" becomes "Recreational Drug". Better a plainly named result than a silently
     * dropped one.
     */
    private fun describe(id: String) = Category(
        id = id,
        displayName = id.split('_').joinToString(" ") { it.replaceFirstChar(Char::uppercase) },
        description = "Reported by the moderation service.",
    )

    /**
     * Converts a `0..1` confidence into a [ModerationState] using the configured thresholds.
     */
    private fun confidenceToState(confidence: Double): ModerationState = when {
        confidence > failedThreshold -> ModerationState.FAILED
        confidence > warningThreshold -> ModerationState.WARNING
        else -> ModerationState.SUCCESS
    }

    /**
     * A category the detection model scores, with the copy shown in the results panel.
     */
    private data class Category(
        val id: String,
        val displayName: String,
        val description: String,
    )

    private companion object {
        const val DEFAULT_GATEWAY_URL = "https://gateway.img.ly"
        const val DETECTION_MODEL = "imgly/detection"
        const val CONNECT_TIMEOUT_MS = 15_000

        // The gateway samples the whole video before it answers, so a long clip takes longer than an image.
        const val READ_TIMEOUT_MS = 120_000
        const val UPLOAD_CHUNK_BYTES = 64 * 1024
        const val MILLISECONDS_PER_SECOND = 1000.0
        const val EVENT_PREFIX = "event:"
        const val DATA_PREFIX = "data:"
        const val EVENT_COMPLETED = "generation.completed"
        const val EVENT_FAILED = "generation.failed"
        val SUCCESS_CODES = 200..299

        /**
         * The categories the detection model reports, in the order they are listed in the results
         * panel. A video frame is scored like an image. The image and text category sets are disjoint,
         * so one table covers both: only the categories present in a response are reported. A category
         * the model scores that is missing here still appears, named from its id.
         */
        val CATEGORIES = listOf(
            Category(
                id = "nudity",
                displayName = "Nudity",
                description = "Raw or partial nudity, sexual acts, and suggestive content.",
            ),
            Category(
                id = "weapon",
                displayName = "Weapons",
                description = "Handguns, rifles, machine guns, threatening knives...",
            ),
            Category(
                id = "recreational_drug",
                displayName = "Recreational Drugs",
                description = "Cannabis and other recreational drugs.",
            ),
            Category(
                id = "medical",
                displayName = "Medical",
                description = "Pills, syringes, medical devices, and other medical imagery.",
            ),
            Category(
                id = "alcohol",
                displayName = "Alcohol",
                description = "Wine, beer, cocktails, champagne...",
            ),
            Category(
                id = "offensive",
                displayName = "Offensive Symbols",
                description = "Hate symbols, extremist imagery, and offensive gestures.",
            ),
            Category(
                id = "gore",
                displayName = "Gore",
                description = "Blood, wounds, corpses, and other graphic imagery.",
            ),
            // Text categories.
            Category(
                id = "toxic",
                displayName = "Toxic Language",
                description = "Language likely to make someone leave the conversation.",
            ),
            Category(
                id = "insulting",
                displayName = "Insulting Language",
                description = "Insults, name-calling, and demeaning remarks.",
            ),
            Category(
                id = "discriminatory",
                displayName = "Discriminatory Language",
                description = "Language that demeans a group or protected characteristic.",
            ),
            Category(
                id = "violent",
                displayName = "Violent Language",
                description = "Threats of violence and calls to harm someone.",
            ),
            Category(
                id = "sexual",
                displayName = "Sexual Language",
                description = "Sexually explicit or suggestive wording.",
            ),
            Category(
                id = "self-harm",
                displayName = "Self-Harm",
                description = "References to suicide, self-injury, or eating disorders.",
            ),
        )
    }
}
