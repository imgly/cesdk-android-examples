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
import ly.img.engine.Vector2
import java.nio.ByteBuffer
import kotlin.math.PI

data class AnchorSkewAutoOrient(
    val centerAnchorX: Float,
    val cornerHasAnchor: Boolean,
    val signSkew: Float,
    val arrowAutoOrient: Boolean,
    val anchorErrorCode: String?,
    val autoOrientErrorCode: String?,
    val previewPng: ByteBuffer,
)

suspend fun anchorSkewAutoOrient(engine: Engine): AnchorSkewAutoOrient {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 1920F)
    engine.block.setHeight(block = page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setDuration(block = page, duration = 5.0)

    // highlight-android-create-rect
    // Creates a rect graphic with a color fill on the page
    fun createRect(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: RGBAColor,
    ): DesignBlock {
        val block = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block = block, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setFill(block = block, fill = engine.block.createFill(FillType.Color))
        engine.block.setFillSolidColor(block = block, color = color)
        engine.block.setWidth(block = block, value = width)
        engine.block.setHeight(block = block, value = height)
        engine.block.setPositionX(block = block, value = x)
        engine.block.setPositionY(block = block, value = y)
        engine.block.appendChild(parent = page, child = block)
        engine.block.setDuration(block = block, duration = 5.0)
        return block
    }
    // highlight-android-create-rect

    // highlight-android-anchor
    val cornerSquare = createRect(300F, 200F, 160F, 160F, Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.9F, a = 1F))
    val centerSquare = createRect(700F, 200F, 160F, 160F, Color.fromRGBA(r = 0.9F, g = 0.3F, b = 0.4F, a = 1F))
    // Without an anchor, a rotation track turns a block about its center, so
    // the left square sets its anchor to its top-left corner
    engine.block.setFloat(block = cornerSquare, property = "transform/anchor/x", value = 0F)
    engine.block.setFloat(block = cornerSquare, property = "transform/anchor/y", value = 0F)
    engine.block.setFloat(block = centerSquare, property = "transform/anchor/x", value = 80F)
    engine.block.setFloat(block = centerSquare, property = "transform/anchor/y", value = 80F)
    for (square in listOf(cornerSquare, centerSquare)) {
        engine.block.setKeyframes(
            block = square,
            property = "rotation",
            keyframes = listOf(
                Keyframe(time = 0F, value = KeyframeValue.Float(0F)),
                Keyframe(time = 1F, value = KeyframeValue.Float(PI.toFloat() / 2F)),
            ),
        )
    }
    // 80.0
    val centerAnchorX = engine.block.getFloat(block = centerSquare, property = "transform/anchor/x")
    // highlight-android-anchor

    // highlight-android-has-anchor
    // An anchor set to 0 is still an anchor
    // true
    val cornerHasAnchor = engine.block.hasTransformAnchor(block = cornerSquare)
    // highlight-android-has-anchor

    // highlight-android-skew
    val sign = createRect(1150F, 200F, 240F, 160F, Color.fromRGBA(r = 0.95F, g = 0.7F, b = 0.1F, a = 1F))
    engine.block.setFloat(block = sign, property = "transform/skew", value = 20F)
    engine.block.setFloat(block = sign, property = "transform/skewAxis", value = 0F)
    // highlight-android-skew

    // highlight-android-animate-skew
    // The anchor sits at the bottom center, so the base of the blade stays put
    val blade = createRect(1600F, 600F, 40F, 300F, Color.fromRGBA(r = 0.2F, g = 0.7F, b = 0.3F, a = 1F))
    engine.block.setFloat(block = blade, property = "transform/anchor/x", value = 20F)
    engine.block.setFloat(block = blade, property = "transform/anchor/y", value = 300F)
    engine.block.setKeyframes(
        block = blade,
        property = "transform/skew",
        keyframes = listOf(
            Keyframe(time = 0F, value = KeyframeValue.Float(-20F)),
            Keyframe(time = 0.5F, value = KeyframeValue.Float(20F)),
            Keyframe(time = 1F, value = KeyframeValue.Float(-20F)),
        ),
    )
    // highlight-android-animate-skew

    // highlight-android-auto-orient
    val arrow = createRect(300F, 800F, 160F, 40F, Color.fromRGBA(r = 0.5F, g = 0.2F, b = 0.8F, a = 1F))
    engine.block.setFloat(block = arrow, property = "transform/anchor/x", value = 80F)
    engine.block.setFloat(block = arrow, property = "transform/anchor/y", value = 20F)
    engine.block.setVec2(block = arrow, property = "position", value = Vector2(300F, 800F))
    engine.block.setKeyframes(
        block = arrow,
        property = "position",
        keyframes = listOf(
            Keyframe(
                time = 0F,
                value = KeyframeValue.Vec2(Vector2(300F, 800F)),
                spatialOut = Vector2(300F, -300F),
                spatialIn = Vector2(-300F, -300F),
            ),
            Keyframe(time = 1F, value = KeyframeValue.Vec2(Vector2(1300F, 800F))),
        ),
    )
    engine.block.setBoolean(block = arrow, property = "transform/autoOrient", value = true)
    // true
    val arrowAutoOrient = engine.block.getBoolean(block = arrow, property = "transform/autoOrient")
    // highlight-android-auto-orient

    // highlight-android-errors
    val anchorErrorCode = try {
        // A page has no anchor and no skew
        engine.block.setFloat(block = page, property = "transform/anchor/x", value = 10F)
        null
    } catch (error: EngineException) {
        // BLOCK.TYPE_CANNOT_HAVE_ANCHOR_OR_SKEW
        error.code
    }
    val autoOrientErrorCode = try {
        engine.block.setBoolean(block = page, property = "transform/autoOrient", value = true)
        null
    } catch (error: EngineException) {
        // BLOCK.TYPE_CANNOT_AUTO_ORIENT
        error.code
    }
    // highlight-android-errors

    // highlight-android-export
    // Export the frame at 1.5 seconds, while the arrow climbs its path
    engine.block.setPlaybackTime(block = page, time = 1.5)
    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    // highlight-android-export

    return AnchorSkewAutoOrient(
        centerAnchorX = centerAnchorX,
        cornerHasAnchor = cornerHasAnchor,
        signSkew = engine.block.getFloat(block = sign, property = "transform/skew"),
        arrowAutoOrient = arrowAutoOrient,
        anchorErrorCode = anchorErrorCode,
        autoOrientErrorCode = autoOrientErrorCode,
        previewPng = previewPng,
    )
}
