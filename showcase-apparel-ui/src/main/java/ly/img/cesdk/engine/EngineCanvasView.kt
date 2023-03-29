package ly.img.cesdk.engine

import android.view.SurfaceView
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.viewinterop.AndroidView
import ly.img.cesdk.core.theme.surface1
import ly.img.engine.Engine

@Composable
fun EngineCanvasView(
    engine: Engine,
    passTouches: Boolean,
    onMoveStart: () -> Unit,
    onMoveEnd: () -> Unit,
    loadScene: () -> Unit,
) {
    engine.start(LocalSavedStateRegistryOwner.current)
    engine.setClearColor(MaterialTheme.colorScheme.surface1)

    var onMoveStarted by remember { mutableStateOf(false) }
    AndroidView(
        factory = {
            val view = SurfaceView(it)
            engine.bindSurfaceView(view)
            view
        },
        modifier = Modifier
            .pointerInput(passTouches) {
                forEachGesture {
                    awaitPointerEventScope {
                        if (passTouches) {
                            awaitFirstDown(requireUnconsumed = false)
                            do {
                                val event: PointerEvent = awaitPointerEvent()
                                if (!onMoveStarted) {
                                    onMoveStarted = true
                                    onMoveStart()
                                }
                            } while (event.changes.any { it.pressed })
                            if (onMoveStarted) {
                                onMoveStarted = false
                                onMoveEnd()
                            }
                        } else {
                            val awaitPointerEvent = awaitPointerEvent(pass = PointerEventPass.Initial)
                            awaitPointerEvent.changes.forEach {
                                it.consume()
                            }
                        }
                    }
                }
            }
    )
    DisposableEffect(engine) {
        loadScene()
        onDispose {
            if (engine.isEngineRunning()) {
                engine.unbind()
            }
        }
    }
}