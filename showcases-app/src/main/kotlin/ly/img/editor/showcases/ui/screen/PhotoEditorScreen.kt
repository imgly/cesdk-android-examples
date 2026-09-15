package ly.img.editor.showcases.ui.screen

import android.net.Uri
import android.util.SizeF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.net.toUri
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ly.img.editor.Editor
import ly.img.editor.configuration.photo.PhotoConfigurationBuilder
import ly.img.editor.configuration.photo.callback.getOrCreateSceneFromImage
import ly.img.editor.configuration.photo.callback.onCreate
import ly.img.editor.configuration.photo.callback.onLoadAssetSources
import ly.img.editor.configuration.photo.component.rememberNavigationBar
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.showcases.Screen
import ly.img.editor.showcases.Secrets
import ly.img.editor.showcases.ShowcasesViewModel
import ly.img.editor.showcases.decodeBase64
import ly.img.editor.showcases.ui.ext.modifiedCloseEditor

@Composable
fun PhotoEditorScreen(
    viewModel: ShowcasesViewModel,
    baseUri: Uri,
    imageUriAsString: String?,
    sizeAsString: String?,
    onBack: () -> Unit,
) {
    val imageUri = remember(imageUriAsString) {
        runCatching {
            imageUriAsString
                ?.decodeBase64(ifPrefixed = Screen.BASE_64_URL_PREFIX)
                ?.toUri()
        }.getOrNull() ?: "file:///android_asset/image/photo-ui-empty.png".toUri()
    }
    val size = remember(sizeAsString) {
        runCatching {
            sizeAsString
                ?.decodeBase64(ifPrefixed = Screen.BASE_64_URL_PREFIX)
                ?.toSize()
        }.getOrNull()
    }
    Editor(
        license = Secrets.license,
        baseUri = baseUri,
        configuration = {
            EditorConfiguration.remember(::PhotoConfigurationBuilder) {
                onCreate = {
                    onCreate(
                        createScene = {
                            getOrCreateSceneFromImage(
                                imageUri = imageUri,
                                size = size,
                            )
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

internal fun String.toSize(): SizeF? = runCatching {
    val dimensions = split("_")
    require(dimensions.size == 2)
    val widthRatio = dimensions[0].toInt()
    val heightRatio = dimensions[1].toInt()
    val fixedDimension = 1080F
    if (widthRatio < heightRatio) {
        SizeF(fixedDimension, fixedDimension * heightRatio.toFloat() / widthRatio)
    } else {
        SizeF(fixedDimension * widthRatio.toFloat() / heightRatio, fixedDimension)
    }
}.getOrNull()
