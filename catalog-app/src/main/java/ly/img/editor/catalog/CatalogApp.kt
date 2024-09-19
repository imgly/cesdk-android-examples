package ly.img.editor.catalog

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

class CatalogApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.crashlytics.setCustomKey("build_name", CatalogBuildConfig.BUILD_NAME)
    }
}
