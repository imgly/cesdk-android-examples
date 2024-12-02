package ly.img.editor.core.ui.library.engine

import ly.img.engine.BlockApi
import ly.img.engine.DesignBlock
import ly.img.engine.FillType

fun BlockApi.isVideoBlock(designBlock: DesignBlock): Boolean {
    val fill = if (hasFill(designBlock)) getFill(designBlock) else null
    return fill != null && FillType.get(getType(fill)) is FillType.Video
}
