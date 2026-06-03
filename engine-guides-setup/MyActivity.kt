import android.net.Uri
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ly.img.engine.Engine

class MyActivity : ComponentActivity() {
    // highlight-android-activity-engine
    private val engine = Engine.getInstance(id = "ly.img.engine.example")
    // highlight-android-activity-engine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textureView = TextureView(this)
        setContentView(textureView)
        // highlight-android-activity-start
        lifecycleScope.launch {
            engine.start(
                license = null, // pass null or empty for evaluation mode with watermark
                userId = "<your unique user id>",
                savedStateRegistryOwner = this@MyActivity,
            )
            bindTextureView(textureView)
            loadScene()
        }
        // highlight-android-activity-start
    }

    // highlight-android-activity-cleanup
    override fun onDestroy() {
        engine.stop()
        super.onDestroy()
    }
    // highlight-android-activity-cleanup

    private fun bindTextureView(textureView: TextureView) {
        // highlight-android-activity-bind-texture-view
        engine.bindTextureView(textureView)
        // highlight-android-activity-bind-texture-view
    }

    private suspend fun loadScene() {
        // highlight-android-activity-load-scene
        // Check whether a scene already exists before loading it again as it might have been restored in engine.start.
        engine.scene.get() ?: run {
            val sceneUri = Uri.parse("https://cdn.img.ly/assets/demo/v1/ly.img.template/templates/cesdk_postcard_1.scene")
            engine.scene.load(sceneUri)
        }
        // highlight-android-activity-load-scene
    }

    private fun bindSurfaceView() {
        // highlight-android-activity-bind-surface-view
        val surfaceView = SurfaceView(this)
        setContentView(surfaceView)
        engine.bindSurfaceView(surfaceView)
        // highlight-android-activity-bind-surface-view
    }

    private fun bindSurfaceHolder(surfaceHolder: SurfaceHolder) {
        // highlight-android-activity-bind-surface-holder
        engine.bindSurfaceHolder(surfaceHolder)
        // highlight-android-activity-bind-surface-holder
    }

    private fun bindOffscreen() {
        // highlight-android-activity-bind-offscreen
        engine.bindOffscreen(width = 100, height = 100)
        // highlight-android-activity-bind-offscreen
    }
}
