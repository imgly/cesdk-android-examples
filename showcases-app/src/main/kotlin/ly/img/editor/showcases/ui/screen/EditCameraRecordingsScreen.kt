package ly.img.editor.showcases.ui.screen

import android.net.Uri
import androidx.compose.runtime.Composable
import kotlinx.coroutines.android.awaitFrame
import ly.img.camera.core.CameraResult
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
fun EditCameraRecordingsScreen(
    baseUri: Uri,
    recording: CameraResult.Record,
    onBack: () -> Unit,
) {
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
                                postCreateScene(recording)
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

private suspend fun EditorScope.postCreateScene(recording: CameraResult.Record) {
    awaitFrame()
    editorContext.eventHandler.send(
        EditorEvent.AddCameraRecordingsToScene(
            uploadAssetSourceType = AssetSourceType.VideoUploads,
            recordings = recording.recordings
                .flatMap { recording ->
                    recording.videos
                        .map { it.uri to recording.duration }
                },
        ),
    )
}
