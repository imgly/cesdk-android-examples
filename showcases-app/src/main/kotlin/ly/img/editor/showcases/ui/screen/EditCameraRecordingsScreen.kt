package ly.img.editor.showcases.ui.screen

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ly.img.camera.core.CameraResult
import ly.img.editor.Editor
import ly.img.editor.configuration.video.VideoConfigurationBuilder
import ly.img.editor.configuration.video.callback.onCreate
import ly.img.editor.configuration.video.callback.onLoadAssetSources
import ly.img.editor.configuration.video.component.rememberNavigationBar
import ly.img.editor.core.EditorScope
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.showcases.Secrets
import ly.img.editor.showcases.ShowcasesViewModel
import ly.img.editor.showcases.ui.ext.modifiedCloseEditor

@Composable
fun EditCameraRecordingsScreen(
    viewModel: ShowcasesViewModel,
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
                        loadAssetSources = {
                            coroutineScope {
                                launch {
                                    onLoadAssetSources()
                                }
                                launch {
                                    viewModel.addRemoteAssetSources(scope = this@Editor, isVideoScene = true)
                                }
                            }
                        },
                        postCreateScene = {
                            if (isNewScene) {
                                postCreateScene(recording)
                            }
                        },
                    )
                }
                assetLibrary = {
                    remember {
                        viewModel.getAssetLibrary(isVideoScene = true)
                    }
                }
                colorPalette = {
                    remember {
                        viewModel.getColorPalette(sceneUri = null)
                    }
                }
                navigationBar = {
                    rememberNavigationBar().modifiedCloseEditor()
                }
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
