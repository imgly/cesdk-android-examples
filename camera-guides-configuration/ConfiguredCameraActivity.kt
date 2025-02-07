import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import ly.img.camera.core.CameraConfiguration
import ly.img.camera.core.CameraMode
import ly.img.camera.core.CaptureVideo
import ly.img.camera.core.EngineConfiguration
import kotlin.time.Duration.Companion.seconds

private const val TAG = "ConfiguredCameraActivity"

class ConfiguredCameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cameraInput =
            CaptureVideo.Input(
                // highlight-engine-configuration
                engineConfiguration =
                    EngineConfiguration(
                        // highlight-license
                        license = "<your license here>",
                        // highlight-userId
                        userId = "<your unique user id>",
                    ),
                // highlight-engine-configuration
                // highlight-camera-configuration
                cameraConfiguration =
                    CameraConfiguration(
                        // highlight-recording-color
                        recordingColor = Color.Blue,
                        // highlight-max-duration
                        maxTotalDuration = 30.seconds,
                        // highlight-allow-exceeding-max-duration
                        allowExceedingMaxDuration = false,
                    ),
                // highlight-camera-configuration
                // highlight-camera-mode
                cameraMode = CameraMode.Standard(),
                // highlight-camera-mode
            )

        setContent {
            val cameraLauncher =
                rememberLauncherForActivityResult(contract = CaptureVideo()) { result ->
                    result ?: run {
                        Log.d(TAG, "Camera dismissed")
                        return@rememberLauncherForActivityResult
                    }
                    Log.d(TAG, "Result: $result")
                }

            Button(onClick = {
                cameraLauncher.launch(cameraInput)
            }) {
                Text(text = "Open Camera")
            }
        }
    }
}
