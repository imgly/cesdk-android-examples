package ly.img.cesdk.showcase

import android.app.Application
import ly.img.cesdk.Environment
import ly.img.engine.Engine

class ShowcaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Engine.init(this)
        Environment.init(this)
    }
}
