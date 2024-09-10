package ly.img.editor.core.ui.library.engine

import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

internal fun Engine.replaceSticker(
    stickerBlock: DesignBlock,
    uri: String,
) {
    block.setString(block.getFill(stickerBlock), "fill/image/imageFileURI", uri)
    block.setContentFillMode(stickerBlock, ContentFillMode.CONTAIN)
}
