package ly.img.editor.showcases.plugin.imagegen.util

import ly.img.editor.showcases.plugin.imagegen.Format
import ly.img.engine.BlockState
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

fun Engine.createPendingBlock(
    format: Format,
    customWidth: String,
    customHeight: String,
): DesignBlock {
    val currentPage = requireNotNull(scene.getCurrentPage()) { "Current page is null" }

    val (blockWidth, blockHeight) = calculateBlockDimensions(
        format = format,
        customWidth = customWidth,
        customHeight = customHeight,
        pageWidth = block.getWidth(currentPage),
        pageHeight = block.getHeight(currentPage),
    )

    val newBlock = block.create(DesignBlockType.Graphic).apply {
        block.setShape(block = this, shape = block.createShape(ShapeType.Rect))
        block.setWidth(block = this, value = blockWidth)
        block.setHeight(block = this, value = blockHeight)
        block.appendChild(parent = currentPage, child = this)
        block.setState(block = this, state = BlockState.Pending(0f))
    }

    centerBlockOnPage(block = newBlock, blockWidth = blockWidth, blockHeight = blockHeight)

    setPlaceholderFill(block = newBlock)

    return newBlock
}

private fun Engine.calculateBlockDimensions(
    format: Format,
    customWidth: String,
    customHeight: String,
    pageWidth: Float,
    pageHeight: Float,
): Pair<Float, Float> = when (format) {
    Format.CUSTOM -> {
        val width = customWidth.toFloatOrNull() ?: 1920f
        val height = customHeight.toFloatOrNull() ?: 1080f
        val aspectRatio = width / height

        TextToImageUtils.calculateSizeWithAspectRatio(
            aspectRatio = aspectRatio,
            pageWidth = pageWidth,
            pageHeight = pageHeight,
        )
    }
    else -> {
        val (widthRatio, heightRatio) = format.ratio.split(":").map { it.toFloat() }
        val aspectRatio = widthRatio / heightRatio

        TextToImageUtils.calculateSizeWithAspectRatio(
            aspectRatio = aspectRatio,
            pageWidth = pageWidth,
            pageHeight = pageHeight,
        )
    }
}

private fun Engine.centerBlockOnPage(
    block: DesignBlock,
    blockWidth: Float,
    blockHeight: Float,
) {
    val currentPage = requireNotNull(scene.getCurrentPage()) { "Current page is null" }
    val pageWidth = this.block.getWidth(currentPage)
    val pageHeight = this.block.getHeight(currentPage)

    val centerX = (pageWidth - blockWidth) / 2f
    val centerY = (pageHeight - blockHeight) / 2f

    this.block.setPositionX(block = block, value = centerX)
    this.block.setPositionY(block = block, value = centerY)
}

private fun Engine.setPlaceholderFill(block: DesignBlock) {
    val colorFill = this.block.createFill(FillType.Color)
    this.block.setColor(
        block = colorFill,
        property = "fill/color/value",
        value = ly.img.engine.Color.fromRGBA(r = 0.0F, g = 0.0F, b = 1.0F, a = 0.1F),
    )
    this.block.setFill(block = block, fill = colorFill)
}

fun Engine.addImageToBlock(
    block: DesignBlock,
    imageUri: String,
) {
    val newFill = this.block.createFill(FillType.Image)
    this.block.setString(
        block = newFill,
        property = "fill/image/imageFileURI",
        value = imageUri,
    )

    this.block.setState(
        block = block,
        state = BlockState.Pending(0f),
    )

    this.block.setFill(block = block, fill = newFill)

    this.block.setState(
        block = block,
        state = BlockState.Ready,
    )
}
