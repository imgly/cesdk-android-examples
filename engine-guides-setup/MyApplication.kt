import android.app.Application
import ly.img.engine.Engine

// highlight-android-application-init
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Engine.init(application = this)
    }
}
// highlight-android-application-init
