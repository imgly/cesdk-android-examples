package ly.img.camera.record.components

import android.util.Range
import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ly.img.editor.compose.foundation.gestures.detectTapGestures
import ly.img.editor.core.theme.LocalExtendedColorScheme
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val longPressToRecordCircleSize = 144.dp
private val canvasSize = 88.dp

@Composable
internal fun RecordingButton(
    modifier: Modifier,
    recordingColor: Color,
    maxDuration: Duration,
    enabled: Boolean,
    hasStartedRecording: Boolean,
    isRecording: Boolean,
    isTimerRunning: Boolean,
    recordedDurations: List<Duration>,
    backgroundColor: Color = LocalExtendedColorScheme.current.black,
    strokeColor: Color = MaterialTheme.colorScheme.onBackground,
    prevRecordedSegmentsColor: Color = LocalExtendedColorScheme.current.white,
    segmentBackgroundStrokeWidth: Dp = 3.dp,
    segmentStrokeWidth: Dp = 5.dp,
    padding: Dp = 1.dp,
    normalizedDurationGap: Double = 0.01,
    longPressTimeout: Int = 600,
    onShortPress: () -> Unit,
    onLongPress: () -> Unit,
    onLongPressRelease: () -> Unit,
) {
    val state =
        when {
            !enabled -> RecordingButtonState.Disabled
            isTimerRunning -> RecordingButtonState.TimerRunning
            hasStartedRecording -> RecordingButtonState.Recording
            else -> RecordingButtonState.Ready
        }
    val transition = updateTransition(targetState = state, label = "RecordingButtonTransition")

    val paddingAnimation by transition.animateDp(
        label = "PaddingAnimation",
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) },
    ) { targetState ->
        when (targetState) {
            RecordingButtonState.Ready, RecordingButtonState.Disabled -> 4.dp
            else -> 0.dp
        }
    }

    val backgroundColorAnimation by transition.animateColor(label = "BackgroundColorAnimation") { targetState ->
        when (targetState) {
            RecordingButtonState.Disabled -> backgroundColor.copy(alpha = 0.50f)
            RecordingButtonState.Ready, RecordingButtonState.TimerRunning -> backgroundColor.copy(alpha = 0.64f)
            RecordingButtonState.Recording -> recordingColor.copy(alpha = 0.88f)
        }
    }

    val backgroundCircleSizeAnimation by transition.animateDp(
        label = "BackgroundCircleSizeAnimation",
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) },
    ) { targetState ->
        when (targetState) {
            RecordingButtonState.Ready, RecordingButtonState.Disabled -> 40.dp - padding - segmentBackgroundStrokeWidth
            else -> 32.dp
        }
    }

    val strokeColorAnimation by transition.animateColor(label = "StrokeColorAnimation") { targetState ->
        when (targetState) {
            RecordingButtonState.Disabled -> strokeColor.copy(alpha = 0.50f)
            else -> strokeColor
        }
    }

    val symbolSizeAnimation by transition.animateDp(
        label = "SymbolSizeAnimation",
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) },
    ) { targetState ->
        when (targetState) {
            RecordingButtonState.Ready -> 20.dp
            RecordingButtonState.Recording, RecordingButtonState.TimerRunning -> 32.dp
            RecordingButtonState.Disabled -> 0.dp
        }
    }

    val cornerRadiusAnimation by transition.animateDp(
        label = "CornerRadiusAnimation",
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) },
    ) { targetState ->
        when (targetState) {
            RecordingButtonState.Ready -> 10.dp
            RecordingButtonState.Recording, RecordingButtonState.TimerRunning -> 4.dp
            RecordingButtonState.Disabled -> 0.dp
        }
    }

    val rotationAnimation by transition.animateFloat(
        label = "RotationAnimation",
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) },
    ) { targetState ->
        when (targetState) {
            RecordingButtonState.Ready, RecordingButtonState.Disabled -> 45f
            else -> 90f
        }
    }

    val symbolColorAnimation by transition.animateColor(label = "SymbolColorAnimation") { targetState ->
        when (targetState) {
            RecordingButtonState.Ready, RecordingButtonState.Disabled -> recordingColor
            else -> strokeColor
        }
    }

    val inactiveSegmentColorAnimation by animateColorAsState(
        targetValue = if (!isRecording) recordingColor else prevRecordedSegmentsColor,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "InactiveSegmentColorAnimation",
    )

    var segmentsBackgroundStart by remember { mutableStateOf(0.0) }
    var segmentsBackgroundEnd by remember { mutableStateOf(1.0) }
    var normalizedSegmentPositions by remember { mutableStateOf(listOf<Range<Double>>()) }

    val segmentsBackgroundStartAngleAnimation by animateFloatAsState(
        targetValue = -90f + (segmentsBackgroundStart * 360).toFloat(),
        label = "StartAngleAnimation",
    )
    val segmentsBackgroundSweepAngleAnimation by animateFloatAsState(
        targetValue = ((segmentsBackgroundEnd - segmentsBackgroundStart) * 360).toFloat(),
        label = "SweepAngleAnimation",
    )

    LaunchedEffect(recordedDurations, isRecording) {
        if (recordedDurations.isEmpty()) {
            segmentsBackgroundStart = 0.0
            segmentsBackgroundEnd = 1.0
            normalizedSegmentPositions = listOf()
        } else {
            // Limit the visualization to a sensible max duration
            val maxTotalDuration = if (maxDuration == Duration.INFINITE) 60.seconds else maxDuration
            val totalDuration = maxOf(maxTotalDuration, recordedDurations.reduce { acc, duration -> acc + duration })

            // Add the length of the visual gaps to the total duration to make calculations easier
            val recordedClipsDurationsCount = recordedDurations.count()
            val gapsCount = if (isRecording) recordedClipsDurationsCount + 1 else recordedClipsDurationsCount
            val gapsDuration = totalDuration * gapsCount * normalizedDurationGap
            val finalDuration = totalDuration + gapsDuration

            fun normalizeSegments(): List<Range<Double>> {
                val normalizedSegments = mutableListOf<Range<Double>>()
                var currentPosition = normalizedDurationGap / 2
                for (duration in recordedDurations) {
                    val normalizedDuration = duration / finalDuration
                    val end = currentPosition + normalizedDuration
                    normalizedSegments.add(Range.create(currentPosition, end.coerceAtMost(1.0)))
                    currentPosition = end + normalizedDurationGap
                }
                return normalizedSegments
            }

            normalizedSegmentPositions = normalizeSegments()

            val newBackgroundStart = normalizedSegmentPositions.last().upper + normalizedDurationGap
            val newBackgroundEnd = 1 - normalizedDurationGap / 2
            if (newBackgroundStart < newBackgroundEnd) {
                segmentsBackgroundStart = newBackgroundStart
                segmentsBackgroundEnd = newBackgroundEnd
            } else {
                segmentsBackgroundStart = 0.0
                segmentsBackgroundEnd = 0.0
            }
        }
    }

    val scope = rememberCoroutineScope()

    var isPressed by remember { mutableStateOf(false) }
    val longPressAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 0f,
        label = "LongPressAlpha",
        animationSpec =
            tween(
                durationMillis = if (isPressed) longPressTimeout else 200,
                easing = EaseInOut,
            ),
    )
    val longPressScale by animateFloatAsState(
        targetValue = if (isPressed) (longPressToRecordCircleSize / canvasSize) else 0f,
        label = "LongPressScale",
        animationSpec =
            tween(
                durationMillis = if (isPressed) longPressTimeout else 200,
                easing = EaseInOut,
            ),
    )

    val localView = LocalView.current

    Box(modifier.size(longPressToRecordCircleSize)) {
        Canvas(
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .size(canvasSize)
                    .padding(paddingAnimation)
                    .pointerInput(enabled) {
                        if (!enabled) return@pointerInput

                        var isLongPress: Boolean
                        detectTapGestures(
                            onPress = {
                                isLongPress = false
                                isPressed = true
                                val job =
                                    scope.launch {
                                        delay(longPressTimeout.toLong()) // Delay for long press
                                        if (isActive) {
                                            isLongPress = true
                                            isPressed = false
                                            localView.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                                            onLongPress()
                                        }
                                    }

                                try {
                                    awaitRelease()
                                } finally {
                                    job.cancel()
                                    isPressed = false
                                    localView.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                                    if (isLongPress) {
                                        onLongPressRelease()
                                    } else {
                                        onShortPress()
                                    }
                                }
                            },
                        )
                    },
        ) {
            val canvasSize = size.minDimension

            // Long press circle
            drawCircle(
                color = recordingColor,
                radius = (canvasSize * longPressScale) / 2.0f,
                alpha = longPressAlpha,
            )

            // Background circle
            drawCircle(
                color = backgroundColorAnimation,
                radius = backgroundCircleSizeAnimation.toPx(),
            )

            // Animated morphing symbol (circle to square)
            val symbolSize = symbolSizeAnimation.toPx()
            val cornerRadius = cornerRadiusAnimation.toPx()
            rotate(rotationAnimation) {
                drawRoundRect(
                    color = symbolColorAnimation,
                    size = Size(symbolSize, symbolSize),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    topLeft = center - Offset(symbolSize / 2, symbolSize / 2),
                )
            }

            // Stroke behind the segments
            val paddingPx = padding.toPx()
            val segmentBackgroundStrokeWidthPx = segmentBackgroundStrokeWidth.toPx()
            val segmentBackgroundTopLeftOffset =
                Offset(paddingPx + segmentBackgroundStrokeWidthPx / 2, paddingPx + segmentBackgroundStrokeWidthPx / 2)
            val segmentBackgroundAdjustedSize = canvasSize - segmentBackgroundTopLeftOffset.x - segmentBackgroundTopLeftOffset.y
            drawArc(
                color = strokeColorAnimation,
                style = Stroke(width = segmentBackgroundStrokeWidth.toPx()),
                topLeft = segmentBackgroundTopLeftOffset,
                size = Size(segmentBackgroundAdjustedSize, segmentBackgroundAdjustedSize),
                startAngle = segmentsBackgroundStartAngleAnimation,
                sweepAngle = segmentsBackgroundSweepAngleAnimation,
                useCenter = false,
            )

            // Recording segments
            val segmentStrokeWidthPx = segmentStrokeWidth.toPx()
            val segmentTopLeftOffset = Offset(segmentStrokeWidthPx / 2, segmentStrokeWidthPx / 2)
            val segmentDiameter = canvasSize - segmentTopLeftOffset.x - segmentTopLeftOffset.y

            normalizedSegmentPositions.forEachIndexed { index, segment ->
                val color = if (index == normalizedSegmentPositions.size - 1) recordingColor else inactiveSegmentColorAnimation
                drawArc(
                    color = color,
                    style = Stroke(width = segmentStrokeWidthPx),
                    topLeft = segmentTopLeftOffset,
                    size = Size(segmentDiameter, segmentDiameter),
                    startAngle = -90f + (segment.lower * 360).toFloat(),
                    sweepAngle = (segment.upper - segment.lower).toFloat() * 360,
                    useCenter = false,
                )
            }
        }
    }
}

private enum class RecordingButtonState {
    Disabled,
    Ready,
    TimerRunning,
    Recording,
}
