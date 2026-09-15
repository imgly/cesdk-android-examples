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

// Add this composable to your NavHost
@Composable
fun ModifyListBuilderDockSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                dock = {
                    Dock.remember {
                        listBuilder = {
                            // highlight-modifyListBuilder
                            // Makes sense to use only with builders that are already available and cannot be modified by you directly.
                            val existingListBuilder = Dock.ListBuilder.remember {
                                add { Dock.Button.rememberSystemGallery() }
                                add { Dock.Button.rememberSystemCamera() }
                                add { Dock.Button.rememberTextLibrary() }
                                add { Dock.Button.rememberShapesLibrary() }
                            }
                            existingListBuilder.modify {
                                // highlight-modifyListBuilder-addFirst
                                addFirst {
                                    Dock.Button.remember {
                                        id = { EditorComponentId("my.package.dock.button.first") }
                                        vectorIcon = null
                                        textString = { "First Button" }
                                        onClick = {}
                                    }
                                }
                                // highlight-modifyListBuilder-addFirst
                                // highlight-modifyListBuilder-addLast
                                addLast {
                                    Dock.Button.remember {
                                        id = { EditorComponentId("my.package.dock.button.last") }
                                        vectorIcon = null
                                        textString = { "Last Button" }
                                        onClick = {}
                                    }
                                }
                                // highlight-modifyListBuilder-addLast
                                // highlight-modifyListBuilder-addAfter
                                addAfter(id = Dock.Button.Id.systemGallery) {
                                    Dock.Button.remember {
                                        id = { EditorComponentId("my.package.dock.button.afterSystemGallery") }
                                        vectorIcon = null
                                        textString = { "After System Gallery" }
                                        onClick = {}
                                    }
                                }
                                // highlight-modifyListBuilder-addAfter
                                // highlight-modifyListBuilder-addBefore
                                addBefore(id = Dock.Button.Id.systemCamera) {
                                    Dock.Button.remember {
                                        id = { EditorComponentId("my.package.dock.button.beforeSystemCamera") }
                                        vectorIcon = null
                                        textString = { "Before System Camera" }
                                        onClick = {}
                                    }
                                }
                                // highlight-modifyListBuilder-addBefore
                                // highlight-modifyListBuilder-replace
                                replace(id = Dock.Button.Id.textLibrary) {
                                    Dock.Button.remember {
                                        id = { EditorComponentId("my.package.dock.button.replacedTextLibrary") }
                                        vectorIcon = null
                                        text = { "Replaced Text Library" }
                                        onClick = {}
                                    }
                                }
                                // highlight-modifyListBuilder-replace
                                // highlight-modifyListBuilder-remove
                                remove(id = Dock.Button.Id.shapesLibrary)
                                // highlight-modifyListBuilder-remove
                            }
                            // highlight-modifyListBuilder
                        }
                    }
                }
            }
        },
        onClose = onClose,
    )
}
