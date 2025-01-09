package ly.img.editor.base.engine

import ly.img.editor.core.ui.engine.getFillType
import ly.img.editor.core.ui.engine.isBackgroundTrack
import ly.img.engine.BlockApi
import ly.img.engine.BlurType
import ly.img.engine.DesignBlock
import ly.img.engine.EffectType
import ly.img.engine.FillType
import ly.img.engine.GradientColorStop
import kotlin.math.min

const val NoneDesignBlock: DesignBlock = -1

fun BlockApi.setFillType(
    designBlock: DesignBlock,
    fillType: FillType,
): DesignBlock {
    val oldFill =
        if (this.supportsFill(designBlock)) {
            this.getType(block = designBlock)
            this.getFill(designBlock).takeIf { it != NoneDesignBlock }
        } else {
            null
        }

    return if (oldFill != null && this.getFillType(designBlock) == fillType) {
        oldFill
    } else {
        val newFill = this.createFill(fillType)
        if (oldFill != null) {
            this.destroy(oldFill)
        }
        this.setFill(designBlock, newFill)
        newFill
    }
}

fun BlockApi.setBlurType(
    designBlock: DesignBlock,
    type: BlurType?,
): DesignBlock? {
    val oldBlur =
        if (this.supportsBlur(designBlock)) {
            this.getBlur(designBlock).takeIf { it != NoneDesignBlock }
        } else {
            null
        }

    if (type == null) {
        this.setBlurEnabled(designBlock, false)
        return null
    } else {
        return if (oldBlur != null && this.getType(oldBlur).endsWith(type.key)) {
            oldBlur
        } else {
            val newBlur = this.createBlur(type)
            this.setBlur(designBlock, newBlur)
            if (oldBlur != null) {
                this.destroy(oldBlur)
            }
            newBlur
        }.also {
            this.setBlurEnabled(designBlock, true)
        }
    }
}

fun BlockApi.setLinearGradientFill(
    designBlock: DesignBlock,
    startPointX: Float,
    startPointY: Float,
    endPointX: Float,
    endPointY: Float,
    colorStops: List<GradientColorStop>? = null,
) {
    val fill = this.setFillType(designBlock, FillType.LinearGradient)

    this.setFloat(fill, property = "fill/gradient/linear/startPointX", value = startPointX)
    this.setFloat(fill, property = "fill/gradient/linear/startPointY", value = startPointY)
    this.setFloat(fill, property = "fill/gradient/linear/endPointX", value = endPointX)
    this.setFloat(fill, property = "fill/gradient/linear/endPointY", value = endPointY)
    if (colorStops != null) {
        this.setGradientColorStops(fill, property = "fill/gradient/colors", colorStops = colorStops)
    }
}

fun BlockApi.setRadialGradientFill(
    designBlock: DesignBlock,
    centerPointX: Float,
    centerPointY: Float,
    radius: Float,
    colorStops: List<GradientColorStop>? = null,
) {
    val fill = this.setFillType(designBlock, FillType.RadialGradient)

    this.setFloat(fill, "fill/gradient/radial/centerPointX", value = centerPointX)
    this.setFloat(fill, "fill/gradient/radial/centerPointY", value = centerPointY)
    this.setFloat(fill, "fill/gradient/radial/radius", value = radius)
    if (colorStops != null) {
        this.setGradientColorStops(fill, property = "fill/gradient/colors", colorStops = colorStops)
    }
}

fun BlockApi.setConicalGradientFill(
    designBlock: DesignBlock,
    centerPointX: Float,
    centerPointY: Float,
    colorStops: List<GradientColorStop>? = null,
) {
    val fill = this.setFillType(designBlock, FillType.ConicalGradient)

    this.setFloat(fill, "fill/gradient/conical/centerPointX", value = centerPointX)
    this.setFloat(fill, "fill/gradient/conical/centerPointY", value = centerPointY)
    if (colorStops != null) {
        this.setGradientColorStops(fill, property = "fill/gradient/colors", colorStops = colorStops)
    }
}

fun BlockApi.findEffect(
    block: DesignBlock,
    type: EffectType,
) = this.getEffects(block).find { effect ->
    this.getType(effect).endsWith(type.key, ignoreCase = true)
}

fun BlockApi.getEffectOrCreateAndAppend(
    block: DesignBlock,
    type: EffectType,
) = this.findEffect(block, type)
    ?: this.createEffect(type).also { effect ->
        this.appendEffect(block, effect)
    }

fun BlockApi.removeEffectByType(
    block: DesignBlock,
    type: EffectType,
) {
    val filterId =
        this.getEffects(block).indexOfFirst { effect ->
            this.getType(effect).endsWith(type.key, ignoreCase = true)
        }
    if (filterId != -1) {
        this.removeEffect(block, filterId)
    }
}

/**
 * An extension function for checking whether the parent of the [designBlock] is a background track.
 * IMPORTANT! When modifying this function also modify similar function in InspectorBarExt.kt.
 *
 * @param designBlock the design block that is being queried.
 * @return true if the parent is a background track, false otherwise.
 */
fun BlockApi.isParentBackgroundTrack(designBlock: DesignBlock): Boolean {
    val parent = getParent(designBlock)
    return parent != null && isBackgroundTrack(parent)
}

fun BlockApi.getAspectRatio(designBlock: DesignBlock): Float {
    val width = getFrameWidth(designBlock)
    val height = getFrameHeight(designBlock)
    return width / height
}

fun BlockApi.getSmallerSide(block: DesignBlock): Float {
    val height = getHeight(block)
    val width = getWidth(block)
    return min(width, height)
}
