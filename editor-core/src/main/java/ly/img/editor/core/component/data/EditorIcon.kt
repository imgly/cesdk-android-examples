package ly.img.editor.core.component.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Stable
sealed interface EditorIcon {
    /**
     * A type of an icon that renders an image vector.
     *
     * @param imageVector the image as a vector.
     * @param tint the tint of the vector.
     */
    @Stable
    data class Vector(
        val imageVector: ImageVector,
        val tint: (@Composable () -> Color)? = null,
    ) : EditorIcon

    /**
     * A type of an icon that renders a list of colors in circles.
     *
     * @param colors list of colors that should be displayed in circles.
     */
    @Stable
    data class Colors(
        val colors: List<Color>,
    ) : EditorIcon {
        init {
            require(colors.isNotEmpty()) {
                "List of colors should not be empty."
            }
        }

        /**
         * A convenience constructor when there is a single color that should be drawn.
         *
         * @param color the color that should be displayed in a circle.
         */
        constructor(color: Color) : this(listOf(color))
    }

    /**
     * A type of an icon that renders a fill and/or a stroke.
     * Check the documentation of [Fill] for more information.
     *
     * @param showFill if true the circle for the fill is shown. If [fill] is null, then a transparent circle will be drawn.
     * @param showStroke if true the circle for the stroke is shown. If [stroke] is null, then a transparent circle will be drawn.
     * @param fill the fill of the icon.
     * @param stroke the stroke color of the icon.
     */
    @Stable
    data class FillStroke(
        val showFill: Boolean,
        val showStroke: Boolean,
        val fill: Fill?,
        val stroke: Color?,
    ) : EditorIcon
}
