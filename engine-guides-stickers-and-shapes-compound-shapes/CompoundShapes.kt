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
import ly.img.engine.ShapeType
import ly.img.engine.Vector2
import java.nio.ByteBuffer

private const val TAG = "CompoundShapes"

data class CompoundShapes(
    val subshapesAfterInsert: List<DesignBlock>,
    val subshapesAfterRemove: List<DesignBlock>,
    val removedSubshapeIsValid: Boolean,
    val mode: String,
    val fillRule: String,
    val errorCode: String?,
    val previewPng: ByteBuffer,
)

suspend fun compoundShapes(engine: Engine): CompoundShapes {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 800F)
    engine.block.setHeight(block = page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    // highlight-android-create-compound
    // A graphic block draws the outline of its compound shape with its fill
    val badge = engine.block.create(DesignBlockType.Graphic)
    val badgeFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = badgeFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.9F, a = 1F),
    )
    engine.block.setFill(block = badge, fill = badgeFill)
    engine.block.setWidth(block = badge, value = 300F)
    engine.block.setHeight(block = badge, value = 300F)
    engine.block.setPositionX(block = badge, value = 75F)
    engine.block.setPositionY(block = badge, value = 150F)
    engine.block.appendChild(parent = page, child = badge)

    val compound = engine.block.createShape(ShapeType.Compound)
    engine.block.setShape(block = badge, shape = compound)
    // highlight-android-create-compound

    // highlight-android-append-subshapes
    // The first subshape is the base. A new subshape fills the reference box.
    val circle = engine.block.createShape(ShapeType.Ellipse)
    engine.block.appendSubshape(block = compound, subshapeBlock = circle)

    val diamond = engine.block.createShape(ShapeType.Rect)
    engine.block.appendSubshape(block = compound, subshapeBlock = diamond)
    // highlight-android-append-subshapes

    // highlight-android-place-subshape
    // A 40 by 40 square, centered in the 100 by 100 reference box and turned
    // into a diamond. Rotation turns the box about its anchor.
    engine.block.setFloat(block = diamond, property = "shape/width", value = 40F)
    engine.block.setFloat(block = diamond, property = "shape/height", value = 40F)
    engine.block.setFloat(block = diamond, property = "shape/anchor/x", value = 20F)
    engine.block.setFloat(block = diamond, property = "shape/anchor/y", value = 20F)
    engine.block.setFloat(block = diamond, property = "shape/position/x", value = 50F)
    engine.block.setFloat(block = diamond, property = "shape/position/y", value = 50F)
    engine.block.setFloat(block = diamond, property = "shape/rotation", value = 45F)
    // highlight-android-place-subshape

    // highlight-android-combine-mode
    // Cut every later subshape out of the base circle
    engine.block.setEnum(block = compound, property = "shape/compound/mode", value = "Subtract")
    // highlight-android-combine-mode

    // highlight-android-reorder
    // Insert a small dot between the circle and the diamond
    val dot = engine.block.createShape(ShapeType.Ellipse)
    engine.block.setFloat(block = dot, property = "shape/width", value = 16F)
    engine.block.setFloat(block = dot, property = "shape/height", value = 16F)
    engine.block.setFloat(block = dot, property = "shape/position/x", value = 42F)
    engine.block.setFloat(block = dot, property = "shape/position/y", value = 8F)
    engine.block.insertSubshape(block = compound, subshapeBlock = dot, index = 1)
    // [circle, dot, diamond]
    val subshapesAfterInsert = engine.block.getSubshapes(compound)

    // Removing a subshape does not destroy it
    engine.block.removeSubshape(block = compound, index = 1)
    val removedSubshapeIsValid = engine.block.isValid(dot)
    Log.i(TAG, "$subshapesAfterInsert, removed subshape is valid: $removedSubshapeIsValid")
    engine.block.destroy(dot)
    // highlight-android-reorder
    val subshapesAfterRemove = engine.block.getSubshapes(compound)

    // highlight-android-fill-rule
    // Two overlapping squares in Merge mode. EvenOdd makes the overlap a hole.
    val squares = engine.block.create(DesignBlockType.Graphic)
    val squaresFill = engine.block.createFill(FillType.Color)
    engine.block.setColor(
        block = squaresFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.95F, g = 0.5F, b = 0.1F, a = 1F),
    )
    engine.block.setFill(block = squares, fill = squaresFill)
    engine.block.setWidth(block = squares, value = 300F)
    engine.block.setHeight(block = squares, value = 300F)
    engine.block.setPositionX(block = squares, value = 425F)
    engine.block.setPositionY(block = squares, value = 150F)
    engine.block.appendChild(parent = page, child = squares)

    val squaresCompound = engine.block.createShape(ShapeType.Compound)
    engine.block.setShape(block = squares, shape = squaresCompound)

    val firstSquare = engine.block.createShape(ShapeType.Rect)
    engine.block.setFloat(block = firstSquare, property = "shape/width", value = 60F)
    engine.block.setFloat(block = firstSquare, property = "shape/height", value = 60F)
    engine.block.appendSubshape(block = squaresCompound, subshapeBlock = firstSquare)

    val secondSquare = engine.block.createShape(ShapeType.Rect)
    engine.block.setFloat(block = secondSquare, property = "shape/width", value = 60F)
    engine.block.setFloat(block = secondSquare, property = "shape/height", value = 60F)
    engine.block.setFloat(block = secondSquare, property = "shape/position/x", value = 40F)
    engine.block.setFloat(block = secondSquare, property = "shape/position/y", value = 40F)
    engine.block.appendSubshape(block = squaresCompound, subshapeBlock = secondSquare)

    engine.block.setEnum(block = squaresCompound, property = "shape/compound/fillRule", value = "EvenOdd")
    // highlight-android-fill-rule

    // highlight-android-animate-subshape
    // Slide the second square to the top left corner and back over the
    // duration of the graphic block
    engine.block.setKeyframes(
        block = secondSquare,
        property = "shape/position",
        keyframes = listOf(
            Keyframe(time = 0F, value = KeyframeValue.Vec2(Vector2(40F, 40F))),
            Keyframe(time = 0.5F, value = KeyframeValue.Vec2(Vector2(0F, 0F))),
            Keyframe(time = 1F, value = KeyframeValue.Vec2(Vector2(40F, 40F))),
        ),
    )
    // highlight-android-animate-subshape

    // highlight-android-errors
    val line = engine.block.createShape(ShapeType.Line)
    val errorCode = try {
        engine.block.appendSubshape(block = compound, subshapeBlock = line)
        null
    } catch (error: EngineException) {
        // BLOCK.SUBSHAPE_TYPE_UNSUPPORTED
        error.code
    }
    engine.block.destroy(line)
    // highlight-android-errors

    // highlight-android-export
    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    // highlight-android-export

    return CompoundShapes(
        subshapesAfterInsert = subshapesAfterInsert,
        subshapesAfterRemove = subshapesAfterRemove,
        removedSubshapeIsValid = removedSubshapeIsValid,
        mode = engine.block.getEnum(block = compound, property = "shape/compound/mode"),
        fillRule = engine.block.getEnum(block = squaresCompound, property = "shape/compound/fillRule"),
        errorCode = errorCode,
        previewPng = previewPng,
    )
}
