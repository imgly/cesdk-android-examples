package ly.img.camera.record

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import ly.img.camera.core.Recording
import ly.img.camera.core.Video
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal class RecordingManager(
    private val maxDuration: Duration,
    private val allowExceedingMaxDuration: Boolean,
    private val coroutineScope: CoroutineScope,
    private val videoRecorder: VideoRecorder,
) {
    var state by mutableStateOf(State())
        private set

    val isRecording
        get() = state.status is Status.Recording
    val hasStartedRecording
        get() = state.status is Status.Recording || state.status is Status.StartRecording

    fun toggleRecording(context: Context) {
        when {
            state.hasReachedMaxDuration -> return
            state.status is Status.TimerRunning -> resetTimer()
            hasStartedRecording -> stop()
            state.timer != Timer.Off -> startTimer(context)
            else -> startRecording(context)
        }
    }

    fun setTimer(timer: Timer) {
        state = state.copy(timer = timer)
    }

    // We intentionally use GlobalScope here so the deletion coroutine isn't cancelled on closing the camera.
    @OptIn(DelicateCoroutinesApi::class)
    fun deletePreviousRecording() {
        val recording = state.recordings.lastOrNull()
        recording ?: return
        val recordings = state.recordings.dropLast(1)
        val updatedDuration = calculateUpdatedDuration(recordings)
        state =
            state.copy(
                recordings = recordings,
                totalRecordedDuration = updatedDuration,
                hasReachedMaxDuration = hasReachedMaxDuration(updatedDuration),
            )
        GlobalScope.launch(Dispatchers.IO) {
            deleteSingleRecording(recording)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun close() {
        videoRecorder.close()
        val recordings = state.recordings
        if (recordings.isEmpty()) return
        GlobalScope.launch(Dispatchers.IO) {
            recordings.forEach(::deleteSingleRecording)
        }
        state = state.copy(recordings = emptyList())
    }

    fun pause() {
        videoRecorder.pause()
    }

    fun stop() {
        if (state.status is Status.TimerRunning) {
            resetTimer()
        } else if (hasStartedRecording) {
            videoRecorder.stopRecording()
        }
    }

    fun resume() {
        videoRecorder.resume()
    }

    private fun startRecording(context: Context) {
        state = state.copy(status = Status.StartRecording)
        var reachedMaxDuration = false
        videoRecorder.startRecording(context) { status ->
            when (status) {
                is VideoRecorder.RecordingStatus.Error -> {
                    state = state.copy(status = Status.Idle, totalRecordedDuration = calculateUpdatedDuration())
                    // Since the recording failed, delete the backing file if it exists.
                    GlobalScope.launch(Dispatchers.IO) {
                        deleteFileUri(status.outputUri)
                    }
                }

                is VideoRecorder.RecordingStatus.Finished -> {
                    val updatedDuration = calculateUpdatedDuration()
                    state =
                        state.copy(
                            status = Status.Idle,
                            recordings = state.recordings + Recording(listOf(Video(status.outputUri)), status.duration),
                            totalRecordedDuration = updatedDuration,
                            hasReachedMaxDuration = hasReachedMaxDuration(updatedDuration),
                        )
                }

                is VideoRecorder.RecordingStatus.Recording -> {
                    val updatedDuration = calculateUpdatedDuration()
                    if (!reachedMaxDuration) {
                        state =
                            state.copy(
                                status = Status.Recording(status.duration),
                                totalRecordedDuration = updatedDuration,
                            )
                    }
                    if (hasReachedMaxDuration(updatedDuration)) {
                        reachedMaxDuration = true
                        stop()
                    }
                }
            }
        }
    }

    private var timerJob: Job? = null

    private fun startTimer(context: Context) {
        var countDownValue = state.timer.duration
        state = state.copy(status = Status.TimerRunning(remainingTime = countDownValue, totalTime = countDownValue))
        timerJob =
            coroutineScope.launch {
                while (isActive && countDownValue > 0) {
                    delay(1000)
                    countDownValue--
                    if (countDownValue != 0) {
                        val status = state.status as? Status.TimerRunning ?: break
                        state = state.copy(status = status.copy(remainingTime = countDownValue))
                    }
                }
                yield()
                startRecording(context)
            }
    }

    private fun resetTimer() {
        timerJob?.cancel()
        timerJob = null
        state = state.copy(status = Status.Idle)
    }

    private fun deleteSingleRecording(recording: Recording) {
        recording.videos.forEach {
            deleteFileUri(it.uri)
        }
    }

    private fun deleteFileUri(uri: Uri) {
        runCatching {
            val file = File(checkNotNull(uri.path))
            file.delete()
        }
    }

    private fun calculateUpdatedDuration(
        recordings: List<Recording> = state.recordings,
        currentRecordingDuration: Duration? = (state.status as? Status.Recording)?.currentRecordingDuration,
    ) = recordings.fold(0.seconds) { total, recording -> total + recording.duration } + (currentRecordingDuration ?: 0.seconds)

    private fun hasReachedMaxDuration(duration: Duration) = !allowExceedingMaxDuration && duration >= maxDuration

    sealed interface Status {
        data object Idle : Status

        data class TimerRunning(val remainingTime: Int, val totalTime: Int) : Status

        data object StartRecording : Status

        data class Recording(val currentRecordingDuration: Duration) : Status
    }

    data class State(
        val timer: Timer = Timer.Off,
        val recordings: List<Recording> = emptyList(),
        val totalRecordedDuration: Duration = Duration.ZERO,
        val hasReachedMaxDuration: Boolean = false,
        val status: Status = Status.Idle,
    )
}
