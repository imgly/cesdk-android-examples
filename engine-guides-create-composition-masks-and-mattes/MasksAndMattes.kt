import android.util.Log
import ly.img.engine.BlockMatte
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.EngineException
import ly.img.engine.FillType
import ly.img.engine.GradientColorStop
import ly.img.engine.MatteMode
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

private const val TAG = "MasksAndMattes"

data class MasksAndMattes(
    val graphicSupportsMasks: Boolean,
    val pageSupportsMasks: Boolean,
    val masks: List<DesignBlock>,
    val maskCountAfterRemove: Int,
    val removedMaskIsValid: Boolean,
    val masksAfterInsert: List<DesignBlock>,
    val matte: BlockMatte?,
    val source: DesignBlock,
    val ringMatte: BlockMatte?,
    val errorCode: String?,
    val previewPng: ByteBuffer,
    val svgByteCount: Int,
)

suspend fun masksAndMattes(engine: Engine): MasksAndMattes {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 1500F)
    engine.block.setHeight(block = page, value = 500F)
    engine.block.appendChild(parent = scene, child = page)

    // A 400 by 400 square graphic at a given x position
    fun addSquare(
        x: Float,
        fill: DesignBlock,
    ): DesignBlock {
        val block = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block = block, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setFill(block = block, fill = fill)
        engine.block.setWidth(block = block, value = 400F)
        engine.block.setHeight(block = block, value = 400F)
        engine.block.setPositionX(block = block, value = x)
        engine.block.setPositionY(block = block, value = 50F)
        engine.block.appendChild(parent = page, child = block)
        return block
    }

    // highlight-android-create-mask
    val purple = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = purple,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.5F, g = 0.2F, b = 0.9F, a = 1F),
    )
    val ringBlock = addSquare(x = 50F, fill = purple)

    val graphicSupportsMasks = engine.block.supportsMasks(block = ringBlock) // true
    val pageSupportsMasks = engine.block.supportsMasks(block = page) // false

    // The ellipse fills the frame of the masked block
    val outerMask = engine.block.createMask()
    engine.block.setShape(block = outerMask, shape = engine.block.createShape(ShapeType.Ellipse))
    engine.block.appendMask(block = ringBlock, maskBlock = outerMask)
    // highlight-android-create-mask

    // highlight-android-mask-properties
    // Soften the edge by 20 units and let 80% of the block show through
    engine.block.setFloat(block = outerMask, property = "mask/feather", value = 20F)
    engine.block.setFloat(block = outerMask, property = "mask/opacity", value = 0.8F)
    // highlight-android-mask-properties

    // highlight-android-combine-masks
    // Shrink a second ellipse by 100 units and subtract it to cut a ring
    val innerMask = engine.block.createMask()
    engine.block.setShape(block = innerMask, shape = engine.block.createShape(ShapeType.Ellipse))
    engine.block.setFloat(block = innerMask, property = "mask/expansion", value = -100F)
    engine.block.setEnum(block = innerMask, property = "mask/mode", value = "Subtract")
    engine.block.appendMask(block = ringBlock, maskBlock = innerMask)
    // highlight-android-combine-masks

    // highlight-android-invert-mask
    val teal = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = teal,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0F, g = 0.6F, b = 0.6F, a = 1F),
    )
    val starBlock = addSquare(x = 550F, fill = teal)

    // An inverted mask keeps the area outside its shape: a star-shaped hole
    val starMask = engine.block.createMask()
    engine.block.setShape(block = starMask, shape = engine.block.createShape(ShapeType.Star))
    engine.block.setBoolean(block = starMask, property = "mask/inverted", value = true)
    engine.block.appendMask(block = starBlock, maskBlock = starMask)
    // highlight-android-invert-mask

    // highlight-android-manage-masks
    // [outerMask, innerMask]
    val masks = engine.block.getMasks(block = ringBlock)

    // Removing a mask does not destroy it
    engine.block.removeMask(block = ringBlock, index = 1)
    val maskCountAfterRemove = engine.block.getMasks(block = ringBlock).size
    val removedMaskIsValid = engine.block.isValid(block = innerMask) // true

    // Insert it again after the outer mask
    engine.block.insertMask(block = ringBlock, maskBlock = innerMask, index = 1)
    // highlight-android-manage-masks
    val masksAfterInsert = engine.block.getMasks(block = ringBlock)
    Log.i(TAG, "masks: $masks, after remove: $maskCountAfterRemove, valid: $removedMaskIsValid")

    // highlight-android-set-matte
    val orange = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = orange,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 1F, g = 0.5F, b = 0F, a = 1F),
    )
    val target = addSquare(x = 1050F, fill = orange)

    // The matte source: a gradient from white at the top to black at the bottom
    val gradient = engine.block.createFill(FillType.LinearGradient)
    engine.block.setGradientColorStops(
        block = gradient,
        property = "fill/gradient/colors",
        colorStops = listOf(
            GradientColorStop(stop = 0F, color = Color.fromRGBA(r = 1F, g = 1F, b = 1F, a = 1F)),
            GradientColorStop(stop = 1F, color = Color.fromRGBA(r = 0F, g = 0F, b = 0F, a = 1F)),
        ),
    )
    val source = addSquare(x = 1050F, fill = gradient)

    // The target shows where the source is bright. The source is not drawn.
    engine.block.setMatte(block = target, source = source, mode = MatteMode.LUMA)
    // highlight-android-set-matte

    // highlight-android-read-matte
    // BlockMatte(source = source, mode = LUMA)
    val matte = engine.block.getMatte(block = target)
    // null
    val ringMatte = engine.block.getMatte(block = ringBlock)
    Log.i(TAG, "matte: $matte, ring matte: $ringMatte")
    // highlight-android-read-matte

    // highlight-android-matte-rules
    val errorCode = try {
        // A block cannot use itself as its matte source
        engine.block.setMatte(block = target, source = target)
        null
    } catch (error: EngineException) {
        // BLOCK.MATTE_SOURCE_INVALID
        error.code
    }
    // highlight-android-matte-rules

    // highlight-android-export
    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)

    // SVG export ignores the mask opacity and feather, and draws the matted block whole
    val svg = engine.block.export(block = page, mimeType = MimeType.SVG)
    // highlight-android-export

    return MasksAndMattes(
        graphicSupportsMasks = graphicSupportsMasks,
        pageSupportsMasks = pageSupportsMasks,
        masks = masks,
        maskCountAfterRemove = maskCountAfterRemove,
        removedMaskIsValid = removedMaskIsValid,
        masksAfterInsert = masksAfterInsert,
        matte = matte,
        source = source,
        ringMatte = ringMatte,
        errorCode = errorCode,
        previewPng = previewPng,
        svgByteCount = svg.remaining(),
    )
}
