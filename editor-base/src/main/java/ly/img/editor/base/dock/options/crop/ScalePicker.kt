package ly.img.editor.base.dock.options.crop

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ly.img.editor.core.ui.utils.toDp
import ly.img.editor.core.ui.utils.toPx
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun ScalePicker(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    rangeInclusionType: RangeInclusionType = RangeInclusionType.RangeInclusiveInclusive,
    rangeExclusionDifferential: Float = 0.001f,
    onValueChangeFinished: ((Float) -> Unit)? = null,
    tickWidth: Dp = 1.dp,
    tickHeight: Dp = 16.dp,
    tickSpacing: Dp = 10.dp,
    tickStep: Int = 3,
    specialTickStep: Int = 15,
    scaleHeight: Dp = 24.dp,
    neutralNormalTickColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
    neutralSpecialTickColor: Color = MaterialTheme.colorScheme.outline,
    neutralScaleColor: Color = MaterialTheme.colorScheme.onSurface,
    highlightNormalTickColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
    highlightSpecialTickColor: Color = MaterialTheme.colorScheme.primary,
    highlightScaleColor: Color = MaterialTheme.colorScheme.primary,
) {
    BoxWithConstraints(
        modifier = modifier.requiredHeight(44.dp),
    ) {
        val textStyle =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.1.sp,
                lineHeight = 20.sp,
                fontSize = 14.sp,
            )

        val rangeStart =
            remember(valueRange) {
                if (rangeInclusionType.isStartIncluded()) valueRange.start else valueRange.start + rangeExclusionDifferential
            }

        val rangeEnd =
            remember(valueRange) {
                if (rangeInclusionType.isEndIncluded()) valueRange.endInclusive else valueRange.endInclusive - rangeExclusionDifferential
            }

        val tickRange =
            remember(valueRange, tickStep) {
                generateSequence(valueRange.start) { previous ->
                    val next = previous + tickStep
                    if (next > valueRange.endInclusive) null else next
                }.toList()
            }

        var currentValue by remember(value) {
            mutableStateOf(value)
        }

        val roundedCurrentValue = currentValue.roundToInt()
        val textToDraw = "$roundedCurrentValue°"

        var snapState by remember {
            mutableStateOf(SnapState.Disarmed)
        }

        val totalTickWidthPx = tickWidth.toPx() + tickSpacing.toPx()
        val tickersWidthPx = totalTickWidthPx * tickRange.size
        val tickersWidthDp = tickersWidthPx.toDp()

        fun getTickColor(value: Float): Color {
            val roundedValue = value.roundToInt()
            return if ((currentValue < 0 && value <= 0 && value > currentValue) || (currentValue > 0 && value >= 0 && value < currentValue)) {
                if (roundedValue % specialTickStep == 0) highlightSpecialTickColor else highlightNormalTickColor
            } else {
                if (roundedValue % specialTickStep == 0) neutralSpecialTickColor else neutralNormalTickColor
            }
        }

        val localView = LocalView.current

        Canvas(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .width(maxWidth + tickersWidthDp)
                    .pointerInput(value) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                onValueChangeFinished?.invoke(currentValue)
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                val changeInValue = (-dragAmount / totalTickWidthPx) * tickStep
                                val changedValue =
                                    (currentValue + changeInValue).coerceIn(
                                        minimumValue = rangeStart,
                                        maximumValue = rangeEnd,
                                    )
                                val canSnap = changedValue in (-0.5..0.5)
                                val newValue =
                                    if (snapState == SnapState.Disarmed) {
                                        if (!canSnap) snapState = SnapState.Armed
                                        changedValue
                                    } else {
                                        if (canSnap) {
                                            if (abs(changedValue) > 0.2 && currentValue == 0f) {
                                                snapState = SnapState.Disarmed
                                            }
                                            0f
                                        } else {
                                            changedValue
                                        }
                                    }
                                if (integerCrossedOrReached(currentValue / tickStep, newValue / tickStep) ||
                                    (newValue != currentValue && (newValue == rangeStart || newValue == rangeEnd))
                                ) {
                                    localView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                }
                                currentValue = newValue
                                onValueChange(currentValue)
                            },
                        )
                    }
                    // Workaround to enable alpha compositing
                    // TODO: Use https://developer.android.com/jetpack/compose/graphics/draw/modifiers#compositing-strategy when available
                    .graphicsLayer { alpha = 0.99F }
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush =
                                Brush.horizontalGradient(
                                    0f to Color.Transparent,
                                    0.25f to Color.Black,
                                    0.75f to Color.Black,
                                    1f to Color.Transparent,
                                ),
                            blendMode = BlendMode.DstIn,
                        )
                    },
        ) {
            tickRange.forEach { tickValue ->
                val startOffset =
                    Offset(
                        x = maxWidth.toPx() / 2 + ((tickValue - currentValue) * (totalTickWidthPx / tickStep)),
                        y = size.height,
                    )
                drawLine(
                    getTickColor(tickValue),
                    start = startOffset,
                    end = startOffset.minus(Offset(x = 0f, y = tickHeight.toPx())),
                    strokeWidth = tickWidth.toPx(),
                )
            }

            val centerTickStartOffset = Offset(center.x, size.height)
            val centerTickEndOffset =
                centerTickStartOffset.minus(
                    Offset(x = 0f, y = scaleHeight.toPx()),
                )

            drawLine(
                if (roundedCurrentValue == 0) neutralScaleColor else highlightScaleColor,
                start = centerTickStartOffset,
                end = centerTickEndOffset,
                strokeWidth = tickWidth.toPx(),
            )
        }

        Text(
            text = textToDraw,
            style =
                textStyle.copy(
                    color = if (roundedCurrentValue == 0) neutralScaleColor else highlightScaleColor,
                ),
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .offset(x = 1.5.dp) // to make the number look centre-ish / account for the °
                    .paddingFromBaseline(bottom = scaleHeight + 2.dp),
        )
    }
}

private fun integerCrossedOrReached(
    old: Float,
    new: Float,
): Boolean {
    if (old == new) return false
    val distanceMoreThanOne = abs(old - new) > 1
    val oldInt = old.toInt()
    val newInt = new.toInt()
    val exactOld = old == oldInt.toFloat()
    val exactNew = new == newInt.toFloat()
    val intChanged = oldInt != newInt
    return distanceMoreThanOne || exactNew || (!exactOld && intChanged)
}

enum class RangeInclusionType {
    RangeInclusiveInclusive,
    RangeInclusiveExclusive,
    RangeExclusiveInclusive,
    RangeExclusiveExclusive,
    ;

    fun isStartIncluded() = this == RangeInclusiveExclusive || this == RangeInclusiveInclusive

    fun isEndIncluded() = this == RangeExclusiveInclusive || this == RangeInclusiveInclusive
}

private enum class SnapState {
    Armed,
    Disarmed,
}
