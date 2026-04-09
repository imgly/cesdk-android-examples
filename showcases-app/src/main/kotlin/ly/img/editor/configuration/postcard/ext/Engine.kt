package ly.img.editor.configuration.postcard.ext

import androidx.compose.ui.graphics.Color
import ly.img.editor.core.component.data.ConicalGradientFill
import ly.img.editor.core.component.data.Fill
import ly.img.editor.core.component.data.LinearGradientFill
import ly.img.editor.core.component.data.RadialGradientFill
import ly.img.editor.core.component.data.SolidFill
import ly.img.engine.ColorSpace
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.RGBAColor

/**
 * Returns current page index.
 */
internal fun Engine.getCurrentPageIndex() = scene.run { getPages().indexOf(getCurrentPage()) }

/**
 * Converts engine color into compose color.
 */
internal fun Engine.toComposeColor(color: ly.img.engine.Color): Color {
    val rgbaEngineColor = color as? RGBAColor
        ?: editor.convertColorToColorSpace(color, ColorSpace.SRGB) as RGBAColor
    return Color(
        red = rgbaEngineColor.r,
        green = rgbaEngineColor.g,
        blue = rgbaEngineColor.b,
        alpha = rgbaEngineColor.a,
    )
}

/**
 * An extension function that returns [FillType] of the design block.
 *
 * @param designBlock the design block that is queried.
 */
internal fun Engine.getFillType(designBlock: DesignBlock): FillType? = if (!block.supportsFill(designBlock)) {
    null
} else {
    FillType.Companion.get(block.getType(block.getFill(designBlock)))
}

/**
 * An extension function that returns [Fill] of the design block.
 *
 * @param designBlock the design block that is queried.
 */
internal fun Engine.getFill(designBlock: DesignBlock): Fill? = if (!block.supportsFill(designBlock)) {
    null
} else {
    when (getFillType(designBlock)) {
        FillType.Color -> {
            val rgbaColor = if (DesignBlockType.Companion.getOrNull(block.getType(designBlock)) == DesignBlockType.Text) {
                block.getTextColors(designBlock).first()
            } else {
                block.getColor(designBlock, "fill/solid/color")
            }
            SolidFill(toComposeColor(rgbaColor))
        }

        FillType.LinearGradient -> {
            val fill = block.getFill(designBlock)
            LinearGradientFill(
                startPointX = block.getFloat(fill, "fill/gradient/linear/startPointX"),
                startPointY = block.getFloat(fill, "fill/gradient/linear/startPointY"),
                endPointX = block.getFloat(fill, "fill/gradient/linear/endPointX"),
                endPointY = block.getFloat(fill, "fill/gradient/linear/endPointY"),
                colorStops = block.getGradientColorStops(fill, "fill/gradient/colors"),
            )
        }

        FillType.RadialGradient -> {
            val fill = block.getFill(designBlock)
            RadialGradientFill(
                centerX = block.getFloat(fill, "fill/gradient/radial/centerPointX"),
                centerY = block.getFloat(fill, "fill/gradient/radial/centerPointY"),
                radius = block.getFloat(fill, "fill/gradient/radial/radius"),
                colorStops = block.getGradientColorStops(fill, "fill/gradient/colors"),
            )
        }

        FillType.ConicalGradient -> {
            val fill = block.getFill(designBlock)
            ConicalGradientFill(
                centerX = block.getFloat(fill, "fill/gradient/conical/centerPointX"),
                centerY = block.getFloat(fill, "fill/gradient/conical/centerPointY"),
                colorStops = block.getGradientColorStops(fill, "fill/gradient/colors"),
            )
        }

        // Image fill and Video fill are not supported yet
        else -> null
    }
}

/**
 * An extension function that returns stroke color of the design block.
 *
 * @param designBlock the design block that is queried.
 */
internal fun Engine.getStrokeColor(designBlock: DesignBlock): Color? {
    if (!block.supportsStroke(designBlock)) return null
    return toComposeColor(block.getColor(designBlock, "stroke/color"))
}
