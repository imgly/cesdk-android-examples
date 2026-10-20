import android.util.Log
import ly.img.editor.defaultBaseUri
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.EngineException
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

private const val TAG = "ColorDistortAndWipe"

data class ColorDistortAndWipeEffects(
    val colorEffectTypes: List<String>,
    val distortEffectTypes: List<String>,
    val wipeEffectTypes: List<String>,
    val strokeEffectTypes: List<String>,
    val strokeMaskCount: Int,
    val sourceMatchesMap: Boolean,
    val errorCode: String?,
    val previewPng: ByteBuffer,
)

suspend fun colorDistortAndWipeEffects(engine: Engine): ColorDistortAndWipeEffects {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 1200F)
    engine.block.setHeight(block = page, value = 900F)
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

    val colorBlock = addImageBlock(x = 30F, y = 150F)
    val distortBlock = addImageBlock(x = 420F, y = 150F)
    val wipeBlock = addImageBlock(x = 810F, y = 150F)
    val strokeBlock = addImageBlock(x = 225F, y = 480F)
    val displacedBlock = addImageBlock(x = 615F, y = 480F)
    // highlight-android-create-image

    // highlight-android-color
    // Levels runs first and stretches the input range
    val levels = engine.block.createEffect(EffectType.Levels)
    engine.block.setFloat(block = levels, property = "effect/levels/rgbInBlack", value = 0.1F)
    engine.block.setFloat(block = levels, property = "effect/levels/rgbInWhite", value = 0.9F)
    engine.block.appendEffect(block = colorBlock, effectBlock = levels)

    // Tritone runs second and maps dark, middle and light values to three colors
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
    engine.block.appendEffect(block = colorBlock, effectBlock = tritone)
    // highlight-android-color

    // highlight-android-distort
    // A wave 15 design units high with crests 90 design units apart
    val waveWarp = engine.block.createEffect(EffectType.WaveWarp)
    engine.block.setFloat(block = waveWarp, property = "effect/wave_warp/height", value = 15F)
    engine.block.setFloat(block = waveWarp, property = "effect/wave_warp/width", value = 90F)
    engine.block.setEnum(block = waveWarp, property = "effect/wave_warp/waveType", value = "Sine")
    engine.block.appendEffect(block = distortBlock, effectBlock = waveWarp)
    // highlight-android-distort

    // highlight-android-wipe
    // Hide the left 40% of the block with a soft edge
    val linearWipe = engine.block.createEffect(EffectType.LinearWipe)
    engine.block.setFloat(block = linearWipe, property = "effect/linear_wipe/angle", value = 90F)
    engine.block.setFloat(block = linearWipe, property = "effect/linear_wipe/completion", value = 0.4F)
    engine.block.setFloat(block = linearWipe, property = "effect/linear_wipe/feather", value = 40F)
    engine.block.appendEffect(block = wipeBlock, effectBlock = linearWipe)
    // highlight-android-wipe

    // highlight-android-stroke
    // An ellipse mask in the "None" mode gives the stroke a path without cutting the block
    val mask = engine.block.createMask()
    engine.block.setShape(block = mask, shape = engine.block.createShape(ShapeType.Ellipse))
    engine.block.setEnum(block = mask, property = "mask/mode", value = "None")
    engine.block.appendMask(block = strokeBlock, maskBlock = mask)

    // Draw a hard white brush stroke along the mask, over the image
    val stroke = engine.block.createEffect(EffectType.Stroke)
    engine.block.setInt(block = stroke, property = "effect/stroke/maskIndex", value = 0)
    engine.block.setFloat(block = stroke, property = "effect/stroke/brushSize", value = 6F)
    engine.block.setFloat(block = stroke, property = "effect/stroke/hardness", value = 1F)
    engine.block.setColor(block = stroke, property = "effect/stroke/color", value = Color.fromRGBA(r = 1F, g = 1F, b = 1F))
    engine.block.setEnum(block = stroke, property = "effect/stroke/paintStyle", value = "OnOriginalImage")
    engine.block.appendEffect(block = strokeBlock, effectBlock = stroke)
    // highlight-android-stroke

    // highlight-android-effect-source
    // The map is a hidden image block. The effect still reads it.
    val map = addImageBlock(x = 0F, y = 0F)
    engine.block.setVisible(block = map, visible = false)

    val displacementMap = engine.block.createEffect(EffectType.DisplacementMap)
    engine.block.setEnum(
        block = displacementMap,
        property = "effect/displacement_map/horizontalChannel",
        value = "Luminance",
    )
    engine.block.setFloat(block = displacementMap, property = "effect/displacement_map/maxHorizontal", value = 20F)
    engine.block.setEnum(
        block = displacementMap,
        property = "effect/displacement_map/verticalChannel",
        value = "Luminance",
    )
    engine.block.setFloat(block = displacementMap, property = "effect/displacement_map/maxVertical", value = 20F)
    engine.block.setEnum(block = displacementMap, property = "effect/displacement_map/mapBehavior", value = "Stretch")
    engine.block.appendEffect(block = displacedBlock, effectBlock = displacementMap)

    // Set the map block as the source of the effect
    engine.block.setEffectSource(effectBlock = displacementMap, source = map)
    // highlight-android-effect-source

    // highlight-android-read-source
    val source = engine.block.getEffectSource(effectBlock = displacementMap)
    Log.i(TAG, "The source is the map: ${source == map}")
    // highlight-android-read-source

    // highlight-android-errors
    val errorCode = try {
        // Only the displacement map effect reads a source
        engine.block.setEffectSource(effectBlock = tritone, source = map)
        null
    } catch (error: EngineException) {
        // BLOCK.EFFECT_SOURCE_UNSUPPORTED
        error.code
    }
    // highlight-android-errors

    // highlight-android-export
    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    // highlight-android-export

    val effectTypes = { block: DesignBlock ->
        engine.block.getEffects(block).map { engine.block.getType(it) }
    }
    return ColorDistortAndWipeEffects(
        colorEffectTypes = effectTypes(colorBlock),
        distortEffectTypes = effectTypes(distortBlock),
        wipeEffectTypes = effectTypes(wipeBlock),
        strokeEffectTypes = effectTypes(strokeBlock),
        strokeMaskCount = engine.block.getMasks(strokeBlock).size,
        sourceMatchesMap = source == map,
        errorCode = errorCode,
        previewPng = previewPng,
    )
}
