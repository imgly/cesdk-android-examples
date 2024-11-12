package ly.img.editor.base.timeline.state

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ly.img.editor.core.ui.engine.getCurrentPage
import ly.img.editor.core.ui.utils.formatForPlayer
import ly.img.engine.Engine
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class PlayerState(
    private val engine: Engine,
) {
    var isPlaying: Boolean by mutableStateOf(false)
        private set

    var playheadPosition: Duration by mutableStateOf(0.seconds)
        private set

    var isLooping: Boolean by mutableStateOf(false)

    val formattedPlayheadPosition by derivedStateOf {
        playheadPosition.formatForPlayer()
    }

    private val page = engine.getCurrentPage()

    fun refresh() {
        isPlaying = engine.block.isPlaying(page)
        playheadPosition = engine.block.getPlaybackTime(page).seconds
        isLooping = engine.block.isLooping(page)
    }

    fun play() {
        val duration = engine.block.getDuration(page).seconds
        if (duration == ZERO) return
        if (playheadPosition >= duration) {
            setPlaybackTime(0.seconds)
        }
        engine.block.setPlaying(page, true)
    }

    fun pause() {
        engine.block.setPlaying(page, false)
    }

    fun togglePlayback() {
        if (isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun toggleLooping() {
        engine.block.setLooping(page, !isLooping)
    }

    fun setPlaybackTime(duration: Duration) {
        engine.block.setPlaybackTime(page, duration.toDouble(DurationUnit.SECONDS))
    }
}
