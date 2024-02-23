package ly.img.editor.core.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import coil.imageLoader
import coil.request.ImageRequest
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.library.data.AssetsRepository
import ly.img.editor.core.ui.tab_item.TabIconMappings
import ly.img.engine.AssetDefinition
import ly.img.engine.Engine

@SuppressLint("StaticFieldLeak")
object Environment {
    private lateinit var context: Context

    fun init(application: Application) {
        if (::context.isInitialized.not()) {
            context = application.applicationContext
            Engine.init(application)
        }
    }

    var tabIconMappings = TabIconMappings()
    var assetLibrary: AssetLibrary? = null
    var onUpload: (suspend AssetDefinition.(Engine, UploadAssetSourceType) -> AssetDefinition)? = null
    var onClose: (suspend (Engine, EditorEventHandler) -> Unit)? = null

    fun getPreferences(): SharedPreferences =
        context.getSharedPreferences(
            "cesdk_prefs",
            Context.MODE_PRIVATE,
        )

    fun getImageLoader() = context.imageLoader

    fun getFilesDir() = context.filesDir

    fun newImageRequest(uri: Uri) = ImageRequest.Builder(context).data(uri).build()

    private var engine: Engine? = null

    fun getEngine(): Engine {
        return engine ?: Engine.getInstance(id = "ly.img.editor").also {
            it.idlingEnabled = true
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
