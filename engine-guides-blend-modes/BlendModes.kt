import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.BlendMode
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.RGBAColor
import ly.img.engine.ShapeType

fun blendModes(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    fun addColorBlock(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: RGBAColor,
    ): DesignBlock {
        val block = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setPositionX(block, value = x)
        engine.block.setPositionY(block, value = y)
        engine.block.setWidth(block, value = width)
        engine.block.setHeight(block, value = height)

        val fill = engine.block.createFill(FillType.Color)
        engine.block.setFill(block, fill = fill)
        engine.block.setFillSolidColor(block = block, color = color)
        engine.block.appendChild(parent = page, child = block)

        return block
    }

    // Create a base block first so the top block has content below it to blend with.
    addColorBlock(
        x = 80F,
        y = 80F,
        width = 420F,
        height = 320F,
        color = Color.fromRGBA(r = 0.12F, g = 0.35F, b = 0.95F, a = 1F),
    )
    val topBlock = addColorBlock(
        x = 240F,
        y = 180F,
        width = 420F,
        height = 320F,
        color = Color.fromRGBA(r = 1F, g = 0.55F, b = 0.08F, a = 1F),
    )

    // highlight-android-check-support
    // Scope checks use engine scope key strings.
    val canSetBlendMode =
        engine.block.supportsBlendMode(topBlock) &&
            engine.block.isAllowedByScope(topBlock, key = "layer/blendMode")
    println("Can set blend mode: $canSetBlendMode")
    // highlight-android-check-support

    // highlight-android-set-blend-mode
    if (canSetBlendMode) {
        engine.block.setBlendMode(topBlock, blendMode = BlendMode.MULTIPLY)
    }
    // highlight-android-set-blend-mode

    // highlight-android-get-blend-mode
    val currentBlendMode = engine.block.getBlendMode(topBlock)
    println("Current blend mode: $currentBlendMode")
    check(currentBlendMode == BlendMode.MULTIPLY)
    // highlight-android-get-blend-mode

    // highlight-android-set-opacity
    val canSetOpacity =
        engine.block.supportsOpacity(topBlock) &&
            engine.block.isAllowedByScope(topBlock, key = "layer/opacity")
    if (canSetOpacity) {
        engine.block.setOpacity(topBlock, value = 0.7F)
    }
    // highlight-android-set-opacity

    // highlight-android-get-opacity
    val currentOpacity = engine.block.getOpacity(topBlock)
    println("Current opacity: $currentOpacity")
    check(currentOpacity == 0.7F)
    // highlight-android-get-opacity

    engine.stop()
}
