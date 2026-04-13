import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ly.img.editor.Editor
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.crop
import ly.img.editor.core.component.delete
import ly.img.editor.core.component.formatText
import ly.img.editor.core.component.layer
import ly.img.editor.core.component.modify
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberCrop
import ly.img.editor.core.component.rememberDelete
import ly.img.editor.core.component.rememberFormatText
import ly.img.editor.core.component.rememberLayer
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember

// Add this composable to your NavHost
@Composable
fun ModifyListBuilderInspectorBarSolution(navController: NavHostController) {
    Editor(
        license = null, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                inspectorBar = {
                    InspectorBar.remember {
                        listBuilder = {
                            // highlight-modifyListBuilder
                            // Makes sense to use only with builders that are already available and cannot be modified by you directly.
                            val existingListBuilder = InspectorBar.ListBuilder.remember {
                                add { InspectorBar.Button.rememberLayer() }
                                add { InspectorBar.Button.rememberCrop() }
                                add { InspectorBar.Button.rememberFormatText() }
                                add { InspectorBar.Button.rememberDelete() }
                            }
                            existingListBuilder.modify {
                                // highlight-modifyListBuilder-addFirst
                                addFirst {
                                    InspectorBar.Button.remember {
                                        id = { EditorComponentId("my.package.inspectorBar.button.first") }
                                        vectorIcon = null
                                        textString = { "First Button" }
                                        onClick = {}
                                    }
                                }
                                // highlight-modifyListBuilder-addFirst
                                // highlight-modifyListBuilder-addLast
                                addLast {
                                    InspectorBar.Button.remember {
                                        id = { EditorComponentId("my.package.inspectorBar.button.last") }
                                        vectorIcon = null
                                        textString = { "Last Button" }
                                        onClick = {}
                                    }
                                }
                                // highlight-modifyListBuilder-addLast
                                // highlight-modifyListBuilder-addAfter
                                addAfter(id = InspectorBar.Button.Id.layer) {
                                    InspectorBar.Button.remember {
                                        id = { EditorComponentId("my.package.inspectorBar.button.afterLayer") }
                                        vectorIcon = null
                                        textString = { "After Layer" }
                                        onClick = {}
                                    }
                                }
                                // highlight-modifyListBuilder-addAfter
                                // highlight-modifyListBuilder-addBefore
                                addBefore(id = InspectorBar.Button.Id.crop) {
                                    InspectorBar.Button.remember {
                                        id = { EditorComponentId("my.package.inspectorBar.button.beforeCrop") }
                                        vectorIcon = null
                                        textString = { "Before Crop" }
                                        onClick = {}
                                    }
                                }
                                // highlight-modifyListBuilder-addBefore
                                // highlight-modifyListBuilder-replace
                                replace(id = InspectorBar.Button.Id.formatText) {
                                    InspectorBar.Button.remember {
                                        id = { EditorComponentId("my.package.inspectorBar.button.replacedFormatText") }
                                        vectorIcon = null
                                        textString = { "Replaced Format Text" }
                                        onClick = {}
                                    }
                                }
                                // highlight-modifyListBuilder-replace
                                // highlight-modifyListBuilder-remove
                                remove(id = InspectorBar.Button.Id.delete)
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
