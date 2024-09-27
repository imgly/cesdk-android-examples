package ly.img.editor.base.timeline.clip

import ly.img.engine.DesignBlock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class Clip(
    val id: DesignBlock,
    val clipType: ClipType,
    val trimmableId: DesignBlock = id,
    val fillId: DesignBlock? = null,
    val shapeId: DesignBlock? = null,
    val blurId: DesignBlock? = null,
    val effectIds: List<DesignBlock>? = null,
    val title: String = "",
    val duration: Duration = 5.seconds,
    val footageDuration: Duration? = null,
    val timeOffset: Duration = 0.seconds,
    val allowsTrimming: Boolean = false,
    val allowsSelecting: Boolean = true,
    val trimOffset: Duration = 0.seconds,
    val isMuted: Boolean = false,
    val volume: Float = 1.0f,
    val isInBackgroundTrack: Boolean = false,
    val hasLoaded: Boolean = false,
)

enum class ClipType {
    Audio,
    Image,
    Shape,
    Sticker,
    Text,
    Video,
}
