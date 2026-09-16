import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import ly.img.editor.Editor
import ly.img.editor.core.UnstableEditorApi
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberCloseEditor
import ly.img.editor.core.component.rememberSystemGallery
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.configuration.then
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.library.data.SystemGalleryAssetSource
import ly.img.editor.core.library.data.SystemGalleryConfiguration
import ly.img.editor.core.library.data.SystemGalleryPermission
import ly.img.engine.Engine

@Composable
fun PhotoRollEditorSolution(
    license: String,
    enableFullGalleryAccess: Boolean,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            val galleryAccessConfiguration = if (enableFullGalleryAccess) {
                // highlight-android-enable-full-gallery-access
                EditorConfiguration.remember {
                    onLoaded = {
                        registerGallerySources(
                            engine = editorContext.engine,
                            context = editorContext.activity,
                        )
                        @OptIn(UnstableEditorApi::class)
                        SystemGalleryPermission.setMode(SystemGalleryConfiguration.Enabled)
                    }
                }
                // highlight-android-enable-full-gallery-access
            } else {
                // highlight-android-register-gallery-sources
                EditorConfiguration.remember {
                    onLoaded = {
                        registerGallerySources(
                            engine = editorContext.engine,
                            context = editorContext.activity,
                        )
                        @OptIn(UnstableEditorApi::class)
                        SystemGalleryPermission.setMode(SystemGalleryConfiguration.Disabled)
                    }
                }
                // highlight-android-register-gallery-sources
            }
            galleryAccessConfiguration.then {
                // highlight-android-show-gallery-button
                dock = {
                    Dock.remember {
                        listBuilder = {
                            Dock.ListBuilder.remember {
                                add { Dock.Button.rememberSystemGallery() }
                            }
                        }
                    }
                }
                // highlight-android-show-gallery-button
                // highlight-android-audiovisual-asset-library
                assetLibrary = {
                    remember { AssetLibrary.getDefault(includeAVResources = true) }
                }
                // highlight-android-audiovisual-asset-library
                navigationBar = {
                    NavigationBar.remember {
                        listBuilder = {
                            NavigationBar.ListBuilder.remember {
                                aligned(alignment = Alignment.Start) {
                                    add { NavigationBar.Button.rememberCloseEditor() }
                                }
                            }
                        }
                    }
                }
            }
        },
        onClose = onClose,
    )
}

// highlight-android-gallery-source-helper
fun registerGallerySources(
    engine: Engine,
    context: Context,
) {
    val existingSourceIds = engine.asset.findAllSources().toSet()
    listOf(
        AssetSourceType.GalleryImage,
        AssetSourceType.GalleryVideo,
        AssetSourceType.GalleryAllVisuals,
    ).forEach { sourceType ->
        if (sourceType.sourceId !in existingSourceIds) {
            engine.asset.addSource(SystemGalleryAssetSource(context, sourceType))
        }
    }
}
// highlight-android-gallery-source-helper
