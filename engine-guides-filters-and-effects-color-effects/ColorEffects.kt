import ly.img.editor.defaultBaseUri
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

data class ColorEffects(
    val stackedEffectTypes: List<String>,
    val tintEffectTypes: List<String>,
    val thresholdEffectTypes: List<String>,
    val shiftChannelsEffectTypes: List<String>,
    val thresholdLevel: Float,
    val takeRedFrom: String,
    val tintProperties: List<String>,
    val previewPng: ByteBuffer,
)

suspend fun colorEffects(engine: Engine): ColorEffects {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 800F)
    engine.block.setHeight(block = page, value = 620F)
    engine.block.appendChild(parent = scene, child = page)

    // highlight-android-create-image
    // Adds a 360 x 270 image block to the page at a position
    val imageUri = defaultBaseUri.buildUpon()
        .appendPath("ly.img.image")
        .appendPath("images")
        .appendPath("sample_1.jpg")
        .build()

    fun addImageBlock(
        x: Float,
        y: Float,
    ): DesignBlock {
        val block = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block = block, shape = engine.block.createShape(ShapeType.Rect))
        val fill = engine.block.createFill(FillType.Image)
        engine.block.setUri(block = fill, property = "fill/image/imageFileURI", value = imageUri)
        engine.block.setFill(block = block, fill = fill)
        engine.block.setWidth(block = block, value = 360F)
        engine.block.setHeight(block = block, value = 270F)
        engine.block.setPositionX(block = block, value = x)
        engine.block.setPositionY(block = block, value = y)
        engine.block.appendChild(parent = page, child = block)
        return block
    }

    val stackBlock = addImageBlock(x = 30F, y = 30F)
    val tintBlock = addImageBlock(x = 410F, y = 30F)
    val thresholdBlock = addImageBlock(x = 30F, y = 320F)
    val shiftChannelsBlock = addImageBlock(x = 410F, y = 320F)
    // highlight-android-create-image

    // highlight-android-stack
    // Levels runs first and stretches the input range
    val levels = engine.block.createEffect(EffectType.Levels)
    engine.block.setFloat(block = levels, property = "effect/levels/rgbInBlack", value = 0.1F)
    engine.block.setFloat(block = levels, property = "effect/levels/rgbInWhite", value = 0.9F)
    engine.block.appendEffect(block = stackBlock, effectBlock = levels)

    // Tritone runs second and maps black, mid gray and white to three colors
    val tritone = engine.block.createEffect(EffectType.Tritone)
    engine.block.setColor(
        block = tritone,
        property = "effect/tritone/shadows",
        value = Color.fromRGBA(r = 0.1F, g = 0.1F, b = 0.3F),
    )
    engine.block.setColor(
        block = tritone,
        property = "effect/tritone/midtones",
        value = Color.fromRGBA(r = 0.8F, g = 0.3F, b = 0.4F),
    )
    engine.block.setColor(
        block = tritone,
        property = "effect/tritone/highlights",
        value = Color.fromRGBA(r = 1F, g = 0.9F, b = 0.7F),
    )
    engine.block.appendEffect(block = stackBlock, effectBlock = tritone)
    // highlight-android-stack

    // highlight-android-tint
    // Map black to dark blue and white to warm yellow, at 80% strength
    val tint = engine.block.createEffect(EffectType.Tint)
    engine.block.setColor(
        block = tint,
        property = "effect/tint/mapBlackTo",
        value = Color.fromRGBA(r = 0.05F, g = 0.1F, b = 0.35F),
    )
    engine.block.setColor(
        block = tint,
        property = "effect/tint/mapWhiteTo",
        value = Color.fromRGBA(r = 1F, g = 0.85F, b = 0.4F),
    )
    engine.block.setFloat(block = tint, property = "effect/tint/amount", value = 0.8F)
    engine.block.appendEffect(block = tintBlock, effectBlock = tint)
    // highlight-android-tint

    // highlight-android-threshold
    // Pixels with a luminance of 0.45 or more turn white, all others black
    val threshold = engine.block.createEffect(EffectType.Threshold)
    engine.block.setFloat(block = threshold, property = "effect/threshold/level", value = 0.45F)
    engine.block.appendEffect(block = thresholdBlock, effectBlock = threshold)
    // highlight-android-threshold

    // highlight-android-shift-channels
    // Swap the red and the blue channel
    val shiftChannels = engine.block.createEffect(EffectType.ShiftChannels)
    engine.block.setEnum(block = shiftChannels, property = "effect/shift_channels/takeRedFrom", value = "Blue")
    engine.block.setEnum(block = shiftChannels, property = "effect/shift_channels/takeBlueFrom", value = "Red")
    engine.block.appendEffect(block = shiftChannelsBlock, effectBlock = shiftChannels)
    // highlight-android-shift-channels

    // highlight-android-export
    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    // highlight-android-export

    val effectTypes = { block: DesignBlock ->
        engine.block.getEffects(block).map { engine.block.getType(it) }
    }
    return ColorEffects(
        stackedEffectTypes = effectTypes(stackBlock),
        tintEffectTypes = effectTypes(tintBlock),
        thresholdEffectTypes = effectTypes(thresholdBlock),
        shiftChannelsEffectTypes = effectTypes(shiftChannelsBlock),
        thresholdLevel = engine.block.getFloat(block = threshold, property = "effect/threshold/level"),
        takeRedFrom = engine.block.getEnum(block = shiftChannels, property = "effect/shift_channels/takeRedFrom"),
        tintProperties = engine.block.findAllProperties(tint),
        previewPng = previewPng,
    )
}
