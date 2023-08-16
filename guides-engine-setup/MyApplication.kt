import android.app.Application
import ly.img.engine.Engine

class MyApplication : Application() {

	override fun onCreate() {
		super.onCreate()
		// highlight-engine-init
		Engine.init(this)
		// highlight-engine-init
	}
}
