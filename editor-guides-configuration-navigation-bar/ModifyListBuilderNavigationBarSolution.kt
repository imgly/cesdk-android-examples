import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.component.EditorComponent.ListBuilder.Companion.modify
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.export
import ly.img.editor.core.component.redo
import ly.img.editor.core.component.rememberForDesign
import ly.img.editor.core.component.togglePagesMode
import ly.img.editor.core.component.undo
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Music
import ly.img.editor.rememberForDesign

// Add this composable to your NavHost
@Composable
fun ModifyListBuilderNavigationBarSolution(navController: NavHostController) {
    val engineConfiguration = EngineConfiguration.rememberForDesign(
        license = "<your license here>",
    )

    val editorConfiguration = EditorConfiguration.rememberForDesign(
        navigationBar = {
            NavigationBar.remember(
                // highlight-modifyListBuilder
                listBuilder = NavigationBar.ListBuilder.rememberForDesign().modify {
                    // highlight-modifyListBuilder-addFirst
                    addFirst(alignment = Alignment.End) {
                        NavigationBar.Button.remember(
                            id = EditorComponentId("my.package.navigationBar.button.endAligned.first"),
                            vectorIcon = { IconPack.Music },
                            text = { "First Button" },
                            onClick = {},
                        )
                    }
                    // highlight-modifyListBuilder-addFirst
                    // highlight-modifyListBuilder-addLast
                    addLast(alignment = Alignment.End) {
                        NavigationBar.Button.remember(
                            id = EditorComponentId("my.package.navigationBar.button.endAligned.last"),
                            vectorIcon = { IconPack.Music },
                            text = { "Last Button" },
                            onClick = {},
                        )
                    }
                    // highlight-modifyListBuilder-addLast
                    // highlight-modifyListBuilder-addAfter
                    addAfter(id = NavigationBar.Button.Id.redo) {
                        NavigationBar.Button.remember(
                            id = EditorComponentId("my.package.navigationBar.button.afterRedo"),
                            vectorIcon = { IconPack.Music },
                            text = { "After Redo" },
                            onClick = {},
                        )
                    }
                    // highlight-modifyListBuilder-addAfter
                    // highlight-modifyListBuilder-addBefore
                    addBefore(id = NavigationBar.Button.Id.undo) {
                        NavigationBar.Button.remember(
                            id = EditorComponentId("my.package.navigationBar.button.beforeUndo"),
                            vectorIcon = { IconPack.Music },
                            text = { "Before Undo" },
                            onClick = {},
                        )
                    }
                    // highlight-modifyListBuilder-addBefore
                    // highlight-modifyListBuilder-replace
                    replace(id = NavigationBar.Button.Id.export) {
                        NavigationBar.Button.remember(
                            id = EditorComponentId("my.package.navigationBar.button.replacedExport"),
                            vectorIcon = null,
                            text = { "Replaced Export" },
                            onClick = {},
                        )
                    }
                    // highlight-modifyListBuilder-replace
                    // highlight-modifyListBuilder-remove
                    remove(id = NavigationBar.Button.Id.togglePagesMode)
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
