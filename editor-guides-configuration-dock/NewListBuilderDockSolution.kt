import androidx.compose.runtime.Composable
import ly.img.editor.Editor
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberElementsLibrary
import ly.img.editor.core.component.rememberImagesLibrary
import ly.img.editor.core.component.rememberStickersLibrary
import ly.img.editor.core.component.rememberSystemCamera
import ly.img.editor.core.component.rememberSystemGallery
import ly.img.editor.core.component.rememberTextLibrary
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.iconpack.AddShape
import ly.img.editor.core.iconpack.IconPack

@Composable
fun NewListBuilderDockSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                dock = {
                    Dock.remember {
                        listBuilder = {
                            // highlight-android-new-list-builder
                            Dock.ListBuilder.remember {
                                add {
                                    Dock.Button.remember {
                                        id = { EditorComponentId("my.package.dock.button.custom") }
                                        vectorIcon = { IconPack.AddShape }
                                        textString = { "Custom" }
                                        onClick = {}
                                    }
                                }
                                add { Dock.Button.rememberSystemGallery() }
                                add { Dock.Button.rememberSystemCamera() }
                                add { Dock.Button.rememberElementsLibrary() }
                                add { Dock.Button.rememberStickersLibrary() }
                                add { Dock.Button.rememberImagesLibrary() }
                                add { Dock.Button.rememberTextLibrary() }
                            }
                            // highlight-android-new-list-builder
                        }
                    }
                }
            }
        },
        onClose = onClose,
    )
}
