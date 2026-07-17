import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.camera.core.CameraResult
import ly.img.camera.core.CaptureMedia
import ly.img.camera.core.EngineConfiguration
import ly.img.camera.core.videos
import java.io.File
import java.util.UUID

private const val TAG = "RecordingsCameraActivity"

class RecordingsCameraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // highlight-android-create-input
        val cameraInput = CaptureMedia.Input(
            engineConfiguration = EngineConfiguration(
                license = null, // pass null or empty for evaluation mode with watermark
                userId = "<your unique user id>",
            ),
        )
        // highlight-android-create-input

        setContent {
            CameraLaunchButton(cameraInput = cameraInput)
        }
    }
}

// highlight-android-camera-launch-button
private object RecordingPersistence {
    // This process-owned scope outlives Compose and Activity instances.
    // Use WorkManager for copies that must survive process termination.
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}

@Composable
private fun CameraLaunchButton(cameraInput: CaptureMedia.Input) {
    val applicationContext = LocalContext.current.applicationContext
    val cameraLauncher = rememberLauncherForActivityResult(contract = CaptureMedia()) { result ->
        handleCameraResult(
            context = applicationContext,
            result = result,
            persistenceScope = RecordingPersistence.applicationScope,
        )
    }

    Button(
        onClick = {
            cameraLauncher.launch(cameraInput)
        },
    ) {
        Text(text = "Open Camera")
    }
}
// highlight-android-camera-launch-button

private fun handleCameraResult(
    context: Context,
    result: CameraResult?,
    persistenceScope: CoroutineScope,
) {
    // highlight-android-handle-no-result
    if (result == null) {
        Log.i(TAG, "Camera returned no successful result (canceled or failed)")
        return
    }
    // highlight-android-handle-no-result

    when (result) {
        // highlight-android-read-captured-videos
        is CameraResult.Captures -> {
            for ((recordingIndex, recording) in result.captures.videos.withIndex()) {
                Log.i(TAG, "Recording $recordingIndex duration: ${recording.duration}")
                for ((videoIndex, video) in recording.videos.withIndex()) {
                    Log.i(TAG, "Video $videoIndex uri: ${video.uri}, rect: ${video.rect}")
                    persistenceScope.launch {
                        val persistedUri = persistRecordingVideo(
                            context = context,
                            sourceUri = video.uri,
                            fileName = "recording-$recordingIndex-$videoIndex-${UUID.randomUUID()}.mp4",
                        )
                        if (persistedUri != null) {
                            Log.i(TAG, "Persisted video $videoIndex uri: $persistedUri")
                        } else {
                            Log.i(TAG, "Unable to persist video $videoIndex from ${video.uri}")
                        }
                    }
                }
            }
        }
        // highlight-android-read-captured-videos

        // highlight-android-read-reaction-recordings
        is CameraResult.Reaction -> {
            Log.i(TAG, "Reacted-to video uri: ${result.video.uri}, rect: ${result.video.rect}")
            for ((reactionIndex, recording) in result.reaction.withIndex()) {
                Log.i(TAG, "Reaction $reactionIndex duration: ${recording.duration}")
                for ((videoIndex, video) in recording.videos.withIndex()) {
                    Log.i(TAG, "Reaction video $videoIndex uri: ${video.uri}, rect: ${video.rect}")
                    persistenceScope.launch {
                        val persistedUri = persistRecordingVideo(
                            context = context,
                            sourceUri = video.uri,
                            fileName = "reaction-$reactionIndex-$videoIndex-${UUID.randomUUID()}.mp4",
                        )
                        if (persistedUri != null) {
                            Log.i(TAG, "Persisted reaction video $videoIndex uri: $persistedUri")
                        } else {
                            Log.i(TAG, "Unable to persist reaction video $videoIndex from ${video.uri}")
                        }
                    }
                }
            }
        }
        // highlight-android-read-reaction-recordings

        else -> {
            Log.i(TAG, "Unhandled camera result")
        }
    }
}

// highlight-android-persist-returned-file
private suspend fun persistRecordingVideo(
    context: Context,
    sourceUri: Uri,
    fileName: String,
): Uri? = withContext(Dispatchers.IO) {
    val recordingsDir = File(context.filesDir, "camera-recordings").apply { mkdirs() }
    val destinationFile = File(recordingsDir, fileName)

    val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return@withContext null
    inputStream.use { input ->
        destinationFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    Uri.fromFile(destinationFile)
}
// highlight-android-persist-returned-file
