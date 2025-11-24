import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import ly.img.camera.core.CameraResult
import ly.img.camera.core.CaptureVideo
import ly.img.camera.core.EngineConfiguration

private const val TAG = "CameraActivity"

class CameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // highlight-initialization
        val cameraInput = CaptureVideo.Input(
            engineConfiguration = EngineConfiguration(
                license = "<your license here>", // pass null or empty for evaluation mode with watermark
                userId = "<your unique user id>",
            ),
        )
        // highlight-initialization

        setContent {
            // highlight-launcher
            val cameraLauncher = rememberLauncherForActivityResult(contract = CaptureVideo()) { result ->
                // highlight-launcher
                // highlight-result
                result ?: run {
                    Log.d(TAG, "Camera dismissed")
                    return@rememberLauncherForActivityResult
                }
                when (result) {
                    is CameraResult.Record -> {
                        val recordedVideoUris = result.recordings.flatMap { it.videos.map { it.uri } }
                        // Do something with the recorded videos
                        Log.d(TAG, "Recorded videos: $recordedVideoUris")
                    }

                    else -> {
                        Log.d(TAG, "Unhandled result")
                    }
                }
                // highlight-result
            }

            Button(
                onClick = {
                    // highlight-launch
                    cameraLauncher.launch(cameraInput)
                    // highlight-launch
                },
            ) {
                Text(text = "Open Camera")
            }
        }
    }
}
