package ly.img.editor.base.engine

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
import ly.img.editor.base.R
import ly.img.editor.core.theme.surface1
import ly.img.editor.core.ui.utils.activity
import ly.img.engine.Engine

@Composable
fun EngineCanvasView(
    license: String,
    userId: String?,
    engine: Engine,
    isCanvasVisible: Boolean,
    passTouches: Boolean,
    onLicenseValidationError: (Throwable) -> Unit,
    onMoveStart: () -> Unit,
    onMoveEnd: () -> Unit,
    loadScene: () -> Unit,
    onTouch: () -> Unit = {},
) {
    val savedStateRegistryOwner = LocalSavedStateRegistryOwner.current
    val activity =
        requireNotNull(LocalContext.current.activity) {
            "Unable to find the activity. This is an internal error. Please report this issue."
        }
    val surfaceView =
        remember {
            SurfaceView(activity).apply { id = R.id.editor_surface_view }
        }
    surfaceView.alpha = if (isCanvasVisible) 1F else 0F
    val clearColor = MaterialTheme.colorScheme.surface1
    var onMoveStarted by remember { mutableStateOf(false) }
    AndroidView(
        factory = { surfaceView },
        modifier =
            Modifier
                .pointerInput(passTouches) {
                    forEachGesture {
                        awaitPointerEventScope {
                            if (passTouches) {
                                awaitFirstDown(requireUnconsumed = false)
                                do {
                                    val event: PointerEvent = awaitPointerEvent(pass = PointerEventPass.Final)
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
                        onTouch()
                    }
                },
    )
    LaunchedEffect(Unit) {
        runCatching {
            engine.start(license = license, userId = userId, savedStateRegistryOwner = savedStateRegistryOwner)
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
