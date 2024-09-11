package ly.img.editor.base.ui.handler

import androidx.compose.ui.graphics.Color
import ly.img.editor.base.engine.changeLightnessBy
import ly.img.editor.base.engine.getFillType
import ly.img.editor.base.engine.setConicalGradientFill
import ly.img.editor.base.engine.setFillType
import ly.img.editor.base.engine.setLinearGradientFill
import ly.img.editor.base.engine.setRadialGradientFill
import ly.img.editor.base.engine.toEngineColor
import ly.img.editor.base.ui.BlockEvent.*
import ly.img.editor.core.ui.EventsHandler
import ly.img.editor.core.ui.inject
import ly.img.editor.core.ui.register
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.GradientColorStop
import ly.img.engine.RGBAColor

@Suppress("NAME_SHADOWING")
fun EventsHandler.blockFillEvents(
    engine: () -> Engine,
    block: () -> DesignBlock,
) {
    val engine by inject(engine)
    val block by inject(block)

    fun setGradientColorAtIndex(
        engine: Engine,
        fillBlock: DesignBlock,
        index: Int,
        color: Color,
    ) {
        val colors = engine.block.getGradientColorStops(fillBlock, "fill/gradient/colors")
        engine.block.setGradientColorStops(
            fillBlock,
            "fill/gradient/colors",
            colors.mapIndexed { colorIndex, gradientColorStop ->
                if (colorIndex == index) {
                    GradientColorStop(gradientColorStop.stop, color.toEngineColor())
                } else {
                    gradientColorStop
                }
            },
        )
    }

    fun onChangeFillStyle(style: String) {
        engine.block.setFillEnabled(block, true)

        val fillStyleEnum = FillType.get(style)
        val currentFillType = engine.block.getFillType(block)
        if (currentFillType == fillStyleEnum) return

        val colorStops =
            when (currentFillType) {
                FillType.Color -> {
                    val originalColor = engine.block.getFillSolidColor(block)
                    listOf(
                        GradientColorStop(0f, originalColor),
                        GradientColorStop(1f, originalColor.changeLightnessBy(0.4f)),
                    )
                }

                FillType.LinearGradient,
                FillType.RadialGradient,
                FillType.ConicalGradient,
                -> {
                    engine.block.getGradientColorStops(
                        engine.block.getFill(block),
                        "fill/gradient/colors",
                    )
                }

                else -> {
                    listOf(
                        GradientColorStop(0f, Color.White.toEngineColor()),
                        GradientColorStop(1f, Color.Black.toEngineColor()),
                    )
                }
            }

        when (fillStyleEnum) {
            FillType.Color -> {
                engine.block.setFillType(block, FillType.Color)
                engine.block.setFillSolidColor(
                    block,
                    colorStops.firstOrNull()?.color as? RGBAColor ?: Color.Black.toEngineColor(),
                )
            }
            FillType.LinearGradient ->
                engine.block.setLinearGradientFill(
                    block,
                    0.5f,
                    0f,
                    0.5f,
                    1.0f,
                    colorStops = colorStops,
                )

            FillType.RadialGradient ->
                engine.block.setRadialGradientFill(
                    block,
                    0.5f,
                    0.5f,
                    0.5f,
                    colorStops = colorStops,
                )

            FillType.ConicalGradient ->
                engine.block.setConicalGradientFill(
                    block,
                    0.5f,
                    0.5f,
                    colorStops = colorStops,
                )

            else -> throw UnsupportedOperationException("Fill type not supported")
        }

        engine.editor.addUndoStep()
    }

    register<OnChangeFillColor> {
        engine.block.setFillEnabled(block, true)
        engine.block.setFillType(block, FillType.Color)
        engine.block.setFillSolidColor(block, it.color.toEngineColor())
    }

    register<OnChangeGradientFillColors> {
        engine.block.setFillEnabled(block, true)
        val fillType = engine.block.getFillType(block)
        if (fillType == FillType.ConicalGradient ||
            fillType == FillType.LinearGradient ||
            fillType == FillType.RadialGradient
        ) {
            val fillBlock = engine.block.getFill(block)
            setGradientColorAtIndex(engine, fillBlock, it.index, it.color)
        } else {
            throw UnsupportedOperationException("Fill type is not a gradient fill")
        }
    }
    register<OnChangeLinearGradientParams> {
        engine.block.setLinearGradientFill(block, it.startX, it.startY, it.endX, it.endY)
    }
    register<OnChangeRadialGradientParams> {
        engine.block.setRadialGradientFill(block, it.centerX, it.centerY, it.radius)
    }
    register<OnChangeConicalGradientParams> {
        engine.block.setConicalGradientFill(block, it.centerX, it.centerY)
    }
    register<OnDisableFill> {
        if (engine.block.isFillEnabled(block)) {
            engine.block.setFillEnabled(block, false)
            engine.editor.addUndoStep()
        }
    }
    register<OnEnableFill> {
        if (!engine.block.isFillEnabled(block)) {
            engine.block.setFillEnabled(block, true)
            engine.editor.addUndoStep()
        }
    }
    register<OnChangeFillStyle> {
        onChangeFillStyle(it.style)
    }
}
