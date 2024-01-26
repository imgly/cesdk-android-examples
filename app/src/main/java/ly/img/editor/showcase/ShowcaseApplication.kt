package ly.img.editor.showcase

import android.app.Application
import ly.img.editor.core.ui.Environment
import ly.img.engine.Engine

class ShowcaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Engine.init(this)
        Environment.init(this)
    }
}
