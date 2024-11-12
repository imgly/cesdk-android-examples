package ly.img.editor.core.ui.utils

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.positionChanged
import kotlin.math.abs

// Based on `PointerInputScope.detectTransformGestures()`
// Differences -
// 1. Only checking for zoom and no other transform gestures
// 2. Using `PointerEventPass.Initial` so we can detect before the Main pass
// 3. Consume all changes if more than one pointer to avoid conflicts with any other gestures
suspend fun PointerInputScope.detectZoomGestures(
    onZoom: (zoom: Float) -> Unit,
    onZoomEnd: () -> Unit,
) {
    awaitEachGesture {
        var zoom = 1f
        var pastTouchSlop = false
        val touchSlop = viewConfiguration.touchSlop

        awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
        do {
            val event = awaitPointerEvent(pass = PointerEventPass.Initial)
            val canceled = event.changes.any { it.isConsumed }
            if (!canceled) {
                val zoomChange = event.calculateZoom()

                if (!pastTouchSlop) {
                    zoom *= zoomChange
                    val centroidSize = event.calculateCentroidSize(useCurrent = false)
                    val zoomMotion = abs(1 - zoom) * centroidSize
                    if (zoomMotion > touchSlop) {
                        pastTouchSlop = true
                    }
                }

                if (pastTouchSlop) {
                    if (zoomChange != 1f) {
                        onZoom(zoomChange)
                    }
                    val consume = event.changes.size > 1
                    event.changes.forEach {
                        if (it.positionChanged() || consume) {
                            it.consume()
                        }
                    }
                }
            }
        } while (!canceled && event.changes.any { it.pressed })
        if (pastTouchSlop) {
            onZoomEnd()
        }
    }
}
