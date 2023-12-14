package ly.img.cesdk.engine

import android.app.Activity
import android.view.SurfaceView
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.viewinterop.AndroidView
import ly.img.cesdk.core.Secrets
import ly.img.cesdk.core.theme.surface1
import ly.img.cesdk.editorui.R
import ly.img.engine.Engine

@Composable
fun EngineCanvasView(
    engine: Engine,
    passTouches: Boolean,
    onLicenseValidationError: (Throwable) -> Unit,
    onMoveStart: () -> Unit,
    onMoveEnd: () -> Unit,
    loadScene: () -> Unit,
) {
    val savedStateRegistryOwner = LocalSavedStateRegistryOwner.current
    val activity = LocalContext.current as Activity
    val surfaceView = remember {
        SurfaceView(activity).apply { id = R.id.editor_surface_view }
    }
    val clearColor = MaterialTheme.colorScheme.surface1
    var onMoveStarted by remember { mutableStateOf(false) }
    AndroidView(
        factory = { surfaceView },
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
    LaunchedEffect(Unit) {
        runCatching {
            engine.start(license = Secrets.license, userId = "showcase-user", savedStateRegistryOwner = savedStateRegistryOwner)
        }.onFailure {
            onLicenseValidationError(it)
        }.onSuccess {
            engine.setClearColor(clearColor)
            engine.bindSurfaceView(surfaceView)
            loadScene()
        }
    }
    DisposableEffect(engine) {
        onDispose {
            if (engine.isEngineRunning()) {
                engine.unbind()
            }
        }
    }
}
