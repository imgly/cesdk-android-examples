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

data class StrokeEffect(
    val overImageEffectTypes: List<String>,
    val maskCounts: List<Int>,
    val maskMode: String,
    val transparentPaintStyle: String,
    val halfStrokeEnd: Float,
    val strokeProperties: List<String>,
    val previewPng: ByteBuffer,
)

suspend fun strokeEffect(engine: Engine): StrokeEffect {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 1180F)
    engine.block.setHeight(block = page, value = 330F)
    engine.block.appendChild(parent = scene, child = page)

    // Three copies of one image, side by side
    val imageUri = defaultBaseUri.buildUpon()
        .appendPath("ly.img.image")
        .appendPath("images")
        .appendPath("sample_1.jpg")
        .build()
    val (overImageBlock, transparentBlock, halfStrokeBlock) = listOf(30F, 410F, 790F).map { x ->
        val block = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block = block, shape = engine.block.createShape(ShapeType.Rect))
        val fill = engine.block.createFill(FillType.Image)
        engine.block.setUri(block = fill, property = "fill/image/imageFileURI", value = imageUri)
        engine.block.setFill(block = block, fill = fill)
        engine.block.setWidth(block = block, value = 360F)
        engine.block.setHeight(block = block, value = 270F)
        engine.block.setPositionX(block = block, value = x)
        engine.block.setPositionY(block = block, value = 30F)
        engine.block.appendChild(parent = page, child = block)
        block
    }

    // highlight-android-add-mask
    // An ellipse mask in the "None" mode gives the stroke a path without cutting the block
    fun addEllipseMask(block: DesignBlock) {
        val mask = engine.block.createMask()
        engine.block.setShape(block = mask, shape = engine.block.createShape(ShapeType.Ellipse))
        engine.block.setEnum(block = mask, property = "mask/mode", value = "None")
        engine.block.appendMask(block = block, maskBlock = mask)
    }

    addEllipseMask(overImageBlock)
    addEllipseMask(transparentBlock)
    addEllipseMask(halfStrokeBlock)
    // highlight-android-add-mask

    // highlight-android-draw-stroke
    // A hard yellow brush stroke, 12 design units wide, along the first mask
    val stroke = engine.block.createEffect(EffectType.Stroke)
    engine.block.setColor(block = stroke, property = "effect/stroke/color", value = Color.fromRGBA(r = 1F, g = 0.8F, b = 0.1F))
    engine.block.setFloat(block = stroke, property = "effect/stroke/brushSize", value = 6F)
    engine.block.setFloat(block = stroke, property = "effect/stroke/hardness", value = 1F)
    engine.block.appendEffect(block = overImageBlock, effectBlock = stroke)
    // highlight-android-draw-stroke

    // highlight-android-paint-style
    // Stroke every mask of the block, and draw only the stroke without the image
    val transparentStroke = engine.block.createEffect(EffectType.Stroke)
    engine.block.setBoolean(block = transparentStroke, property = "effect/stroke/allMasks", value = true)
    engine.block.setEnum(block = transparentStroke, property = "effect/stroke/paintStyle", value = "OnTransparent")
    engine.block.setColor(block = transparentStroke, property = "effect/stroke/color", value = Color.fromRGBA(r = 1F, g = 0.8F, b = 0.1F))
    engine.block.setFloat(block = transparentStroke, property = "effect/stroke/brushSize", value = 6F)
    engine.block.appendEffect(block = transparentBlock, effectBlock = transparentStroke)
    // highlight-android-paint-style

    // highlight-android-trim
    // Draw only the first half of the mask path
    val halfStroke = engine.block.createEffect(EffectType.Stroke)
    engine.block.setInt(block = halfStroke, property = "effect/stroke/maskIndex", value = 0)
    engine.block.setFloat(block = halfStroke, property = "effect/stroke/start", value = 0F)
    engine.block.setFloat(block = halfStroke, property = "effect/stroke/end", value = 0.5F)
    engine.block.setColor(block = halfStroke, property = "effect/stroke/color", value = Color.fromRGBA(r = 1F, g = 0.8F, b = 0.1F))
    engine.block.setFloat(block = halfStroke, property = "effect/stroke/brushSize", value = 6F)
    engine.block.appendEffect(block = halfStrokeBlock, effectBlock = halfStroke)
    // highlight-android-trim

    // highlight-android-export
    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    // highlight-android-export

    val blocks = listOf(overImageBlock, transparentBlock, halfStrokeBlock)
    return StrokeEffect(
        overImageEffectTypes = engine.block.getEffects(overImageBlock).map { engine.block.getType(it) },
        maskCounts = blocks.map { engine.block.getMasks(it).size },
        maskMode = engine.block.getEnum(block = engine.block.getMasks(overImageBlock).first(), property = "mask/mode"),
        transparentPaintStyle = engine.block.getEnum(block = transparentStroke, property = "effect/stroke/paintStyle"),
        halfStrokeEnd = engine.block.getFloat(block = halfStroke, property = "effect/stroke/end"),
        strokeProperties = engine.block.findAllProperties(stroke),
        previewPng = previewPng,
    )
}
