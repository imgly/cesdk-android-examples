package ly.img.editor.base.engine

import ly.img.editor.core.ui.engine.Scope
import ly.img.engine.BlockApi
import ly.img.engine.BlurType
import ly.img.engine.DesignBlock
import ly.img.engine.EffectType
import ly.img.engine.FillType
import ly.img.engine.GradientColorStop
import ly.img.engine.RGBAColor

const val NoneDesignBlock: DesignBlock = -1

fun BlockApi.getFillType(designBlock: DesignBlock): FillType? {
    return if (!this.hasFill(designBlock)) {
        null
    } else {
        FillType.get(this.getType(this.getFill(designBlock)))
    }
}

fun BlockApi.setFillType(
    designBlock: DesignBlock,
    fillType: FillType,
): DesignBlock {
    val oldFill =
        if (this.hasFill(designBlock)) {
            this.getType(block = designBlock)
            this.getFill(designBlock).takeIf { it != NoneDesignBlock }
        } else {
            null
        }

    return if (oldFill != null && this.getFillType(designBlock) == fillType) {
        oldFill
    } else {
        val newFill = this.createFill(fillType)
        this.setFill(designBlock, newFill)
        if (oldFill != null) {
            this.setScopeEnabled(oldFill, Scope.LifecycleDestroy, true)
            this.destroy(oldFill)
        }
        newFill
    }
}

fun BlockApi.setBlurType(
    designBlock: DesignBlock,
    type: BlurType?,
): DesignBlock? {
    val oldBlur =
        if (this.hasBlur(designBlock)) {
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
                this.setScopeEnabled(oldBlur, Scope.LifecycleDestroy, true)
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

fun BlockApi.getFillInfo(designBlock: DesignBlock): Fill? {
    return if (!this.hasFill(designBlock)) {
        null
    } else {
        when (this.getFillType(designBlock)) {
            FillType.Color -> {
                val rgbaColor = this.getColor(designBlock, "fill/solid/color") as RGBAColor
                SolidFill(rgbaColor.toComposeColor())
            }

            FillType.LinearGradient -> {
                val fill = this.getFill(designBlock)
                LinearGradientFill(
                    startPointX = this.getFloat(fill, "fill/gradient/linear/startPointX"),
                    startPointY = this.getFloat(fill, "fill/gradient/linear/startPointY"),
                    endPointX = this.getFloat(fill, "fill/gradient/linear/endPointX"),
                    endPointY = this.getFloat(fill, "fill/gradient/linear/endPointY"),
                    colorStops = this.getGradientColorStops(fill, "fill/gradient/colors"),
                )
            }

            FillType.RadialGradient -> {
                val fill = this.getFill(designBlock)
                RadialGradientFill(
                    centerX = this.getFloat(fill, "fill/gradient/radial/centerPointX"),
                    centerY = this.getFloat(fill, "fill/gradient/radial/centerPointY"),
                    radius = this.getFloat(fill, "fill/gradient/radial/radius"),
                    colorStops = this.getGradientColorStops(fill, "fill/gradient/colors"),
                )
            }

            FillType.ConicalGradient -> {
                val fill = this.getFill(designBlock)
                ConicalGradientFill(
                    centerX = this.getFloat(fill, "fill/gradient/conical/centerPointX"),
                    centerY = this.getFloat(fill, "fill/gradient/conical/centerPointY"),
                    colorStops = this.getGradientColorStops(fill, "fill/gradient/colors"),
                )
            }

            // Image fill and Video fill are not supported yet
            else -> null
        }
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
