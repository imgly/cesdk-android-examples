import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ly.img.camera.core.CameraConfiguration
import ly.img.camera.core.CameraResult
import ly.img.camera.core.Capture
import ly.img.camera.core.CaptureMedia
import ly.img.camera.core.CaptureType
import ly.img.camera.core.EngineConfiguration

private const val TAG = "PhotosCamera"

// highlight-android-configure-photo-capture
fun photoCameraInput(): CaptureMedia.Input = CaptureMedia.Input(
    engineConfiguration = EngineConfiguration(
        license = null, // Use your production license for release builds.
        userId = "<your unique user id>",
    ),
    cameraConfiguration = CameraConfiguration(
        captureType = CaptureType.Photo,
    ),
)
// highlight-android-configure-photo-capture

// highlight-android-launch-photo-capture
@Composable
fun PhotosCameraButton() {
    val cameraLauncher = rememberLauncherForActivityResult(contract = CaptureMedia()) { result ->
        handlePhotoCameraResult(result)
    }

    Button(onClick = { cameraLauncher.launch(photoCameraInput()) }) {
        Text(text = "Open Camera")
    }
}
// highlight-android-launch-photo-capture

// highlight-android-read-photo-results
fun handlePhotoCameraResult(result: CameraResult?) {
    result ?: run {
        Log.i(TAG, "Camera returned no successful result")
        return
    }

    when (result) {
        is CameraResult.Captures -> {
            result.captures.filterIsInstance<Capture.Photo>().forEach { photo ->
                Log.i(TAG, "Photo URI: ${photo.uri}; clip duration: ${photo.clipDuration}")
            }
        }

        is CameraResult.Reaction -> {
            Log.i(TAG, "Reaction mode does not return photo captures")
        }

        else -> {
            Log.i(TAG, "Unhandled camera result: $result")
        }
    }
}
// highlight-android-read-photo-results

// highlight-android-handle-mixed-captures
fun mixedCameraInput(): CaptureMedia.Input = CaptureMedia.Input(
    engineConfiguration = EngineConfiguration(
        license = null,
        userId = "<your unique user id>",
    ),
    cameraConfiguration = CameraConfiguration(
        captureType = CaptureType.Mixed,
    ),
)

fun handleMixedCameraResult(result: CameraResult?) {
    result ?: return

    if (result !is CameraResult.Captures) return

    result.captures.forEach { capture ->
        when (capture) {
            is Capture.Photo -> {
                Log.i(TAG, "Photo URI: ${capture.uri}; clip duration: ${capture.clipDuration}")
            }

            is Capture.Video -> {
                Log.i(TAG, "Video duration: ${capture.recording.duration}")
            }
        }
    }
}
// highlight-android-handle-mixed-captures
