import android.net.Uri
import android.os.Bundle
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.Engine

class MyActivity : AppCompatActivity() {
    // highlight-setup
    private val engine = Engine.getInstance(id = "ly.img.engine.example")
    // highlight-setup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textureView = TextureView(this)
        setContentView(textureView)
        CoroutineScope(Dispatchers.Main).launch {
            // highlight-start
            engine.start(
                license = "<your license here>",
                userId = "<your unique user id>",
                savedStateRegistryOwner = this@MyActivity,
            )
            // highlight-start
            // highlight-bind
            engine.bindTextureView(textureView)
            /*
            // Or any of the following
            engine.bindSurfaceView(SurfaceView(this@MyActivity))
            engine.bindOffscreen(100, 100)
             */

            // highlight-bind
            // highlight-work
            // Check whether scene already exists before loading it again as it might have been restored in engine.start).
            engine.scene.get() ?: run {
                val sceneUri = Uri.parse("https://cdn.img.ly/assets/demo/v1/ly.img.template/templates/cesdk_postcard_1.scene")
                engine.scene.load(sceneUri)
            }
            // highlight-work
        }
    }

    override fun onDestroy() {
        // highlight-unbind
        engine.unbind()
        // highlight-unbind
        // highlight-stop
        if (isFinishing) {
            engine.stop()
        }
        // highlight-stop
        super.onDestroy()
    }
}
