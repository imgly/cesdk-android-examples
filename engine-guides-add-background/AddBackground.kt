import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.GradientColorStop
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode

fun addBackground(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    try {
        engine.bindOffscreen(width = 1080, height = 1920)

        // highlight-android-setup
        val scene = engine.scene.create()

        val page = engine.block.create(DesignBlockType.Page)
        engine.block.setWidth(page, value = 800F)
        engine.block.setHeight(page, value = 600F)
        engine.block.appendChild(parent = scene, child = page)
        // highlight-android-setup

        // highlight-android-page-fill
        if (engine.block.supportsFill(page)) {
            val gradientFill = engine.block.createFill(FillType.LinearGradient)
            engine.block.setGradientColorStops(
                block = gradientFill,
                property = "fill/gradient/colors",
                colorStops = listOf(
                    GradientColorStop(
                        color = Color.fromRGBA(r = 0.85F, g = 0.75F, b = 0.95F, a = 1F),
                        stop = 0F,
                    ),
                    GradientColorStop(
                        color = Color.fromRGBA(r = 0.7F, g = 0.9F, b = 0.95F, a = 1F),
                        stop = 1F,
                    ),
                ),
            )
            engine.block.setFill(page, fill = gradientFill)
        }
        // highlight-android-page-fill

        val textBlock = engine.block.create(DesignBlockType.Text)
        engine.block.replaceText(textBlock, text = "Backgrounds")
        engine.block.setTextFontSize(block = textBlock, fontSize = 48F)
        engine.block.setWidth(textBlock, value = 280F)
        engine.block.setHeightMode(textBlock, mode = SizeMode.AUTO)
        engine.block.setPositionX(textBlock, value = 66F)
        engine.block.setPositionY(textBlock, value = 280F)
        engine.block.appendChild(parent = page, child = textBlock)

        // highlight-android-background-color
        if (engine.block.supportsBackgroundColor(textBlock)) {
            engine.block.setBackgroundColorEnabled(textBlock, enabled = true)
            engine.block.setBackgroundColor(
                block = textBlock,
                color = Color.fromRGBA(r = 1F, g = 1F, b = 1F, a = 1F),
            )
            engine.block.setFloat(textBlock, property = "backgroundColor/paddingLeft", value = 16F)
            engine.block.setFloat(textBlock, property = "backgroundColor/paddingRight", value = 16F)
            engine.block.setFloat(textBlock, property = "backgroundColor/paddingTop", value = 10F)
            engine.block.setFloat(textBlock, property = "backgroundColor/paddingBottom", value = 10F)
            engine.block.setFloat(textBlock, property = "backgroundColor/cornerRadius", value = 8F)
        }
        // highlight-android-background-color

        val imageBlock = engine.block.create(DesignBlockType.Graphic)
        val rectShape = engine.block.createShape(ShapeType.Rect)
        engine.block.setShape(imageBlock, shape = rectShape)
        engine.block.setWidth(imageBlock, value = 340F)
        engine.block.setHeight(imageBlock, value = 400F)
        engine.block.setPositionX(imageBlock, value = 420F)
        engine.block.setPositionY(imageBlock, value = 100F)
        engine.block.appendChild(parent = page, child = imageBlock)

        // highlight-android-shape-fill
        if (engine.block.supportsFill(imageBlock)) {
            val imageFill = engine.block.createFill(FillType.Image)
            engine.block.setString(
                block = imageFill,
                property = "fill/image/imageFileURI",
                value = "https://img.ly/static/ubq_samples/sample_1.jpg",
            )
            engine.block.setFill(imageBlock, fill = imageFill)
        }
        // highlight-android-shape-fill
    } finally {
        engine.stop()
    }
}
