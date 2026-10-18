package ly.img.editor.showcases.ui.screen

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.platform.LocalContext
import ly.img.editor.Editor
import ly.img.editor.configuration.video.VideoConfigurationBuilder
import ly.img.editor.configuration.video.callback.onCreate
import ly.img.editor.core.EditorScope
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.configuration.then
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.showcases.Secrets
import ly.img.editor.showcases.plugin.ShowcasesPlugin

@Composable
fun EditVideoFromUriScreen(
    baseUri: Uri,
    videoUri: Uri,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val size = remember(videoUri) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, videoUri)
            val frameAtTime = requireNotNull(retriever.frameAtTime)
            val width = frameAtTime.width
            val height = frameAtTime.height
            Size(width.toFloat(), height.toFloat())
        } catch (e: Exception) {
            e.printStackTrace()
            Size.Unspecified
        } finally {
            retriever.release()
        }
    }
    Editor(
        license = Secrets.license,
        baseUri = baseUri,
        configuration = {
            EditorConfiguration.remember(::VideoConfigurationBuilder) {
                onCreate = {
                    val isNewScene = editorContext.engine.scene.get() == null
                    onCreate(
                        postCreateScene = {
                            if (isNewScene) {
                                postCreateScene(videoUri, size)
                            }
                        },
                    )
                }
            }.then(::ShowcasesPlugin) {
                this.isVideoScene = true
            }
        },
    ) {
        onBack()
    }
}

private fun EditorScope.postCreateScene(
    uri: Uri,
    size: Size,
) {
    val currentPage = requireNotNull(editorContext.engine.scene.getCurrentPage())
    if (size.isSpecified) {
        editorContext.engine.block.setWidth(currentPage, size.width)
        editorContext.engine.block.setHeight(currentPage, size.height)
    }

    editorContext.eventHandler.send(
        EditorEvent.AddUriToScene(
            uploadAssetSourceType = AssetSourceType.VideoUploads,
            uri = uri,
            addToBackgroundTrack = true,
        ),
    )
}
