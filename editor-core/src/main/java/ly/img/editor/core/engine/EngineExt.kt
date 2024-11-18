package ly.img.editor.core.engine

import androidx.compose.ui.graphics.Color
import ly.img.editor.core.component.data.ConicalGradientFill
import ly.img.editor.core.component.data.Fill
import ly.img.editor.core.component.data.LinearGradientFill
import ly.img.editor.core.component.data.RadialGradientFill
import ly.img.editor.core.component.data.SolidFill
import ly.img.engine.BlockApi
import ly.img.engine.ColorSpace
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.RGBAColor

/**
 * An extension function for converting an [Engine] RGBA color into Jetpack Compose Color.
 *
 * @return the converted color.
 */
fun RGBAColor.toComposeColor(): Color {
    return Color(
        red = this.r,
        green = this.g,
        blue = this.b,
        alpha = this.a,
    )
}

/**
 * An extension function for converting any [Engine] color into an Engine RGBA color.
 *
 * @return the converted color.
 */
fun ly.img.engine.Color.toRGBColor(engine: Engine): RGBAColor {
    return this as? RGBAColor ?: engine.editor.convertColorToColorSpace(this, ColorSpace.SRGB) as RGBAColor
}

/**
 * An extension function for checking whether the block has a color or gradient fill.
 *
 * @return true if the [designBlock] has a color or gradient fill, false otherwise.
 */
fun BlockApi.hasColorOrGradientFill(designBlock: DesignBlock): Boolean {
    val fillType = getFillType(designBlock)
    return fillType == FillType.Color ||
        fillType == FillType.LinearGradient ||
        fillType == FillType.RadialGradient ||
        fillType == FillType.ConicalGradient
}

/**
 * An extension function for getting the [FillType] of the [designBlock]. Check the documentation of [FillType] for more information.
 *
 * @return the [FillType] of the [designBlock].
 */
fun BlockApi.getFillType(designBlock: DesignBlock): FillType? =
    if (!this.supportsFill(designBlock)) {
        null
    } else {
        FillType.get(this.getType(this.getFill(designBlock)))
    }

/**
 * An extension function for getting the [Fill] of the [designBlock]. Check the documentation of [Fill] for more information.
 * Note that [Fill] (defined in the editor) does not support all the types defined in [FillType] (defined in the [Engine]).
 * More types will be added over time.
 *
 * @return the [Fill] of the [designBlock] based on its [FillType] and engine's current state.
 */
fun Engine.getFill(designBlock: DesignBlock): Fill? {
    return if (!block.supportsFill(designBlock)) {
        null
    } else {
        when (block.getFillType(designBlock)) {
            FillType.Color -> {
                val rgbaColor =
                    if (DesignBlockType.getOrNull(block.getType(designBlock)) == DesignBlockType.Text) {
                        block.getTextColors(designBlock).first().toRGBColor(this)
                    } else {
                        block.getColor(designBlock, "fill/solid/color") as RGBAColor
                    }
                SolidFill(rgbaColor.toComposeColor())
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
}

/**
 * An extension function that returns the stroke color of the [designBlock] if it is present.
 *
 * @param designBlock the design block that is being queried.
 * @return the stroke color of the [designBlock] if it is present, null otherwise.
 */
fun Engine.getStrokeColor(designBlock: DesignBlock): Color? {
    if (!block.supportsStroke(designBlock)) return null
    return block.getColor(designBlock, "stroke/color")
        .toRGBColor(this)
        .toComposeColor()
}

/**
 * An extension function that tells whether the [designBlock] is a background track.
 *
 * @param designBlock the design block that is being queried.
 * @return true if the design block is a background track, false otherwise.
 */
fun BlockApi.isBackgroundTrack(designBlock: DesignBlock): Boolean {
    return DesignBlockType.get(getType(designBlock)) == DesignBlockType.Track &&
        isAlwaysOnBottom(designBlock)
}

/**
 * An extension function for getting the background track from the scene.
 * A scene should have a maximum of one background track.
 *
 * @return the design block of the background track if it exists, null otherwise.
 */
fun BlockApi.getBackgroundTrack(): DesignBlock? {
    return findByType(DesignBlockType.Track).firstOrNull { isBackgroundTrack(it) }
}
