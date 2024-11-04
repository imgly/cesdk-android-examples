package ly.img.camera.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ly.img.camera.record.RecordingManager
import ly.img.editor.compose.animation.AnimatedContent
import ly.img.editor.compose.animation.AnimatedVisibility
import ly.img.editor.compose.animation.with
import ly.img.editor.compose.animation_core.fadeIn
import ly.img.editor.compose.animation_core.fadeOut
import ly.img.editor.compose.animation_core.scaleIn
import ly.img.editor.compose.animation_core.scaleOut
import ly.img.editor.compose.animation_core.slideInVertically
import ly.img.editor.core.theme.LocalExtendedColorScheme
import ly.img.editor.core.theme.surface2

@Composable
internal fun CountdownTimerView(
    modifier: Modifier = Modifier,
    recordingColor: Color,
    recordingStatus: RecordingManager.Status,
    size: Dp = 228.dp,
    strokeWidth: Dp = 12.dp,
    strokeColor: Color = LocalExtendedColorScheme.current.white,
    backgroundColor: Color = MaterialTheme.colorScheme.surface2,
    hideDelay: Long = 400,
) {
    var remainingTime by remember { mutableStateOf(1) }
    var totalTime by remember { mutableStateOf(1) }
    var state by remember { mutableStateOf(State.Idle) }

    var scale by remember { mutableStateOf(0f) }
    val scaleAnimation by animateFloatAsState(
        targetValue = scale,
        label = "ScaleAnimation",
        finishedListener = { value ->
            if (value == 0f) {
                state = State.Idle
            }
        },
    )

    LaunchedEffect(state) {
        when (state) {
            State.Idle -> {
                scale = 0f
                totalTime = 1
                remainingTime = 1
            }

            State.TimerRunning -> {
                scale = 1f
            }

            State.Recording -> {
                remainingTime = 0
                delay(hideDelay)
                scale = 0f
            }

            State.TimerCancelled -> {
                delay(hideDelay)
                scale = 0f
            }
        }
    }

    var prevStatus by remember { mutableStateOf<RecordingManager.Status>(RecordingManager.Status.Idle) }
    LaunchedEffect(recordingStatus) {
        val newStatus = recordingStatus

        when {
            prevStatus is RecordingManager.Status.Idle && newStatus is RecordingManager.Status.TimerRunning -> {
                state = State.TimerRunning
            }

            prevStatus is RecordingManager.Status.TimerRunning && newStatus is RecordingManager.Status.StartRecording -> {
                state = State.Recording
            }

            prevStatus is RecordingManager.Status.TimerRunning && newStatus is RecordingManager.Status.Idle -> {
                state = State.TimerCancelled
            }
        }

        if (newStatus is RecordingManager.Status.TimerRunning) {
            remainingTime = newStatus.remainingTime
            totalTime = newStatus.totalTime
        }

        prevStatus = newStatus
    }

    Box(
        modifier =
            modifier
                .size(size)
                .graphicsLayer {
                    scaleX = scaleAnimation
                    scaleY = scaleAnimation
                    alpha = scaleAnimation
                },
    ) {
        val strokeSweepAngleAnimation by animateFloatAsState(
            targetValue = -360f * (remainingTime.toFloat() / totalTime),
            label = "SweepAngleAnimation",
        )

        val backgroundColorAnimation by animateColorAsState(
            targetValue = if (state == State.Recording) recordingColor else backgroundColor,
            label = "BackgroundColorAnimation",
        )

        val recordingSymbolAlphaAnimation by animateFloatAsState(
            targetValue = if (state == State.Recording) 1f else 0f,
            label = "AlphaAnimation",
        )

        val timerStoppedSymbolAlphaAnimation by animateFloatAsState(
            targetValue = if (state == State.TimerCancelled) 1f else 0f,
            label = "AlphaAnimation",
        )

        Canvas(modifier = Modifier.size(size)) {
            drawCircle(
                color = backgroundColorAnimation,
                alpha = 0.64f,
            )

            drawCircle(
                color = strokeColor,
                radius = 40.dp.toPx() * recordingSymbolAlphaAnimation,
                alpha = recordingSymbolAlphaAnimation,
            )

            val rectSize = 80.dp.toPx() * timerStoppedSymbolAlphaAnimation
            val topLeft = Offset(this.size.width / 2 - rectSize / 2, this.size.height / 2 - rectSize / 2)
            drawRoundRect(
                color = strokeColor,
                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                alpha = timerStoppedSymbolAlphaAnimation,
                topLeft = topLeft,
                size = Size(rectSize, rectSize),
            )

            val strokeWidthPx = strokeWidth.toPx()
            val strokeTopLeft = Offset(x = strokeWidthPx / 2, y = strokeWidthPx / 2)
            val strokeSize = Size(this.size.width - strokeWidthPx, this.size.height - strokeWidthPx)
            drawArc(
                color = strokeColor,
                startAngle = -90f,
                sweepAngle = strokeSweepAngleAnimation,
                useCenter = false,
                topLeft = strokeTopLeft,
                size = strokeSize,
                style = Stroke(width = strokeWidthPx),
            )
        }

        AnimatedVisibility(
            visible = state == State.TimerRunning,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(strokeWidth),
        ) {
            AnimatedContent(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(strokeWidth),
                transitionSpec = {
                    fadeIn() + slideInVertically() with fadeOut() + scaleOut()
                },
                targetState = remainingTime,
                contentAlignment = Alignment.Center,
            ) { time ->
                if (time > 0) {
                    Text(
                        modifier = Modifier.wrapContentHeight(),
                        style =
                            TextStyle(
                                fontSize = 137.sp,
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.W400,
                                textAlign = TextAlign.Center,
                                platformStyle = PlatformTextStyle(includeFontPadding = false),
                            ),
                        color = LocalExtendedColorScheme.current.white,
                        text = "$time",
                    )
                }
            }
        }
    }
}

private enum class State {
    Idle,
    TimerRunning,
    Recording,
    TimerCancelled,
}
