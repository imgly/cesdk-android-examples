import androidx.compose.runtime.Composable
import ly.img.editor.Editor
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.modify
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberShapesLibrary
import ly.img.editor.core.component.rememberSystemCamera
import ly.img.editor.core.component.rememberSystemGallery
import ly.img.editor.core.component.rememberTextLibrary
import ly.img.editor.core.component.shapesLibrary
import ly.img.editor.core.component.systemCamera
import ly.img.editor.core.component.systemGallery
import ly.img.editor.core.component.textLibrary
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember

@Composable
fun ModifyListBuilderDockSolution(
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
                            // highlight-android-modify-list-builder
                            val existingListBuilder = Dock.ListBuilder.remember {
                                add { Dock.Button.rememberSystemGallery() }
                                add { Dock.Button.rememberSystemCamera() }
                                add { Dock.Button.rememberTextLibrary() }
                                add { Dock.Button.rememberShapesLibrary() }
                            }
                            existingListBuilder.modify {
                                addFirst {
                                    Dock.Button.remember {
                                        id = { EditorComponentId("my.package.dock.button.first") }
                                        vectorIcon = null
                                        textString = { "First Button" }
                                        onClick = {}
                                    }
                                }
                                addLast {
                                    Dock.Button.remember {
                                        id = { EditorComponentId("my.package.dock.button.last") }
                                        vectorIcon = null
                                        textString = { "Last Button" }
                                        onClick = {}
                                    }
                                }
                                addAfter(id = Dock.Button.Id.systemGallery, failIfNotFound = true) {
                                    Dock.Button.remember {
                                        id = { EditorComponentId("my.package.dock.button.afterSystemGallery") }
                                        vectorIcon = null
                                        textString = { "After System Gallery" }
                                        onClick = {}
                                    }
                                }
                                addBefore(id = Dock.Button.Id.systemCamera, failIfNotFound = true) {
                                    Dock.Button.remember {
                                        id = { EditorComponentId("my.package.dock.button.beforeSystemCamera") }
                                        vectorIcon = null
                                        textString = { "Before System Camera" }
                                        onClick = {}
                                    }
                                }
                                replace(id = Dock.Button.Id.textLibrary, failIfNotFound = true) {
                                    Dock.Button.remember {
                                        id = { EditorComponentId("my.package.dock.button.replacedTextLibrary") }
                                        vectorIcon = null
                                        textString = { "Replaced Text Library" }
                                        onClick = {}
                                    }
                                }
                                remove(id = Dock.Button.Id.shapesLibrary, failIfNotFound = true)
                            }
                            // highlight-android-modify-list-builder
                        }
                    }
                }
            }
        },
        onClose = onClose,
    )
}
