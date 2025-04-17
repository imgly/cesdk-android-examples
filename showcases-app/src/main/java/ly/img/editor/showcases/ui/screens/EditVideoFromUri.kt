package ly.img.editor.showcases.ui.screens

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import ly.img.editor.EditorConfiguration
import ly.img.editor.EditorDefaults
import ly.img.editor.EngineConfiguration
import ly.img.editor.VideoEditor
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.rememberForVideo
import ly.img.editor.showcases.Secrets

@Composable
fun EditVideoFromUri(
    uri: Uri,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val size =
        remember {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!.toInt()
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!.toInt()
            retriever.release()
            Size(width.toFloat(), height.toFloat())
        }
    Box(
        modifier =
            Modifier.fillMaxSize(),
    ) {
        val engineConfig =
            EngineConfiguration.remember(
                license = Secrets.license,
                onCreate = {
                    val eventHandler = editorContext.eventHandler
                    val engine = editorContext.engine
                    EditorDefaults.onCreate(
                        engine = engine,
                        eventHandler = eventHandler,
                        sceneUri = EngineConfiguration.defaultVideoSceneUri,
                    )

                    val currentPage = engine.scene.getCurrentPage()!!
                    engine.block.setWidth(currentPage, size.width)
                    engine.block.setHeight(currentPage, size.height)

                    eventHandler.send(
                        EditorEvent.AddUriToScene(
                            uploadAssetSourceType = AssetSourceType.VideoUploads,
                            uri = uri,
                        ),
                    )
                },
            )

        val editorConfig = EditorConfiguration.rememberForVideo()
        VideoEditor(
            engineConfiguration = engineConfig,
            editorConfiguration = editorConfig,
        ) {
            onBack()
        }
    }
}
