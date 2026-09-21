import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import ly.img.camera.core.CameraConfiguration
import ly.img.camera.core.CameraResult
import ly.img.camera.core.Capture
import ly.img.camera.core.CaptureCount
import ly.img.camera.core.CaptureMedia
import ly.img.camera.core.CaptureType
import ly.img.camera.core.EngineConfiguration
import kotlin.time.Duration.Companion.seconds

private const val TAG = "TakePhotoActivity"

class TakePhotoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // highlight-android-single-photo-input
        val singlePhotoInput = CaptureMedia.Input(
            engineConfiguration = EngineConfiguration(
                license = null, // pass null or empty for evaluation mode with watermark
                userId = "<your unique user id>",
            ),
            cameraConfiguration = CameraConfiguration(
                captureType = CaptureType.Photo,
                captureCount = CaptureCount.Single,
            ),
        )
        // highlight-android-single-photo-input

        // highlight-android-multi-photo-input
        val multiPhotoInput = CaptureMedia.Input(
            engineConfiguration = EngineConfiguration(
                license = null,
                userId = "<your unique user id>",
            ),
            cameraConfiguration = CameraConfiguration(
                captureType = CaptureType.Photo,
                captureCount = CaptureCount.Multi,
                photoClipDuration = 4.seconds,
                showsPhotoPreview = false,
            ),
        )
        // highlight-android-multi-photo-input

        setContent {
            // highlight-android-launch-photo-camera
            val cameraLauncher = rememberLauncherForActivityResult(contract = CaptureMedia()) { result ->
                handlePhotoResult(result)
            }

            Column {
                Button(
                    onClick = {
                        cameraLauncher.launch(singlePhotoInput)
                    },
                ) {
                    Text(text = "Take Photo")
                }

                Button(
                    onClick = {
                        cameraLauncher.launch(multiPhotoInput)
                    },
                ) {
                    Text(text = "Take Multiple Photos")
                }
            }
            // highlight-android-launch-photo-camera
        }
    }
}

// highlight-android-handle-photo-result
private fun handlePhotoResult(result: CameraResult?) {
    result ?: run {
        Log.i(TAG, "Camera dismissed")
        return
    }

    when (result) {
        is CameraResult.Captures -> {
            val photos = result.captures.filterIsInstance<Capture.Photo>()
            for (photo in photos) {
                // Copy the file behind photo.uri to app-owned storage here if the image must persist.
                Log.i(TAG, "Captured photo: ${photo.uri}, clip duration: ${photo.clipDuration}")
            }
        }

        else -> {
            Log.i(TAG, "Unhandled result")
        }
    }
}
// highlight-android-handle-photo-result
