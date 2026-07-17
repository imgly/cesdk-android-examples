import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ly.img.camera.core.CameraConfiguration
import ly.img.camera.core.CameraMode
import ly.img.camera.core.CameraResult
import ly.img.camera.core.CaptureCount
import ly.img.camera.core.CaptureMedia
import ly.img.camera.core.CaptureType
import ly.img.camera.core.EngineConfiguration
import ly.img.camera.core.videos
import kotlin.time.Duration.Companion.seconds

private const val TAG = "ConfiguredCameraActivity"

fun createConfiguredCameraInput(
    license: String?,
    userId: String?,
) = CaptureMedia.Input(
    // highlight-android-engine-configuration
    engineConfiguration = EngineConfiguration(
        license = license, // null or an empty string opens the camera in evaluation (watermark) mode
        userId = userId,
    ),
    // highlight-android-engine-configuration
    // highlight-android-camera-configuration
    cameraConfiguration = CameraConfiguration(
        recordingColor = Color.Blue,
        maxTotalDuration = 30.seconds,
        allowExceedingMaxDuration = false,
    ),
    // highlight-android-camera-configuration
    // highlight-android-camera-mode
    cameraMode = CameraMode.Standard(),
    // highlight-android-camera-mode
)

@Composable
fun ConfiguredCameraScreen(
    license: String?,
    userId: String? = null,
) {
    val cameraInput = createConfiguredCameraInput(
        license = license,
        userId = userId,
    )

    // highlight-android-result
    val cameraLauncher = rememberLauncherForActivityResult(contract = CaptureMedia()) { result ->
        when (result) {
            null -> Log.i(TAG, "Camera dismissed")
            is CameraResult.Captures -> {
                val videoUris = result.captures.videos.flatMap { recording -> recording.videos.map { it.uri } }
                Log.i(TAG, "Captured ${result.captures.size} item(s); video URIs: $videoUris")
            }
            is CameraResult.Reaction -> Log.i(TAG, "Reaction with ${result.reaction.size} recording(s)")
            else -> Log.i(TAG, "Unhandled result: $result")
        }
    }
    // highlight-android-result

    Button(
        onClick = { cameraLauncher.launch(cameraInput) },
    ) {
        Text(text = "Open Camera")
    }
}

class ConfiguredCameraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConfiguredCameraScreen(
                license = null,
                userId = "<your unique user id>",
            )
        }
    }
}

// A single-photo session: take one photo and return immediately, skipping the confirm/discard preview.
// highlight-android-capture-type
val photoConfiguration = CameraConfiguration(
    captureType = CaptureType.Photo,
    captureCount = CaptureCount.Single,
    showsPhotoPreview = false,
)
// highlight-android-capture-type
