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

private const val TAG = "RecordingsCameraActivity"

class RecordingsCameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cameraInput = CaptureVideo.Input(
            engineConfiguration = EngineConfiguration(
                license = "<your license here>",
                userId = "<your unique user id>",
            ),
        )

        setContent {
            val cameraLauncher = rememberLauncherForActivityResult(contract = CaptureVideo()) { result ->
                // highlight-failure
                result ?: run {
                    Log.d(TAG, "Camera dismissed")
                    return@rememberLauncherForActivityResult
                }
                // highlight-failure
                // highlight-success
                when (result) {
                    // highlight-standard
                    is CameraResult.Record -> {
                        for (recording in result.recordings) {
                            Log.d(TAG, "Duration: ${recording.duration}")
                            for (video in recording.videos) {
                                Log.d(TAG, "Video Uri: ${video.uri} Video Rect: ${video.rect}")
                            }
                        }
                    }
                    // highlight-standard
                    // highlight-reaction
                    is CameraResult.Reaction -> {
                        Log.d(TAG, "Video uri: ${result.video.uri}")
                        for (reaction in result.reaction) {
                            Log.d(TAG, "Duration: ${reaction.duration}")
                            for (video in reaction.videos) {
                                Log.d(TAG, "Video Uri: ${video.uri} Video Rect: ${video.rect}")
                            }
                        }
                    }
                    // highlight-reaction

                    else -> {
                        Log.d(TAG, "Unhandled result")
                    }
                }
                // highlight-success
            }

            Button(
                onClick = {
                    cameraLauncher.launch(cameraInput)
                },
            ) {
                Text(text = "Open Camera")
            }
        }
    }
}
