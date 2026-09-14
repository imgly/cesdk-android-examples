package ly.img.editor.showcases.plugin.imagegen.api

import ai.fal.client.ClientConfig
import ai.fal.client.CredentialsResolver
import ai.fal.client.kt.SubscribeOptions
import ai.fal.client.kt.createFalClient
import ai.fal.client.kt.subscribe
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.core.graphics.scale
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

object FalAIApi {
    private const val TEXT_TO_IMAGE_ENDPOINT = "fal-ai/recraft/v3/text-to-image"
    private const val IMAGE_TO_IMAGE_ENDPOINT = "fal-ai/recraft/v3/image-to-image"

    suspend fun generateImageWithFalAI(
        prompt: String,
        style: FalImageStyle,
        imageSize: Any,
        inputImageUri: String = "",
        context: Context,
        apiConfig: String,
    ): List<String> = withContext(Dispatchers.Default) {
        val fal = createFalClient(
            config = when {
                apiConfig.isEmpty() -> ClientConfig.builder().build()
                apiConfig.startsWith("http") -> ClientConfig.withProxyUrl(apiConfig)
                else -> ClientConfig.withCredentials(CredentialsResolver.fromApiKey(apiConfig))
            },
        )

        var endpointId = TEXT_TO_IMAGE_ENDPOINT
        val input = buildMap {
            put("prompt", prompt)
            put("style", style.apiValue)

            if (inputImageUri.isNotEmpty()) {
                try {
                    if (inputImageUri.startsWith("http")) {
                        put("image_url", inputImageUri)
                        put("strength", 0.5f)
                        endpointId = IMAGE_TO_IMAGE_ENDPOINT
                    } else {
                        // Local URI - encode to data URI (I/O operation)
                        val imageDataUri = withContext(Dispatchers.IO) {
                            encodeImageToDataUri(uri = inputImageUri.toUri(), context = context)
                        }
                        if (imageDataUri != null) {
                            put("image_url", imageDataUri)
                            put("strength", 0.5f)
                            endpointId = IMAGE_TO_IMAGE_ENDPOINT
                        }
                    }
                } catch (e: Exception) {
                    Log.w("FalAIImageGenPlugin", "Failed to process input image: ${e.message}")
                }
            } else {
                put("image_size", imageSize)
                put("enable_safety_checker", true)
            }
        }

        val result = fal.subscribe(
            endpointId = endpointId,
            input = input,
            options = SubscribeOptions(
                logs = true,
            ),
        )
        result.data
            .getAsJsonArray("images")
            .map {
                it.asJsonObject.get("url").asString
            }
    }

    /**
     * Encodes an image URI to a base64 data URI for Fal AI API
     */
    private fun encodeImageToDataUri(
        uri: Uri,
        context: Context,
    ): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return null

            val bitmap = BitmapFactory.decodeStream(inputStream)
                ?: return null

            // Scale down the image if it's too large to avoid API limits
            val scaledBitmap = if (bitmap.width > 1024 || bitmap.height > 1024) {
                val scale = minOf(1024f / bitmap.width, 1024f / bitmap.height)
                val newWidth = (bitmap.width * scale).toInt()
                val newHeight = (bitmap.height * scale).toInt()
                bitmap.scale(newWidth, newHeight)
            } else {
                bitmap
            }

            // Convert to base64
            val byteArrayOutputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream)
            val imageBytes = byteArrayOutputStream.toByteArray()
            val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

            val result = "data:image/jpeg;base64,$base64Image"
            result
        } catch (e: Exception) {
            Log.e("FalAIImageGenPlugin", "Failed to encode image to data URI\n${e.message}", e)
            null
        }
    }
}
