package ly.img.editor.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ly.img.editor.core.R
import ly.img.editor.core.component.data.EditorIcon
import ly.img.editor.core.component.data.Fill
import ly.img.editor.core.component.data.LinearGradientFill
import ly.img.editor.core.component.data.RadialGradientFill
import ly.img.editor.core.component.data.SolidFill
import ly.img.editor.core.component.data.toComposeColor
import ly.img.engine.RGBAColor

/**
 * A composable function that renders [EditorIcon].
 *
 * @param modifier the [Modifier] to be applied to this icon.
 * @param icon the icon that should be rendered.
 */
@Composable
fun EditorIcon(
    icon: EditorIcon,
    modifier: Modifier = Modifier,
) {
    when (icon) {
        is EditorIcon.Vector -> {
            Icon(
                modifier = modifier,
                imageVector = icon.imageVector,
                contentDescription = null,
                tint = icon.tint?.invoke() ?: LocalContentColor.current,
            )
        }
        is EditorIcon.Colors -> {
            if (icon.colors.size == 1) {
                ColorButton(
                    modifier = modifier,
                    color = icon.colors[0],
                    buttonSize = 24.dp,
                    selectionStrokeWidth = 0.dp,
                )
            } else if (icon.colors.size > 1) {
                Box(modifier = modifier) {
                    icon.colors.forEachIndexed { index, color ->
                        ColorButton(
                            color = color,
                            buttonSize = 24.dp,
                            selectionStrokeWidth = 0.dp,
                            modifier = Modifier.ifTrue(index > 0) { padding(start = 12.dp) },
                        )
                    }
                }
            }
        }
        is EditorIcon.FillStroke -> {
            if (icon.showFill && icon.showStroke) {
                Box(modifier = modifier) {
                    FillButton(fill = icon.fill, buttonSize = 24.dp, selectionStrokeWidth = 0.dp)
                    ColorButton(
                        color = icon.stroke,
                        buttonSize = 24.dp,
                        selectionStrokeWidth = 0.dp,
                        punchHole = true,
                        modifier = Modifier.padding(start = 12.dp),
                    )
                }
            } else if (icon.showFill) {
                FillButton(
                    modifier = modifier,
                    fill = icon.fill,
                    buttonSize = 24.dp,
                    selectionStrokeWidth = 0.dp,
                )
            } else if (icon.showStroke) {
                ColorButton(
                    modifier = modifier,
                    color = icon.stroke,
                    buttonSize = 24.dp,
                    selectionStrokeWidth = 0.dp,
                    punchHole = true,
                )
            } else {
                throw IllegalStateException("FillStrokeIcon has neither stroke nor fill.")
            }
        }
    }
}

@Composable
internal fun FillButton(
    fill: Fill?,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    punchHole: Boolean = false,
    onClick: (() -> Unit)? = null,
    buttonSize: Dp = 40.dp,
    selectionStrokeWidth: Dp = 2.dp,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.outline
    val noStroke = fill == null
    val colorFill = if (fill is SolidFill) fill.mainColor else null
    val fillSize = buttonSize.toPx()
    val brush: Brush? =
        when (fill) {
            is SolidFill -> null
            is LinearGradientFill ->
                remember(fill) {
                    ShaderBrush(
                        LinearGradientShader(
                            from = Offset(0f, 0f),
                            to = Offset(fillSize, fillSize),
                            colors = fill.colorStops.map { (it.color as? RGBAColor)?.toComposeColor() ?: Color.Transparent },
                            colorStops = fill.colorStops.map { it.stop },
                            tileMode = TileMode.Clamp,
                        ),
                    )
                }
            is RadialGradientFill ->
                remember(fill) {
                    ShaderBrush(
                        RadialGradientShader(
                            center = Offset(fillSize / 2f, fillSize / 2f),
                            radius = fillSize / 2f,
                            colors = fill.colorStops.map { (it.color as? RGBAColor)?.toComposeColor() ?: Color.Transparent },
                            colorStops = fill.colorStops.map { it.stop },
                            tileMode = TileMode.Clamp,
                        ),
                    )
                }
            /* Hide for now. No ConicalGradientFill implementation in Compose.
            is ConicalGradientFill ->
                remember(fill) {

                    TODO(
                        """
                        ConicalGradientFill is not supported yet.
                        There is not ConicalGradientShader implementation in Compose.
                        """.trimIndent(),
                    )
                }*/
            else -> {
                val image = ImageBitmap.imageResource(R.drawable.checkerboard_pattern)
                remember(
                    image,
                ) { ShaderBrush(ImageShader(image, TileMode.Repeated, TileMode.Repeated)) }
            }
        }
    Canvas(
        modifier =
            modifier
                .size(buttonSize)
                .ifTrue(onClick != null) { clip(if (noStroke) RoundedCornerShape(30) else CircleShape) }
                .ifTrue(onClick != null) { clickable { onClick?.invoke() } },
        onDraw = {
            val contrastStrokeWidthPx = 1.dp.toPx()
            val selectionStrokeWidthPx = selectionStrokeWidth.toPx()

            // Blend modes are only working with using the layer directly
            // Consider using CompositionStrategy instead when it's available
            // https://stackoverflow.com/a/73317659/21169736
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)

                // selection stroke
                if (selected) {
                    drawCircle(
                        color = primaryColor,
                        radius = size.minDimension / 2 - (selectionStrokeWidthPx / 2),
                        style = Stroke(width = selectionStrokeWidthPx),
                    )
                }

                // contrast stroke
                drawCircle(
                    color = outlineColor,
                    radius = size.minDimension / 2 - (selectionStrokeWidthPx * 2),
                )

                val circleRadius = size.minDimension / 2 - selectionStrokeWidthPx * 2 - contrastStrokeWidthPx

                if (brush != null) {
                    drawCircle(
                        brush = brush,
                        radius = circleRadius,
                    )
                } else {
                    drawCircle(
                        color = checkNotNull(colorFill),
                        radius = circleRadius,
                    )
                }

                if (punchHole) {
                    drawCircle(outlineColor, size.minDimension / 4)
                    drawCircle(
                        Color.Black,
                        size.minDimension / 4 - contrastStrokeWidthPx,
                        blendMode = BlendMode.Clear,
                    )
                }

                if (noStroke) {
                    val strokeMargin = contrastStrokeWidthPx * 2 + selectionStrokeWidthPx * 1.5f
                    drawLine(
                        start = Offset(x = strokeMargin, y = strokeMargin),
                        end = Offset(x = size.width - strokeMargin, y = size.height - strokeMargin),
                        strokeWidth = contrastStrokeWidthPx * 3 + selectionStrokeWidthPx / 2,
                        cap = StrokeCap.Round,
                        color = Color.White.copy(alpha = 0.5f),
                    )
                    drawLine(
                        start = Offset(x = strokeMargin, y = strokeMargin),
                        end = Offset(x = size.width - strokeMargin, y = size.height - strokeMargin),
                        strokeWidth = contrastStrokeWidthPx * 2 + selectionStrokeWidthPx / 2,
                        cap = StrokeCap.Round,
                        color = Color.Black,
                    )
                }

                restoreToCount(checkPoint)
            }
        },
    )
}
