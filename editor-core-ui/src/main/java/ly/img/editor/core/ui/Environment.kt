package ly.img.editor.core.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.Uri
import coil.imageLoader
import coil.request.ImageRequest
import ly.img.editor.core.ui.library.data.MediaMetadataExtractor
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

    fun getImageLoader() = context.imageLoader

    fun getEditorDir() = File(context.filesDir, "ly.img.editor")

    fun getEditorCacheDir() = File(context.cacheDir, "ly.img.editor")

    fun getMediaMetadataExtractor() = MediaMetadataExtractor(context)

    fun newImageRequest(uri: Uri) = ImageRequest.Builder(context).data(uri).build()
}
