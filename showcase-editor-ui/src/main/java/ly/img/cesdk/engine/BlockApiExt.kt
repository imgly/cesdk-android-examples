package ly.img.cesdk.engine

import ly.img.engine.BlockApi
import ly.img.engine.DesignBlock
import ly.img.engine.FillType
import ly.img.engine.GradientType
import ly.img.engine.GradientColorStop
import java.lang.IllegalStateException

fun BlockApi.getFillType(designBlock: DesignBlock): FillType? {
    return if (!this.hasFill(designBlock)) null
    else when (this.getEnum(designBlock, "fill/type")) {
        "Gradient" -> FillType.GRADIENT
        "Solid" -> FillType.SOLID
        "Image" -> FillType.IMAGE
        "Video" -> FillType.VIDEO
        else -> null
    }
}

fun BlockApi.getGradientFillType(designBlock: DesignBlock): GradientType? {
    return if (!this.hasFill(designBlock)) null
    else when (val type = this.getType(this.getFill(designBlock)).substringAfter("gradient/", "NaG")) {
        "NaG" -> null // Not a gradient
        "linear" -> GradientType.LINEAR
        "radial" -> GradientType.RADIAL
        "conical" -> GradientType.CONICAL
        else -> throw IllegalArgumentException("Unknown gradient type: $type")
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
            this.setScopeEnabled(oldFill, "lifecycle/destroy", true)
            this.destroy(oldFill)
        }
        newFill
    }
}
fun BlockApi.setLinearGradientFill(
    designBlock: DesignBlock,
    startPointX : Float,
    startPointY : Float,
    endPointX : Float,
    endPointY : Float,
    colorStops:List<GradientColorStop>? = null
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
    centerPointX : Float,
    centerPointY : Float,
    radius: Float,
    colorStops:List<GradientColorStop>? = null
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
    centerPointX : Float,
    centerPointY : Float,
    colorStops:List<GradientColorStop>? = null
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
        FillType.SOLID -> SolidFill(checkNotNull(this.getColor(designBlock, "fill/solid/color").toComposeColor()))
        FillType.GRADIENT -> {
            val fill = this.getFill(designBlock)
            when (this.getGradientFillType(designBlock)) {
                GradientType.LINEAR -> LinearGradientFill(
                    startPointX = this.getFloat(fill, "fill/gradient/linear/startPointX"),
                    startPointY = this.getFloat(fill, "fill/gradient/linear/startPointY"),
                    endPointX = this.getFloat(fill, "fill/gradient/linear/endPointX"),
                    endPointY = this.getFloat(fill, "fill/gradient/linear/endPointY"),
                    colorStops = this.getGradientColorStops(fill, "fill/gradient/colors")
                )
                GradientType.RADIAL -> RadialGradientFill(
                    centerX = this.getFloat(fill, "fill/gradient/radial/centerPointX"),
                    centerY = this.getFloat(fill, "fill/gradient/radial/centerPointY"),
                    radius = this.getFloat(fill, "fill/gradient/radial/radius"),
                    colorStops = this.getGradientColorStops(fill, "fill/gradient/colors")
                )
                GradientType.CONICAL -> ConicalGradientFill(
                    centerX = this.getFloat(fill, "fill/gradient/conical/centerPointX"),
                    centerY = this.getFloat(fill, "fill/gradient/conical/centerPointY"),
                    colorStops = this.getGradientColorStops(fill, "fill/gradient/colors")
                )
                null -> throw IllegalStateException("Fill type gradient, but gradient fill type is null")
            }
        }
        FillType.IMAGE -> TODO("Image fill is not supported yet")
        FillType.VIDEO -> TODO("Video fill is not supported yet")
        null -> null
    }
}