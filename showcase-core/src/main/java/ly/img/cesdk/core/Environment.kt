package ly.img.cesdk.core

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import coil.imageLoader
import coil.request.ImageRequest
import ly.img.cesdk.core.data.AssetsRepository
import ly.img.cesdk.core.ui.tab_item.TabIconMappings
import ly.img.engine.Engine
import ly.img.engine.SceneMode

@SuppressLint("StaticFieldLeak")
object Environment {
    private lateinit var context: Context
    fun init(application: Application) {
        context = application
    }

    var tabIconMappings = TabIconMappings()
    var sceneMode = SceneMode.DESIGN

    fun getPreferences(): SharedPreferences = context.getSharedPreferences("cesdk_prefs", Context.MODE_PRIVATE)
    fun getImageLoader() = context.imageLoader
    fun getFilesDir() = context.filesDir
    fun newImageRequest(uri: Uri) = ImageRequest.Builder(context).data(uri).build()

    private var engine: Engine? = null
    fun getEngine(): Engine {
        return engine ?: Engine.getInstance(id = "ly.img.cesdk.showcase").also {
            engine = it
        }
    }

    private var assetsRepo: AssetsRepository? = null
    fun getAssetsRepo(): AssetsRepository {
        return assetsRepo ?: AssetsRepository().also {
            assetsRepo = it
        }
    }

    fun clear() {
        assetsRepo = null
        engine = null
    }
}