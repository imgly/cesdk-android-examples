import android.net.Uri
import android.view.SurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.viewinterop.AndroidView
import ly.img.engine.Engine

@Composable
fun MyComposable() {
    // highlight-android-compose-engine
    val engine = remember { Engine.getInstance(id = "ly.img.engine.example") }
    // highlight-android-compose-engine
    // highlight-android-compose-render-target
    val context = LocalContext.current
    val surfaceView = remember { SurfaceView(context) }
    val savedStateRegistryOwner = LocalSavedStateRegistryOwner.current
    AndroidView(factory = { surfaceView })
    // highlight-android-compose-render-target
    // highlight-android-compose-start
    LaunchedEffect(Unit) {
        engine.start(
            license = null, // pass null or empty for evaluation mode with watermark
            userId = "<your unique user id>",
            savedStateRegistryOwner = savedStateRegistryOwner,
        )
        engine.bindSurfaceView(surfaceView)
        engine.scene.get() ?: run {
            val sceneUri = Uri.parse("https://cdn.img.ly/assets/demo/v1/ly.img.template/templates/cesdk_postcard_1.scene")
            engine.scene.load(sceneUri)
        }
    }
    // highlight-android-compose-start
    // highlight-android-compose-cleanup
    DisposableEffect(Unit) {
        onDispose {
            engine.stop()
        }
    }
    // highlight-android-compose-cleanup
}
