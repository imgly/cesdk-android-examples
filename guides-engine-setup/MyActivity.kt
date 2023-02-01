import android.os.Bundle
import android.net.Uri
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import ly.img.engine.*

class MyActivity : AppCompatActivity() {

	// highlight-setup
	private val engine = Engine

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		engine.start(this)
		// highlight-setup
		val textureView = TextureView(this)
		setContentView(textureView)

		// highlight-bind
		engine.bindTextureView(textureView, savedInstanceState)
		/*
		// Or any of the following
		engine.bindSurfaceView(SurfaceView(this), savedInstanceState)
		engine.bindOffscreen(100, 100, savedInstanceState)
		*/
		// highlight-bind

		// highlight-work
		CoroutineScope(Dispatchers.Main).launch {
			// Check whether scene already exists before loading it again as it might have been restored in engine.start(this)
			engine.scene.get() ?: run {
				val sceneUri = Uri.parse("https://cdn.img.ly/packages/imgly/cesdk-js/latest/assets/templates/cesdk_postcard_1.scene")
				engine.scene.load(sceneUri)
			}
		}
		// highlight-work
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
