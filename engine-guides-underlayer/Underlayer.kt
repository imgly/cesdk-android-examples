import kotlinx.coroutines.*
import ly.img.engine.*
import java.io.File

fun underlayer(
    license: String,
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 100, height = 100)

    // highlight-setup
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block, shape = engine.block.createShape(ShapeType.Star))
    engine.block.setPositionX(block, value = 350F)
    engine.block.setPositionY(block, value = 400F)
    engine.block.setWidth(block, value = 100F)
    engine.block.setHeight(block, value = 100F)

    val fill = engine.block.createFill(FillType.Color)
    engine.block.setFill(block, fill = fill)
    val rgbaBlue = Color.fromRGBA(r = 0F, g = 0F, b = 1F, a = 1F)
    engine.block.setColor(fill, property = "fill/color/value", value = rgbaBlue)
    // highlight-setup

    // highlight-create-underlayer-spot-color
    engine.editor.setSpotColor(name = "RDG_WHITE", Color.fromRGBA(r = 0.8F, g = 0.8F, b = 0.8F))
    // highlight-create-underlayer-spot-color

    // highlight-export-pdf-underlayer
    val mimeType = MimeType.PDF
    val options =
        ExportOptions(
            exportPdfWithUnderlayer = true,
            underlayerSpotColorName = "RDG_WHITE",
            underlayerOffset = -2.0F,
        )
    val blob = engine.block.export(scene, mimeType = mimeType, options = options)
    withContext(Dispatchers.IO) {
        File.createTempFile("underlayer_example", ".pdf").apply {
            outputStream().channel.write(blob)
        }
    }
    // highlight-export-pdf-underlayer

    engine.stop()
}
