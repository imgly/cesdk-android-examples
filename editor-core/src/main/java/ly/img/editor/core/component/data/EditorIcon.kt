package ly.img.editor.core.component.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import ly.img.editor.core.engine.EngineScope
import ly.img.editor.core.engine.getFill
import ly.img.editor.core.engine.getStrokeColor
import ly.img.editor.core.engine.hasColorOrGradientFill
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

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
    ) : EditorIcon {
        companion object {
            /**
             * Returns a fill stroke icon for the [designBlock] based on the current engine state.
             *
             * @param engine the engine of the current editor.
             * @param designBlock the design block that is queried.
             * @return a fill stroke icon for the queried design block.
             */
            fun getForDesignBlock(
                engine: Engine,
                designBlock: DesignBlock,
            ): FillStroke {
                val showFill =
                    engine.block.supportsFill(designBlock) &&
                        engine.block.hasColorOrGradientFill(designBlock) &&
                        engine.block.isAllowedByScope(designBlock, EngineScope.FillChange)
                val showStroke =
                    engine.block.supportsStroke(designBlock) &&
                        engine.block.isAllowedByScope(designBlock, EngineScope.StrokeChange)
                return FillStroke(
                    showFill = showFill,
                    showStroke = showStroke,
                    fill =
                        if (showFill && engine.block.isFillEnabled(designBlock)) {
                            engine.getFill(designBlock)
                        } else {
                            null
                        },
                    stroke =
                        if (showStroke && engine.block.isStrokeEnabled(designBlock)) {
                            engine.getStrokeColor(designBlock)
                        } else {
                            null
                        },
                )
            }
        }
    }
}
