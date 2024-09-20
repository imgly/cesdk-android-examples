package ly.img.editor.core.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.Uri
import coil.imageLoader
import coil.request.ImageRequest
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.library.data.MediaMetadataExtractor
import ly.img.editor.core.ui.tab_item.TabIconMappings
import ly.img.engine.AssetDefinition
import ly.img.engine.Engine
import java.io.File

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

    fun getImageLoader() = context.imageLoader

    fun getEditorDir() = File(context.filesDir, "ly.img.editor")

    fun getEditorCacheDir() = File(context.cacheDir, "ly.img.editor")

    fun getMediaMetadataExtractor() = MediaMetadataExtractor(context)

    fun newImageRequest(uri: Uri) = ImageRequest.Builder(context).data(uri).build()

    private var engine: Engine? = null

    fun getEngine(): Engine {
        return engine ?: Engine.getInstance(id = "ly.img.editor").also {
            // FIXME: Idling has been disabled till all video issues are resolved
            // Known issues with idlingEnabled -
            // 1. Canvas turns black when going to background and coming back again
            // it.idlingEnabled = true
            engine = it
        }
    }

    fun clear() {
        engine = null
    }
}
