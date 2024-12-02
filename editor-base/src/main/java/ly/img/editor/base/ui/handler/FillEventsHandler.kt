package ly.img.editor.base.ui.handler

import androidx.compose.ui.graphics.Color
import ly.img.editor.base.engine.changeLightnessBy
import ly.img.editor.base.engine.setConicalGradientFill
import ly.img.editor.base.engine.setFillType
import ly.img.editor.base.engine.setLinearGradientFill
import ly.img.editor.base.engine.setRadialGradientFill
import ly.img.editor.base.engine.toEngineColor
import ly.img.editor.base.ui.BlockEvent.OnChangeConicalGradientParams
import ly.img.editor.base.ui.BlockEvent.OnChangeFillColor
import ly.img.editor.base.ui.BlockEvent.OnChangeFillStyle
import ly.img.editor.base.ui.BlockEvent.OnChangeGradientFillColors
import ly.img.editor.base.ui.BlockEvent.OnChangeLinearGradientParams
import ly.img.editor.base.ui.BlockEvent.OnChangeRadialGradientParams
import ly.img.editor.base.ui.BlockEvent.OnDisableFill
import ly.img.editor.base.ui.BlockEvent.OnEnableFill
import ly.img.editor.core.ui.EventsHandler
import ly.img.editor.core.ui.engine.getFillType
import ly.img.editor.core.ui.inject
import ly.img.editor.core.ui.register
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.GradientColorStop
import ly.img.engine.RGBAColor
import kotlin.math.tan

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
        if (DesignBlockType.getOrNull(engine.block.getType(block)) == DesignBlockType.Text) {
            engine.block.setTextColor(block, it.color.toEngineColor())
        } else {
            engine.block.setFillSolidColor(block, it.color.toEngineColor())
        }
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
        val absRotationInDegrees = ((it.rotationInDegrees % 360.0) + 360.0) % 360.0

        val slope = tan(Math.toRadians(absRotationInDegrees))

        val startX: Double
        val startY: Double
        val endX: Double
        val endY: Double

        when (absRotationInDegrees) {
            in 0f..45f, in 315f..360f -> {
                startX = 0.0
                startY = 0.5 - 0.5 * slope
                endX = 1.0
                endY = 0.5 + 0.5 * slope
            }
            in 135.0f..225.0f -> {
                startX = 1.0
                startY = 0.5 + 0.5 * slope
                endX = 0.0
                endY = 0.5 - 0.5 * slope
            }
            in 45f..135.0f -> {
                startX = 0.5 - 0.5 / slope
                startY = 0.0
                endX = 0.5 + 0.5 / slope
                endY = 1.0
            }
            else -> { // 225.0f..315.0f
                startX = 0.5 + 0.5 / slope
                startY = 1.0
                endX = 0.5 - 0.5 / slope
                endY = 0.0
            }
        }
        engine.block.setLinearGradientFill(block, startX.toFloat(), startY.toFloat(), endX.toFloat(), endY.toFloat())
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
