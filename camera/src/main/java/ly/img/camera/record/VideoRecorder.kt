package ly.img.camera.record

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.camera.video.ExperimentalPersistentRecording
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

internal class VideoRecorder(private val videoCapture: VideoCapture<Recorder>) {
    private var recording: Recording? = null

    @OptIn(ExperimentalPersistentRecording::class)
    @SuppressLint("MissingPermission")
    fun startRecording(
        context: Context,
        onRecordStatusUpdate: (RecordingStatus) -> Unit,
    ) {
        val videoFile = createFile(context)
        val fileOutputOptions = FileOutputOptions.Builder(videoFile).build()

        recording =
            videoCapture.output
                .prepareRecording(context, fileOutputOptions)
                .asPersistentRecording()
                .withAudioEnabled()
                .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                    val duration = recordEvent.recordingStats.recordedDurationNanos.nanoseconds
                    when (recordEvent) {
                        is VideoRecordEvent.Status -> {
                            onRecordStatusUpdate(RecordingStatus.Recording(duration))
                        }

                        is VideoRecordEvent.Finalize -> {
                            val outputUri = recordEvent.outputResults.outputUri
                            if (!recordEvent.hasError()) {
                                onRecordStatusUpdate(RecordingStatus.Finished(outputUri, duration))
                            } else {
                                onRecordStatusUpdate(RecordingStatus.Error(outputUri, duration))
                            }
                        }
                    }
                }
    }

    fun stopRecording() {
        recording?.stop()
        recording = null
    }

    fun close() {
        recording?.close()
        recording = null
    }

    private fun createFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        return File(context.filesDir, "VIDEO_$timeStamp.mp4")
    }

    fun pause() {
        recording?.pause()
    }

    fun resume() {
        recording?.resume()
    }

    sealed class RecordingStatus(val duration: Duration) {
        class Recording(duration: Duration) : RecordingStatus(duration)

        class Finished(val outputUri: Uri, duration: Duration) : RecordingStatus(duration)

        class Error(val outputUri: Uri, duration: Duration) : RecordingStatus(duration)
    }
}
