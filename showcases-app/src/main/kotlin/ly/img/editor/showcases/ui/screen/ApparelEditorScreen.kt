package ly.img.editor.showcases.ui.screen

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ly.img.editor.Editor
import ly.img.editor.configuration.apparel.ApparelConfigurationBuilder
import ly.img.editor.configuration.apparel.callback.onCreate
import ly.img.editor.configuration.apparel.callback.onLoadAssetSources
import ly.img.editor.configuration.apparel.component.rememberNavigationBar
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.showcases.Secrets
import ly.img.editor.showcases.ShowcasesViewModel
import ly.img.editor.showcases.ui.ext.modifiedCloseEditor

@Composable
fun ApparelEditorScreen(
    viewModel: ShowcasesViewModel,
    baseUri: Uri,
    sceneUri: Uri,
    onBack: () -> Unit,
) {
    Editor(
        license = Secrets.license,
        baseUri = baseUri,
        configuration = {
            EditorConfiguration.remember(::ApparelConfigurationBuilder) {
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
                                    viewModel.addRemoteAssetSources(scope = this@Editor, isVideoScene = false)
                                }
                            }
                        },
                    )
                }
                assetLibrary = {
                    remember {
                        viewModel.getAssetLibrary(isVideoScene = false)
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
            }
        },
    ) {
        onBack()
    }
}
