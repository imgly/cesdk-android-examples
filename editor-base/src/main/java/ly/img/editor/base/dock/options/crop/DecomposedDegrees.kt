package ly.img.editor.base.dock.options.crop

import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import kotlin.math.ceil
import kotlin.math.floor

fun getNormalizedDegrees(
    engine: Engine,
    designBlock: DesignBlock,
    offset: Int = 0,
): Float {
    val cropRotationDegrees = engine.block.getCropRotation(designBlock) * (180f / Math.PI.toFloat())
    val cropRotatedDegrees = cropRotationDegrees + offset
    var normalizedDegrees = cropRotatedDegrees % 360
    if (normalizedDegrees < 0) normalizedDegrees += 360
    return normalizedDegrees
}

fun getRotationDegrees(
    engine: Engine,
    designBlock: DesignBlock,
): Float {
    return getDecomposedDegrees(engine, designBlock).first
}

fun getStraightenDegrees(
    engine: Engine,
    designBlock: DesignBlock,
): Float {
    return getDecomposedDegrees(engine, designBlock).second
}

private fun getDecomposedDegrees(
    engine: Engine,
    designBlock: DesignBlock,
): Pair<Float, Float> {
    val normalizedDegrees = getNormalizedDegrees(engine, designBlock)
    var rotationCounts = (normalizedDegrees / 90).roundToZero()
    var rotationDeg = 0f

    fun straightenDegrees(): Float {
        rotationDeg = rotationCounts * 90
        return normalizedDegrees - rotationDeg
    }

    var straightenDeg = straightenDegrees()
    if (straightenDeg > 45) {
        rotationCounts += 1
        straightenDeg = straightenDegrees()
    }
    return rotationDeg to straightenDeg
}

private fun Float.roundToZero(): Float {
    return if (this > 0) floor(this) else ceil(this)
}
