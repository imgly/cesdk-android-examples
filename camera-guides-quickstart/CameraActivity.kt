import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import ly.img.camera.core.CameraResult
import ly.img.camera.core.Capture
import ly.img.camera.core.CaptureMedia
import ly.img.camera.core.EngineConfiguration

private const val TAG = "CameraActivity"

// highlight-android-handle-result
private fun handleCameraResult(result: CameraResult?) {
    result ?: run {
        Log.i(TAG, "Camera dismissed")
        return
    }
    when (result) {
        is CameraResult.Captures -> {
            result.captures.forEach { capture ->
                when (capture) {
                    is Capture.Photo -> {
                        Log.i(TAG, "Captured photo: ${capture.uri}")
                    }
                    is Capture.Video -> {
                        val videoUris = capture.recording.videos.map { it.uri }
                        Log.i(TAG, "Recorded video: $videoUris")
                    }
                }
            }
        }

        else -> {
            Log.i(TAG, "Unhandled result: $result")
        }
    }
}
// highlight-android-handle-result

class CameraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // highlight-android-create-input
        val cameraInput = CaptureMedia.Input(
            engineConfiguration = EngineConfiguration(
                license = "YOUR_CESDK_LICENSE_KEY",
                userId = "<your unique user id>",
            ),
        )
        // highlight-android-create-input

        setContent {
            // highlight-android-register-launcher
            val cameraLauncher = rememberLauncherForActivityResult(contract = CaptureMedia()) { result ->
                handleCameraResult(result)
            }
            // highlight-android-register-launcher

            Button(
                onClick = {
                    // highlight-android-launch-camera
                    cameraLauncher.launch(cameraInput)
                    // highlight-android-launch-camera
                },
            ) {
                Text(text = "Open Camera")
            }
        }
    }
}
