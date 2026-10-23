import android.util.Log
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.EngineException
import ly.img.engine.FillType
import ly.img.engine.Keyframe
import ly.img.engine.KeyframeValue
import ly.img.engine.MimeType
import ly.img.engine.RGBAColor
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import java.nio.ByteBuffer
import kotlin.math.PI

private const val TAG = "TransformParent"

data class TransformParent(
    val pivot: DesignBlock,
    val planetTransformParent: DesignBlock?,
    val moonOpacity: Float,
    val moonParent: DesignBlock?,
    val page: DesignBlock,
    val planetX: Float,
    val planetY: Float,
    val labelTransformParent: DesignBlock?,
    val cycleErrorCode: String?,
    val unsupportedErrorCode: String?,
    val previewPng: ByteBuffer,
)

suspend fun transformParent(engine: Engine): TransformParent {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 1920F)
    engine.block.setHeight(block = page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setDuration(block = page, duration = 5.0)

    // highlight-android-create-circle
    // Creates a circle graphic of the given size and color on the page
    fun createCircle(
        size: Float,
        color: RGBAColor,
    ): DesignBlock {
        val block = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block = block, shape = engine.block.createShape(ShapeType.Ellipse))
        engine.block.setFill(block = block, fill = engine.block.createFill(FillType.Color))
        engine.block.setFillSolidColor(block = block, color = color)
        engine.block.setWidth(block = block, value = size)
        engine.block.setHeight(block = block, value = size)
        engine.block.appendChild(parent = page, child = block)
        engine.block.setDuration(block = block, duration = 5.0)
        return block
    }
    // highlight-android-create-circle

    // highlight-android-hidden-parent
    val pivot = createCircle(size = 20F, color = Color.fromRGBA(r = 0F, g = 0F, b = 0F, a = 1F))
    engine.block.setPositionX(block = pivot, value = 950F)
    engine.block.setPositionY(block = pivot, value = 530F)
    engine.block.setFloat(block = pivot, property = "transform/anchor/x", value = 10F)
    engine.block.setFloat(block = pivot, property = "transform/anchor/y", value = 10F)
    engine.block.setKeyframes(
        block = pivot,
        property = "rotation",
        keyframes = listOf(
            Keyframe(time = 0F, value = KeyframeValue.Float(0F)),
            Keyframe(time = 1F, value = KeyframeValue.Float(2F * PI.toFloat())),
        ),
    )
    engine.block.setVisible(block = pivot, visible = false)
    // highlight-android-hidden-parent

    // highlight-android-link
    val planet = createCircle(size = 120F, color = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.9F, a = 1F))
    engine.block.setTransformParent(block = planet, parent = pivot)
    // The pivot
    val planetTransformParent = engine.block.getTransformParent(block = planet)
    // The position is now an offset from the pivot's top-left corner,
    // in the pivot's turning space
    engine.block.setPositionX(block = planet, value = 300F)
    engine.block.setPositionY(block = planet, value = -50F)
    // highlight-android-link

    // highlight-android-chain
    // The planet spins twice about its center while it circles the pivot
    engine.block.setFloat(block = planet, property = "transform/anchor/x", value = 60F)
    engine.block.setFloat(block = planet, property = "transform/anchor/y", value = 60F)
    engine.block.setKeyframes(
        block = planet,
        property = "rotation",
        keyframes = listOf(
            Keyframe(time = 0F, value = KeyframeValue.Float(0F)),
            Keyframe(time = 1F, value = KeyframeValue.Float(4F * PI.toFloat())),
        ),
    )

    // The moon follows the planet, which follows the pivot
    val moon = createCircle(size = 40F, color = Color.fromRGBA(r = 0.85F, g = 0.85F, b = 0.8F, a = 1F))
    engine.block.setTransformParent(block = moon, parent = planet)
    engine.block.setPositionX(block = moon, value = 160F)
    engine.block.setPositionY(block = moon, value = 40F)

    // A text label follows the planet, too
    val label = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(block = label, text = "Planet")
    engine.block.setTextFontSize(block = label, fontSize = 36F)
    engine.block.setWidthMode(block = label, mode = SizeMode.AUTO)
    engine.block.setHeightMode(block = label, mode = SizeMode.AUTO)
    engine.block.appendChild(parent = page, child = label)
    engine.block.setTransformParent(block = label, parent = planet)
    engine.block.setPositionX(block = label, value = 0F)
    engine.block.setPositionY(block = label, value = 140F)
    // highlight-android-chain

    // highlight-android-transform-only
    engine.block.setOpacity(block = planet, value = 0.5F)
    // 1.0
    val moonOpacity = engine.block.getOpacity(block = moon)
    // The page
    val moonParent = engine.block.getParent(block = moon)
    // highlight-android-transform-only

    // highlight-android-read-position
    // A quarter of the way through, the pivot has turned by 90 degrees
    engine.block.setPlaybackTime(block = page, time = 1.25)
    // 900, 830
    val planetX = engine.block.getGlobalBoundingBoxX(block = planet)
    val planetY = engine.block.getGlobalBoundingBoxY(block = planet)
    Log.i(TAG, "planet at $planetX, $planetY")
    // highlight-android-read-position

    // highlight-android-unlink
    // The label's position is read in page space again, so place it there
    engine.block.setTransformParent(block = label, parent = null)
    // null
    val labelTransformParent = engine.block.getTransformParent(block = label)
    engine.block.setPositionX(block = label, value = 80F)
    engine.block.setPositionY(block = label, value = 80F)
    // highlight-android-unlink

    // highlight-android-errors
    val cycleErrorCode = try {
        // The pivot cannot follow a block that follows it
        engine.block.setTransformParent(block = pivot, parent = moon)
        null
    } catch (error: EngineException) {
        // BLOCK.TRANSFORM_PARENT_CYCLE
        error.code
    }
    val unsupportedErrorCode = try {
        // Only graphic and text blocks take part in a link
        engine.block.setTransformParent(block = planet, parent = page)
        null
    } catch (error: EngineException) {
        // BLOCK.TRANSFORM_PARENT_UNSUPPORTED
        error.code
    }
    // highlight-android-errors

    // highlight-android-export
    // The playback time is still 1.25 seconds, so the frame shows the planet below the pivot
    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    // highlight-android-export

    return TransformParent(
        pivot = pivot,
        planetTransformParent = planetTransformParent,
        moonOpacity = moonOpacity,
        moonParent = moonParent,
        page = page,
        planetX = planetX,
        planetY = planetY,
        labelTransformParent = labelTransformParent,
        cycleErrorCode = cycleErrorCode,
        unsupportedErrorCode = unsupportedErrorCode,
        previewPng = previewPng,
    )
}
