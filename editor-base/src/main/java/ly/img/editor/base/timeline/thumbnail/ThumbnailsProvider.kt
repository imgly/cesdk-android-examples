package ly.img.editor.base.timeline.thumbnail

import android.content.res.Resources
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import ly.img.editor.base.engine.getAspectRatio
import ly.img.editor.base.timeline.clip.Clip
import ly.img.editor.base.timeline.state.TimelineConfiguration
import ly.img.engine.Engine
import ly.img.engine.EngineException
import ly.img.engine.VideoThumbnailResult
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.time.DurationUnit

class ThumbnailsProvider(
    private val engine: Engine,
    private val scope: CoroutineScope,
) {
    private var _thumbs = mutableStateOf(emptyList<VideoThumbnailResult>())
    val thumbnails: List<VideoThumbnailResult>
        get() = _thumbs.value

    private var thumbHeight = TimelineConfiguration.clipHeight

    private var job: Job? = null

    fun loadThumbnails(
        clip: Clip,
        width: Dp,
    ) {
        val aspectRatio = engine.block.getAspectRatio(clip.id)
        val thumbWidth = thumbHeight * aspectRatio
        val numberOfFrames = ceil(width / thumbWidth).toInt()

        job?.cancel()
        job =
            scope.launch {
                try {
                    engine.block.generateVideoThumbnailSequence(
                        block = clip.id,
                        thumbnailHeight = (thumbHeight.value * Resources.getSystem().displayMetrics.density).roundToInt(),
                        timeBegin = 0.0,
                        timeEnd = clip.duration.toDouble(DurationUnit.SECONDS),
                        numberOfFrames = numberOfFrames,
                    ).toList().let {
                        _thumbs.value = it
                    }
                } catch (_: EngineException) {
                    // do nothing, can happen in case the block is deleted while thumbs were being generated
                }
            }
    }

    fun cancel() {
        job?.cancel()
    }
}
