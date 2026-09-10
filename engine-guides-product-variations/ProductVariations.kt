import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.io.File

private data class ProductVariant(
    val name: String,
    val color: String,
    val size: String,
    val price: String,
    val imageURL: String,
)

suspend fun productVariations(
    context: Context,
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
): List<File> = withContext(Dispatchers.Main) {
    val engine = Engine.getInstance(id = "ly.img.engine.product-variations")
    try {
        engine.start(license = license, userId = userId)
        engine.bindOffscreen(width = 1080, height = 1080)

        // highlight-productVariations-dataModel
        val variants = listOf(
            ProductVariant(
                name = "Classic Tee",
                color = "Midnight Black",
                size = "M",
                price = "$29.99",
                imageURL = "https://img.ly/static/ubq_samples/sample_1.jpg",
            ),
            ProductVariant(
                name = "Classic Tee",
                color = "Ocean Blue",
                size = "L",
                price = "$34.99",
                imageURL = "https://img.ly/static/ubq_samples/sample_2.jpg",
            ),
        )
        // highlight-productVariations-dataModel

        // highlight-productVariations-createTemplate
        val scene = engine.scene.create()
        val page = engine.block.create(DesignBlockType.Page)
        engine.block.appendChild(parent = scene, child = page)
        engine.block.setWidth(page, value = 500F)
        engine.block.setHeight(page, value = 500F)

        val text = engine.block.create(DesignBlockType.Text)
        engine.block.appendChild(parent = page, child = text)
        engine.block.setWidth(text, value = 400F)
        engine.block.setHeight(text, value = 50F)
        engine.block.setPositionX(text, value = 50F)
        engine.block.setPositionY(text, value = 50F)
        engine.block.setName(text, name = "ProductTitle")
        engine.block.replaceText(text, text = "{{ProductName}} – {{ProductColor}}")
        engine.block.setTextColor(text, color = Color.fromHex("#FF000000"))

        val priceText = engine.block.create(DesignBlockType.Text)
        engine.block.appendChild(parent = page, child = priceText)
        engine.block.setWidth(priceText, value = 200F)
        engine.block.setHeight(priceText, value = 40F)
        engine.block.setPositionX(priceText, value = 50F)
        engine.block.setPositionY(priceText, value = 120F)
        engine.block.setName(priceText, name = "ProductPriceLabel")
        engine.block.replaceText(priceText, text = "{{ProductPrice}}")
        engine.block.setTextColor(priceText, color = Color.fromHex("#FF000000"))

        val imageBlock = engine.block.create(DesignBlockType.Graphic)
        engine.block.appendChild(parent = page, child = imageBlock)
        engine.block.setShape(imageBlock, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setWidth(imageBlock, value = 300F)
        engine.block.setHeight(imageBlock, value = 300F)
        engine.block.setPositionX(imageBlock, value = 100F)
        engine.block.setPositionY(imageBlock, value = 180F)
        engine.block.setName(imageBlock, name = "ProductImage")
        val imageFill = engine.block.createFill(FillType.Image)
        engine.block.setFill(imageBlock, fill = imageFill)
        engine.block.setString(
            block = imageFill,
            property = "fill/image/imageFileURI",
            value = "https://img.ly/static/ubq_samples/sample_1.jpg",
        )
        engine.block.setContentFillMode(imageBlock, ContentFillMode.CONTAIN)

        // Seed the variable store that this sample persists with the reusable template string.
        engine.variable.set(key = "ProductName", value = variants.first().name)
        engine.variable.set(key = "ProductColor", value = variants.first().color)
        engine.variable.set(key = "ProductPrice", value = variants.first().price)

        val templateString = engine.scene.saveToString(scene = scene)
        // highlight-productVariations-createTemplate

        // highlight-productVariations-discoverVariables
        val tokenRegex = Regex("""\{\{\s*([^{}]+?)\s*\}\}""")
        val templateTokens = engine.block.findByType(DesignBlockType.Text)
            .flatMap { textBlock ->
                tokenRegex.findAll(engine.block.getString(textBlock, property = "text/text"))
                    .map { match -> match.groupValues[1].trim() }
                    .toList()
            }
            .distinct()
        println("Template text tokens: $templateTokens")
        // Expected: [ProductName, ProductColor, ProductPrice]
        // highlight-productVariations-discoverVariables

        val exportedFiles = mutableListOf<File>()

        // highlight-productVariations-generateLoop
        for (variant in variants) {
            engine.scene.load(scene = templateString)

            // highlight-productVariations-setVariables
            engine.variable.set(key = "ProductName", value = variant.name)
            engine.variable.set(key = "ProductColor", value = variant.color)
            engine.variable.set(key = "ProductPrice", value = variant.price)
            // Keep the rendered text blocks in sync for Android's offscreen export path.
            engine.block.findByName("ProductTitle").firstOrNull()?.let { title ->
                engine.block.replaceText(
                    title,
                    text = "${variant.name} – ${variant.color} (${variant.size})",
                )
            }
            engine.block.findByName("ProductPriceLabel").firstOrNull()?.let { priceLabel ->
                engine.block.replaceText(priceLabel, text = "${variant.price} · Size ${variant.size}")
            }
            // highlight-productVariations-setVariables

            // highlight-productVariations-replaceImage
            engine.block.findByName("ProductImage").firstOrNull()?.let { block ->
                val fill = engine.block.getFill(block)
                engine.block.setString(
                    block = fill,
                    property = "fill/image/imageFileURI",
                    value = variant.imageURL,
                )
                engine.block.resetCrop(block)
            }
            // highlight-productVariations-replaceImage

            // highlight-productVariations-export
            val exportPage = engine.block.findByType(DesignBlockType.Page).firstOrNull()
                ?: continue
            // Android needs explicit resource preloading so text glyphs and image fills are
            // resolved before the offscreen export runs.
            engine.block.forceLoadResources(
                engine.block.findByType(DesignBlockType.Text) +
                    engine.block.findByName("ProductImage"),
            )
            val blob = engine.block.export(exportPage, mimeType = MimeType.JPEG)

            val fileName =
                "product-${variant.color.lowercase().replace(" ", "-")}-${variant.size}.jpg"
            val file = File(context.cacheDir, fileName)
            withContext(Dispatchers.IO) {
                blob.rewind()
                file.outputStream().channel.use { channel ->
                    channel.write(blob)
                }
            }
            exportedFiles += file
            // highlight-productVariations-export
        }
        // highlight-productVariations-generateLoop

        exportedFiles
    } finally {
        engine.stop()
    }
}
