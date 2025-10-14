import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.component.CanvasMenu
import ly.img.editor.core.component.EditorComponent.ListBuilder.Companion.modify
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.bringForward
import ly.img.editor.core.component.delete
import ly.img.editor.core.component.duplicate
import ly.img.editor.core.component.rememberDuplicate
import ly.img.editor.core.component.sendBackward
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Music
import ly.img.editor.rememberForDesign

// Add this composable to your NavHost
@Composable
fun ModifyListBuilderCanvasMenuSolution(navController: NavHostController) {
    val engineConfiguration = EngineConfiguration.rememberForDesign(
        license = "<your license here>",
    )

    val editorConfiguration = EditorConfiguration.rememberForDesign(
        canvasMenu = {
            CanvasMenu.remember(
                // highlight-modifyListBuilder
                // highlight-modifyListBuilderSignature
                listBuilder = CanvasMenu.ListBuilder.remember().modify {
                    // highlight-modifyListBuilderSignature
                    // highlight-modifyListBuilder-addFirst
                    addFirst {
                        CanvasMenu.Button.remember(
                            id = EditorComponentId("my.package.canvasMenu.button.first"),
                            vectorIcon = null,
                            text = { "First Button" },
                            onClick = {},
                        )
                    }
                    // highlight-modifyListBuilder-addFirst
                    // highlight-modifyListBuilder-addLast
                    addLast {
                        CanvasMenu.Button.remember(
                            id = EditorComponentId("my.package.canvasMenu.button.last"),
                            vectorIcon = null,
                            text = { "Last Button" },
                            onClick = {},
                        )
                    }
                    // highlight-modifyListBuilder-addLast
                    // highlight-modifyListBuilder-addAfter
                    addAfter(id = CanvasMenu.Button.Id.bringForward) {
                        CanvasMenu.Button.remember(
                            id = EditorComponentId("my.package.canvasMenu.button.afterBringForward"),
                            vectorIcon = null,
                            text = { "After Bring Forward" },
                            onClick = {},
                        )
                    }
                    // highlight-modifyListBuilder-addAfter
                    // highlight-modifyListBuilder-addBefore
                    addBefore(id = CanvasMenu.Button.Id.sendBackward) {
                        CanvasMenu.Button.remember(
                            id = EditorComponentId("my.package.canvasMenu.button.beforeSendBackward"),
                            vectorIcon = null,
                            text = { "Before Send Backward" },
                            onClick = {},
                        )
                    }
                    // highlight-modifyListBuilder-addBefore
                    // highlight-modifyListBuilder-replace
                    replace(id = CanvasMenu.Button.Id.duplicate) {
                        // Note that it can be replaced with a component that has a different id.
                        CanvasMenu.Button.rememberDuplicate(
                            vectorIcon = { IconPack.Music },
                        )
                    }
                    // highlight-modifyListBuilder-replace
                    // highlight-modifyListBuilder-remove
                    remove(id = CanvasMenu.Button.Id.delete)
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
