import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.RGBAColor
import ly.img.engine.ShapeType
import java.io.File
import java.nio.ByteBuffer

suspend fun exportWithColorMask(engine: Engine): Pair<ByteBuffer, ByteBuffer> {
    val page = createColorMaskScene(engine)
    return exportPageWithColorMask(engine, page)
}

// highlight-android-export-with-color-mask
suspend fun exportPageWithColorMask(
    engine: Engine,
    page: DesignBlock,
): Pair<ByteBuffer, ByteBuffer> {
    val maskColor = Color.fromRGBA(r = 1F, g = 0F, b = 0F)
    val options = ExportOptions(
        pngCompressionLevel = 9,
        targetWidth = 800F,
        targetHeight = 600F,
    )

    val (maskedImage, maskImage) = engine.block.exportWithColorMask(
        block = page,
        mimeType = MimeType.PNG,
        maskColor = maskColor,
        options = options,
    )

    check(maskedImage.hasRemaining()) { "Masked image export is empty" }
    check(maskImage.hasRemaining()) { "Mask image export is empty" }
    return maskedImage to maskImage
}
// highlight-android-export-with-color-mask

// highlight-android-save-color-mask-files
suspend fun saveColorMaskFiles(
    maskedImage: ByteBuffer,
    maskImage: ByteBuffer,
    outputDir: File,
): Pair<File, File> = withContext(Dispatchers.IO) {
    outputDir.mkdirs()

    val maskedImageFile = File(outputDir, "color-mask-image.png")
    val maskImageFile = File(outputDir, "color-mask-mask.png")

    fun writeBuffer(
        buffer: ByteBuffer,
        outputFile: File,
    ) {
        outputFile.outputStream().channel.use { channel ->
            val readableBuffer = buffer.asReadOnlyBuffer()
            while (readableBuffer.hasRemaining()) {
                channel.write(readableBuffer)
            }
        }
    }

    writeBuffer(maskedImage, maskedImageFile)
    writeBuffer(maskImage, maskImageFile)

    maskedImageFile to maskImageFile
}
// highlight-android-save-color-mask-files

private fun createColorMaskScene(engine: Engine): DesignBlock {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    fun addRectangle(
        name: String,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: RGBAColor,
    ) {
        val block = engine.block.create(DesignBlockType.Graphic)
        engine.block.setName(block, name = name)
        engine.block.setShape(block, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setFill(block, fill = engine.block.createFill(FillType.Color))
        engine.block.setFillSolidColor(block = block, color = color)
        engine.block.setPositionX(block, value = x)
        engine.block.setPositionY(block, value = y)
        engine.block.setWidth(block, value = width)
        engine.block.setHeight(block, value = height)
        engine.block.appendChild(parent = page, child = block)
    }

    addRectangle(
        name = "Color mask background",
        x = 0F,
        y = 0F,
        width = 800F,
        height = 600F,
        color = Color.fromHex("#FFF8FAFC"),
    )
    addRectangle(
        name = "Print content",
        x = 170F,
        y = 145F,
        width = 460F,
        height = 310F,
        color = Color.fromHex("#FF2457D6"),
    )

    val redMaskColor = Color.fromRGBA(r = 1F, g = 0F, b = 0F)
    listOf(
        48F to 48F,
        704F to 48F,
        48F to 504F,
        704F to 504F,
    ).forEachIndexed { index, (x, y) ->
        addRectangle(
            name = "Registration mark ${index + 1}",
            x = x,
            y = y,
            width = 48F,
            height = 48F,
            color = redMaskColor,
        )
    }

    return page
}
