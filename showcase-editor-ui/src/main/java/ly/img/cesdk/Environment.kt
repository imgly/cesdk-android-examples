package ly.img.cesdk

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import coil.imageLoader
import coil.request.ImageRequest
import ly.img.cesdk.core.components.tab_item.TabIconMappings
import ly.img.cesdk.library.data.AssetsRepository
import ly.img.engine.Engine

@SuppressLint("StaticFieldLeak")
object Environment {
    private lateinit var context: Context
    fun init(application: Application) {
        context = application
    }

    var tabIconMappings = TabIconMappings()

    fun getPreferences(): SharedPreferences = context.getSharedPreferences("cesdk_prefs", Context.MODE_PRIVATE)
    fun getImageLoader() = context.imageLoader
    fun getFilesDir() = context.filesDir
    fun newImageRequest(uri: String) = ImageRequest.Builder(context).data(uri).build()

    private var engine: Engine? = null
    fun getEngine(): Engine {
        return engine ?: Engine(id = "ly.img.cesdk.showcase").also {
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