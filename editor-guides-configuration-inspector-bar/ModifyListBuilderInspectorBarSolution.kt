import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.component.EditorComponent.ListBuilder.Companion.modify
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.crop
import ly.img.editor.core.component.delete
import ly.img.editor.core.component.formatText
import ly.img.editor.core.component.layer
import ly.img.editor.rememberForDesign

// Add this composable to your NavHost
@Composable
fun ModifyListBuilderInspectorBarSolution(navController: NavHostController) {
    val engineConfiguration = EngineConfiguration.rememberForDesign(
        license = "<your license here>", // pass null or empty for evaluation mode with watermark
    )

    val editorConfiguration = EditorConfiguration.rememberForDesign(
        inspectorBar = {
            InspectorBar.remember(
                // highlight-modifyListBuilder
                // highlight-modifyListBuilderSignature
                listBuilder = InspectorBar.ListBuilder.remember().modify {
                    // highlight-modifyListBuilderSignature
                    // highlight-modifyListBuilder-addFirst
                    addFirst {
                        InspectorBar.Button.remember(
                            id = EditorComponentId("my.package.inspectorBar.button.first"),
                            vectorIcon = null,
                            text = { "First Button" },
                            onClick = {},
                        )
                    }
                    // highlight-modifyListBuilder-addFirst
                    // highlight-modifyListBuilder-addLast
                    addLast {
                        InspectorBar.Button.remember(
                            id = EditorComponentId("my.package.inspectorBar.button.last"),
                            vectorIcon = null,
                            text = { "Last Button" },
                            onClick = {},
                        )
                    }
                    // highlight-modifyListBuilder-addLast
                    // highlight-modifyListBuilder-addAfter
                    addAfter(id = InspectorBar.Button.Id.layer) {
                        InspectorBar.Button.remember(
                            id = EditorComponentId("my.package.inspectorBar.button.afterLayer"),
                            vectorIcon = null,
                            text = { "After Layer" },
                            onClick = {},
                        )
                    }
                    // highlight-modifyListBuilder-addAfter
                    // highlight-modifyListBuilder-addBefore
                    addBefore(id = InspectorBar.Button.Id.crop) {
                        InspectorBar.Button.remember(
                            id = EditorComponentId("my.package.inspectorBar.button.beforeCrop"),
                            vectorIcon = null,
                            text = { "Before Crop" },
                            onClick = {},
                        )
                    }
                    // highlight-modifyListBuilder-addBefore
                    // highlight-modifyListBuilder-replace
                    replace(id = InspectorBar.Button.Id.formatText) {
                        InspectorBar.Button.remember(
                            id = EditorComponentId("my.package.inspectorBar.button.replacedFormatText"),
                            vectorIcon = null,
                            text = { "Replaced Format Text" },
                            onClick = {},
                        )
                    }
                    // highlight-modifyListBuilder-replace
                    // highlight-modifyListBuilder-remove
                    remove(id = InspectorBar.Button.Id.delete)
                    // highlight-modifyListBuilder-remove
                },
                // highlight-modifyListBuilder
            )
        },
    )
    DesignEditor(
        engineConfiguration = engineConfiguration,
        editorConfiguration = editorConfiguration,
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
