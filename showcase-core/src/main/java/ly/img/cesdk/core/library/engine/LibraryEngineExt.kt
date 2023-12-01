package ly.img.cesdk.core.library.engine

import android.net.Uri
import ly.img.engine.DesignBlockType
import ly.img.cesdk.core.engine.FONT_BASE_PATH
import ly.img.cesdk.core.engine.deselectAllBlocks
import ly.img.cesdk.core.engine.getPage
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.engine.PositionMode
import ly.img.engine.SizeMode
import kotlin.random.Random

internal fun Engine.replaceSticker(stickerBlock: DesignBlock, uri: String) {
    block.setString(block.getFill(stickerBlock), "fill/image/imageFileURI", uri)
    block.setContentFillMode(stickerBlock, ContentFillMode.CONTAIN)
}

internal fun Engine.addText(path: String, size: Float) : DesignBlock {
    val fontSize = (50.0f / 24.0f) * size
    val textBlock = block.create(DesignBlockType.Text)
    block.setString(textBlock, "text/fontFileUri", Uri.parse("$FONT_BASE_PATH/$path").toString())
    block.setFloat(textBlock, "text/fontSize", fontSize)
    block.setEnum(textBlock, "text/horizontalAlignment", "Center")
    block.setHeightMode(textBlock, SizeMode.AUTO)
    addBlockToPage(textBlock)
    return textBlock
}

/**
 * Appends a block into the scene and positions it somewhat randomly.
 */
private fun Engine.addBlockToPage(designBlock: DesignBlock) {
    deselectAllBlocks()
    // TODO: unhardcode page index
    block.appendChild(getPage(0), designBlock)
    block.setPositionXMode(designBlock, PositionMode.ABSOLUTE)
    block.setPositionX(designBlock, 15 + (Random.nextFloat() * 20))
    block.setPositionYMode(designBlock, PositionMode.ABSOLUTE)
    block.setPositionY(designBlock, 5 + (Random.nextFloat() * 20))
    block.setSelected(designBlock, true)
    editor.addUndoStep()
}