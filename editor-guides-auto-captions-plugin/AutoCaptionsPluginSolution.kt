// highlight-android-auto-captions-imports
import androidx.compose.runtime.Composable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import ly.img.editor.Editor
import ly.img.editor.configuration.video.VideoConfigurationBuilder
import ly.img.editor.configuration.video.callback.onCreate
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.configuration.then
import ly.img.editor.plugin.autoCaptions.AutoCaptionsPlugin
import ly.img.editor.plugin.autoCaptions.TranscriptionOptions
import ly.img.editor.plugin.autoCaptions.TranscriptionProvider
import ly.img.editor.plugin.autoCaptions.gateway.GatewayTranscriptionProvider
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
// highlight-android-auto-captions-imports

// Add this composable to your NavHost.
@Composable
fun AutoCaptionsPluginSolution(
    license: String,
    gatewayApiKey: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-android-auto-captions-basic-setup
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration
                .remember(::VideoConfigurationBuilder) {
                    onCreate = {
                        // Demo scaffolding — not part of the lesson: open a sample clip so the canvas shows
                        // footage behind the captions sheet and Generate Automatically has speech to transcribe.
                        onCreate(
                            createScene = {
                                editorContext.engine.scene.createFromVideo(
                                    videoUri = editorContext.baseUri
                                        .buildUpon()
                                        .appendPath("ly.img.video")
                                        .appendPath("videos")
                                        .appendPath("pexels-kampus-production-8154913.mp4")
                                        .build(),
                                )
                            },
                        )
                    }
                }
                .then(::AutoCaptionsPlugin) {
                    provider = GatewayTranscriptionProvider(apiKey = gatewayApiKey)
                }
        },
        onClose = onClose,
    )
    // highlight-android-auto-captions-basic-setup
}

@Composable
private fun AutoCaptionsTranscriptionOptionsSolution(
    license: String,
    gatewayApiKey: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::VideoConfigurationBuilder)
                // highlight-android-auto-captions-transcription-options
                .then(::AutoCaptionsPlugin) {
                    provider = GatewayTranscriptionProvider(apiKey = gatewayApiKey)
                    options = TranscriptionOptions(
                        language = "en",
                        maxLineLength = 30,
                        maxLines = 2,
                    )
                }
            // highlight-android-auto-captions-transcription-options
        },
        onClose = onClose,
    )
}

// highlight-android-auto-captions-custom-provider

/**
 * A minimal custom provider: post the audio to any speech-to-text service and return SRT text.
 */
class CustomTranscriptionProvider(
    private val httpClient: OkHttpClient = OkHttpClient(),
) : TranscriptionProvider {
    override val name = "My Speech-to-Text Service"

    override suspend fun transcribe(
        audio: File,
        mimeType: String,
        options: TranscriptionOptions,
    ): String {
        val url = "https://example.com/transcribe".toHttpUrl()
            .newBuilder()
            .apply { options.language?.let { addQueryParameter("language", it) } }
            .build()
        val request = Request.Builder()
            .url(url)
            // The file streams out in segments; reading it into memory first would defeat the point of
            // receiving one, since a scene's audio has no size limit.
            .post(audio.asRequestBody(mimeType.toMediaType()))
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = httpClient.newCall(request)
            // Tapping Cancel cancels the surrounding coroutine; aborting the call stops the request from
            // running on to its timeout.
            continuation.invokeOnCancellation { call.cancel() }
            call.enqueue(
                object : Callback {
                    override fun onFailure(
                        call: Call,
                        e: IOException,
                    ) = continuation.resumeWithException(e)

                    override fun onResponse(
                        call: Call,
                        response: Response,
                    ) {
                        response.use {
                            if (!it.isSuccessful) {
                                continuation.resumeWithException(IOException("Transcription failed: ${it.code}"))
                            } else {
                                // Convert your service's response to SRT here, and return an empty string
                                // when it reports no speech.
                                continuation.resume(it.body?.string().orEmpty())
                            }
                        }
                    }
                },
            )
        }
    }
}
// highlight-android-auto-captions-custom-provider

@Composable
private fun AutoCaptionsCustomProviderSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::VideoConfigurationBuilder)
                // highlight-android-auto-captions-use-custom-provider
                .then(::AutoCaptionsPlugin) {
                    provider = CustomTranscriptionProvider()
                }
            // highlight-android-auto-captions-use-custom-provider
        },
        onClose = onClose,
    )
}

@Composable
private fun AutoCaptionsGenerationCallbackSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration
                .remember(::VideoConfigurationBuilder)
                // highlight-android-auto-captions-generation-callback
                .then(::AutoCaptionsPlugin) {
                    // The plugin still owns the Generate action and its cancellable progress state; only the
                    // transcription is yours. It validates a provider at setup either way, so one is still
                    // required even though a replaced generator never calls it.
                    provider = CustomTranscriptionProvider()
                    captionsGeneration = {
                        // Replace this with your own pipeline: transcribe the scene's audible content —
                        // editorContext.engine reads it — and serialize the cues as SRT or VTT, timed relative
                        // to the page timeline. The fixed cue below stands in for that work.
                        val srt = """
                            1
                            00:00:00,000 --> 00:00:03,000
                            Captions from a custom pipeline
                        """.trimIndent()
                        // Returning null tells the user no speech was detected; any error thrown shows a
                        // generic message and reaches the integrator's `onError`.
                        if (srt.isBlank()) {
                            null
                        } else {
                            withContext(Dispatchers.IO) {
                                File.createTempFile("captions", ".srt", editorContext.activity.cacheDir)
                                    .apply { writeText(srt) }
                            }
                        }
                    }
                }
            // highlight-android-auto-captions-generation-callback
        },
        onClose = onClose,
    )
}
