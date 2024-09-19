import android.app.Activity
import android.net.Uri
import android.view.SurfaceView
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import ly.img.engine.*

@Composable
fun MyComposable() {
    // highlight-setup
    val engine = remember { Engine.getInstance(id = "ly.img.engine.example") }
    // highlight-setup
    val activity = LocalContext.current as Activity
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val surfaceView = remember { SurfaceView(activity) }
    val savedStateRegistryOwner = LocalSavedStateRegistryOwner.current
    AndroidView(factory = { surfaceView })
    LaunchedEffect(Unit) {
        // highlight-start
        engine.start(
            license = "<your license here>",
            userId = "<your unique user id>",
            savedStateRegistryOwner = savedStateRegistryOwner,
        )
        // highlight-start
        // highlight-bind
        engine.bindSurfaceView(surfaceView)
        // highlight-bind
        // highlight-work
        engine.scene.get() ?: run {
            val sceneUri = Uri.parse("https://cdn.img.ly/assets/demo/v1/ly.img.template/templates/cesdk_postcard_1.scene")
            engine.scene.load(sceneUri)
        }
        // highlight-work
    }
    DisposableEffect(Unit) {
        val observer =
            LifecycleEventObserver { _, event ->
                when {
                    // highlight-stop
                    event == Lifecycle.Event.ON_DESTROY && !activity.isChangingConfigurations -> engine.stop()
                    // highlight-stop
                    // highlight-unbind
                    event == Lifecycle.Event.ON_DESTROY -> engine.unbind()
                    // highlight-unbind
                }
            }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
}
