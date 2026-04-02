import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import ly.img.editor.Editor
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.closeEditor
import ly.img.editor.core.component.export
import ly.img.editor.core.component.modify
import ly.img.editor.core.component.redo
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberCloseEditor
import ly.img.editor.core.component.rememberExport
import ly.img.editor.core.component.rememberRedo
import ly.img.editor.core.component.rememberTogglePagesMode
import ly.img.editor.core.component.rememberUndo
import ly.img.editor.core.component.undo
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Music

// Add this composable to your NavHost
@Composable
fun ModifyListBuilderNavigationBarSolution(navController: NavHostController) {
    Editor(
        license = null, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                navigationBar = {
                    NavigationBar.remember {
                        listBuilder = {
                            // highlight-modifyListBuilder
                            // Makes sense to use only with builders that are already available and cannot be modified by you directly.
                            val existingListBuilder = NavigationBar.ListBuilder.remember {
                                aligned(alignment = Alignment.End) {
                                    aligned(alignment = Alignment.Start) {
                                        add { NavigationBar.Button.rememberCloseEditor() }
                                    }
                                    aligned(alignment = Alignment.End) {
                                        add { NavigationBar.Button.rememberUndo() }
                                        add { NavigationBar.Button.rememberRedo() }
                                        add { NavigationBar.Button.rememberTogglePagesMode() }
                                        add { NavigationBar.Button.rememberExport() }
                                    }
                                }
                            }
                            existingListBuilder.modify {
                                // highlight-modifyListBuilder-addFirst
                                addFirst(alignment = Alignment.End) {
                                    NavigationBar.Button.remember {
                                        id = { EditorComponentId("my.package.navigationBar.button.endAligned.first") }
                                        vectorIcon = { IconPack.Music }
                                        textString = { "First Button" }
                                        onClick = {}
                                    }
                                }
                                // highlight-modifyListBuilder-addFirst
                                // highlight-modifyListBuilder-addLast
                                addLast(alignment = Alignment.End) {
                                    NavigationBar.Button.remember {
                                        id = { EditorComponentId("my.package.navigationBar.button.endAligned.last") }
                                        vectorIcon = { IconPack.Music }
                                        textString = { "Last Button" }
                                        onClick = {}
                                    }
                                }
                                // highlight-modifyListBuilder-addLast
                                // highlight-modifyListBuilder-addAfter
                                addAfter(id = NavigationBar.Button.Id.redo) {
                                    NavigationBar.Button.remember {
                                        id = { EditorComponentId("my.package.navigationBar.button.afterRedo") }
                                        vectorIcon = { IconPack.Music }
                                        textString = { "After Redo" }
                                        onClick = {}
                                    }
                                }
                                // highlight-modifyListBuilder-addAfter
                                // highlight-modifyListBuilder-addBefore
                                addBefore(id = NavigationBar.Button.Id.undo) {
                                    NavigationBar.Button.remember {
                                        id = { EditorComponentId("my.package.navigationBar.button.beforeUndo") }
                                        vectorIcon = { IconPack.Music }
                                        textString = { "Before Undo" }
                                        onClick = {}
                                    }
                                }
                                // highlight-modifyListBuilder-addBefore
                                // highlight-modifyListBuilder-replace
                                replace(id = NavigationBar.Button.Id.export) {
                                    NavigationBar.Button.remember {
                                        id = { EditorComponentId("my.package.navigationBar.button.replacedExport") }
                                        vectorIcon = null
                                        text = { "Replaced Export" }
                                        onClick = {}
                                    }
                                }
                                // highlight-modifyListBuilder-replace
                                // highlight-modifyListBuilder-remove
                                remove(id = NavigationBar.Button.Id.closeEditor)
                                // highlight-modifyListBuilder-remove
                            }
                            // highlight-modifyListBuilder
                        }
                    }
                }
            }
        },
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
