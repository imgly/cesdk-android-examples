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
	val engine = Engine
	engine.start(LocalSavedStateRegistryOwner.current)
	// highlight-setup
	// highlight-bind
	AndroidView(factory = { SurfaceView(it).also(engine::bindSurfaceView) })
	// highlight-bind
	// highlight-work
	LaunchedEffect(Unit) {
		engine.scene.get() ?: run {
			val sceneUri = Uri.parse("https://cdn.img.ly/packages/imgly/cesdk-js/latest/assets/templates/cesdk_postcard_1.scene")
			engine.scene.load(sceneUri)
		}
	}
	// highlight-work
	val activity = LocalContext.current as Activity
	val lifecycle = LocalLifecycleOwner.current.lifecycle
	DisposableEffect(Unit) {
		val observer = LifecycleEventObserver { _, event ->
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
