package ly.img.cesdk.engine

import ly.img.cesdk.core.engine.Scope
import ly.img.engine.BlockApi
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.GradientColorStop
import ly.img.engine.RGBAColor

fun BlockApi.getFillType(designBlock: DesignBlock): DesignBlockType? {
    return if (!this.hasFill(designBlock)) null
    else DesignBlockType.values().find {
        it.key == this.getType(this.getFill(designBlock))
    }
}

fun BlockApi.setFillType(designBlock: DesignBlock, type: String) : DesignBlock {
    val oldFill = if (this.hasFill(designBlock)) {
        this.getFill(designBlock)
    } else null

    return if (oldFill != null && this.getType(oldFill).endsWith(type)) {
        oldFill
    } else {
        val newFill = this.createFill(type)
        this.setFill(designBlock, newFill)
        if (oldFill != null) {
            this.setScopeEnabled(oldFill, Scope.LifecycleDestroy, true)
            this.destroy(oldFill)
        }
        newFill
    }
}

fun BlockApi.setLinearGradientFill(
    designBlock: DesignBlock,
    startPointX: Float,
    startPointY: Float,
    endPointX: Float,
    endPointY: Float,
    colorStops: List<GradientColorStop>? = null
) {

    val fill = this.setFillType(designBlock, "gradient/linear")

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
    colorStops: List<GradientColorStop>? = null
) {
    val fill = this.setFillType(designBlock, "gradient/radial")

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
    colorStops: List<GradientColorStop>? = null
) {
    val fill = this.setFillType(designBlock, "gradient/conical")

    this.setFloat(fill, "fill/gradient/conical/centerPointX", value = centerPointX)
    this.setFloat(fill, "fill/gradient/conical/centerPointY", value = centerPointY)
    if (colorStops != null) {
        this.setGradientColorStops(fill, property = "fill/gradient/colors", colorStops = colorStops)
    }
}

fun BlockApi.getFillInfo(designBlock: DesignBlock): Fill? {
    return if (!this.hasFill(designBlock)) null
    else when (this.getFillType(designBlock)) {
        DesignBlockType.COLOR_FILL -> {
            val rgbaColor = this.getColor(designBlock, "fill/solid/color") as RGBAColor
            SolidFill(rgbaColor.toComposeColor())
        }
        DesignBlockType.LINEAR_GRADIENT_FILL -> {
            var fill = this.getFill(designBlock)
            LinearGradientFill(
                startPointX = this.getFloat(fill, "fill/gradient/linear/startPointX"),
                startPointY = this.getFloat(fill, "fill/gradient/linear/startPointY"),
                endPointX = this.getFloat(fill, "fill/gradient/linear/endPointX"),
                endPointY = this.getFloat(fill, "fill/gradient/linear/endPointY"),
                colorStops = this.getGradientColorStops(fill, "fill/gradient/colors")
            )
        }

        DesignBlockType.RADIAL_GRADIENT_FILL -> {
            var fill = this.getFill(designBlock)
            RadialGradientFill(
                centerX = this.getFloat(fill, "fill/gradient/radial/centerPointX"),
                centerY = this.getFloat(fill, "fill/gradient/radial/centerPointY"),
                radius = this.getFloat(fill, "fill/gradient/radial/radius"),
                colorStops = this.getGradientColorStops(fill, "fill/gradient/colors")
            )
        }

        DesignBlockType.CONICAL_GRADIENT_FILL -> {
            var fill = this.getFill(designBlock)
            ConicalGradientFill(
                centerX = this.getFloat(fill, "fill/gradient/conical/centerPointX"),
                centerY = this.getFloat(fill, "fill/gradient/conical/centerPointY"),
                colorStops = this.getGradientColorStops(fill, "fill/gradient/colors")
            )
        }

        // Image fill and Video fill are not supported yet
        else -> null
    }
}