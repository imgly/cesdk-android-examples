import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponent.ListBuilder.Companion.modify
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.rememberForDesign
import ly.img.editor.core.component.shapesLibrary
import ly.img.editor.core.component.systemCamera
import ly.img.editor.core.component.systemGallery
import ly.img.editor.core.component.textLibrary
import ly.img.editor.rememberForDesign

// Add this composable to your NavHost
@Composable
fun ModifyListBuilderDockSolution(navController: NavHostController) {
    val engineConfiguration = EngineConfiguration.rememberForDesign(
        license = "<your license here>",
    )

    val editorConfiguration = EditorConfiguration.rememberForDesign(
        dock = {
            Dock.remember(
                // highlight-modifyListBuilder
                // highlight-modifyListBuilderSignature
                listBuilder = Dock.ListBuilder.rememberForDesign().modify {
                    // highlight-modifyListBuilderSignature
                    // highlight-modifyListBuilder-addFirst
                    addFirst {
                        Dock.Button.remember(
                            id = EditorComponentId("my.package.dock.button.first"),
                            vectorIcon = null,
                            text = { "First Button" },
                            onClick = {},
                        )
                    }
                    // highlight-modifyListBuilder-addFirst
                    // highlight-modifyListBuilder-addLast
                    addLast {
                        Dock.Button.remember(
                            id = EditorComponentId("my.package.dock.button.last"),
                            vectorIcon = null,
                            text = { "Last Button" },
                            onClick = {},
                        )
                    }
                    // highlight-modifyListBuilder-addLast
                    // highlight-modifyListBuilder-addAfter
                    addAfter(id = Dock.Button.Id.systemGallery) {
                        Dock.Button.remember(
                            id = EditorComponentId("my.package.dock.button.afterSystemGallery"),
                            vectorIcon = null,
                            text = { "After System Gallery" },
                            onClick = {},
                        )
                    }
                    // highlight-modifyListBuilder-addAfter
                    // highlight-modifyListBuilder-addBefore
                    addBefore(id = Dock.Button.Id.systemCamera) {
                        Dock.Button.remember(
                            id = EditorComponentId("my.package.dock.button.beforeSystemCamera"),
                            vectorIcon = null,
                            text = { "Before System Camera" },
                            onClick = {},
                        )
                    }
                    // highlight-modifyListBuilder-addBefore
                    // highlight-modifyListBuilder-replace
                    replace(id = Dock.Button.Id.textLibrary) {
                        Dock.Button.remember(
                            id = EditorComponentId("my.package.dock.button.replacedTextLibrary"),
                            vectorIcon = null,
                            text = { "Replaced Text Library" },
                            onClick = {},
                        )
                    }
                    // highlight-modifyListBuilder-replace
                    // highlight-modifyListBuilder-remove
                    remove(id = Dock.Button.Id.shapesLibrary)
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
