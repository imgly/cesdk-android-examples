package ly.img.editor.showcases.ui.screen

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ly.img.editor.Editor
import ly.img.editor.configuration.video.VideoConfigurationBuilder
import ly.img.editor.configuration.video.callback.onCreate
import ly.img.editor.configuration.video.callback.onLoadAssetSources
import ly.img.editor.configuration.video.component.rememberDock
import ly.img.editor.configuration.video.component.rememberNavigationBar
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.modify
import ly.img.editor.core.component.rememberImglyCamera
import ly.img.editor.core.component.systemCamera
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.showcases.Secrets
import ly.img.editor.showcases.ShowcasesViewModel
import ly.img.editor.showcases.ui.ext.modifiedCloseEditor

@Composable
fun VideoEditorScreen(
    viewModel: ShowcasesViewModel,
    baseUri: Uri,
    sceneUri: Uri,
    onBack: () -> Unit,
) {
    Editor(
        license = Secrets.license,
        baseUri = baseUri,
        configuration = {
            EditorConfiguration.remember(::VideoConfigurationBuilder) {
                onCreate = {
                    onCreate(
                        createScene = {
                            getOrLoadScene(sceneUri = sceneUri)
                        },
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
                    )
                }
                assetLibrary = {
                    remember {
                        viewModel.getAssetLibrary(isVideoScene = true)
                    }
                }
                colorPalette = {
                    remember {
                        viewModel.getColorPalette(sceneUri = sceneUri)
                    }
                }
                navigationBar = {
                    rememberNavigationBar().modifiedCloseEditor()
                }
                dock = {
                    val dock = rememberDock()
                    val updatedListBuilder = dock.listBuilder.modify {
                        replace(id = Dock.Button.Id.systemCamera) {
                            Dock.Button.rememberImglyCamera()
                        }
                    }
                    remember(updatedListBuilder) {
                        dock.copy(listBuilder = updatedListBuilder)
                    }
                }
            }
        },
    ) {
        onBack()
    }
}
