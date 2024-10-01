package ly.img.editor.core.ui.library.engine

import ly.img.engine.BlockApi
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.FillType

fun BlockApi.isBackgroundTrack(designBlock: DesignBlock): Boolean {
    return DesignBlockType.get(getType(designBlock)) == DesignBlockType.Track &&
        isAlwaysOnBottom(designBlock)
}

fun BlockApi.getBackgroundTrack(): DesignBlock? {
    return findByType(DesignBlockType.Track).firstOrNull { isBackgroundTrack(it) }
}

fun BlockApi.isVideoBlock(designBlock: DesignBlock): Boolean {
    val fill = if (hasFill(designBlock)) getFill(designBlock) else null
    return fill != null && FillType.get(getType(fill)) is FillType.Video
}
