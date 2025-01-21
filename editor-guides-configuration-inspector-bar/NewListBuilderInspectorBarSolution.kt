import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import ly.img.editor.DesignEditor
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.core.LocalEditorScope
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.rememberAdjustments
import ly.img.editor.core.component.rememberBlur
import ly.img.editor.core.component.rememberCrop
import ly.img.editor.core.component.rememberDelete
import ly.img.editor.core.component.rememberDuplicate
import ly.img.editor.core.component.rememberEditText
import ly.img.editor.core.component.rememberEffect
import ly.img.editor.core.component.rememberFillStroke
import ly.img.editor.core.component.rememberFormatText
import ly.img.editor.core.component.rememberLayer
import ly.img.editor.core.component.rememberMoveAsClip
import ly.img.editor.core.component.rememberMoveAsOverlay
import ly.img.editor.core.component.rememberReplace
import ly.img.editor.core.component.rememberShape
import ly.img.editor.core.component.rememberSplit
import ly.img.editor.core.component.rememberVolume
import ly.img.editor.rememberForDesign

// Add this composable to your NavHost
@Composable
fun NewListBuilderInspectorBarSolution(navController: NavHostController) {
    val engineConfiguration =
        EngineConfiguration.rememberForDesign(
            license = "<your license here>",
        )

    val editorConfiguration =
        EditorConfiguration.rememberForDesign(
            inspectorBar = {
                InspectorBar.remember(
                    // highlight-newListBuilder
                    listBuilder =
                        InspectorBar.ListBuilder.remember {
                            add {
                                val buttonScope =
                                    LocalEditorScope.current.run {
                                        remember(this) { InspectorBar.ButtonScope(parentScope = this) }
                                    }
                                InspectorBar.Button.remember(
                                    id = EditorComponentId("my.package.inspectorBar.button.custom"),
                                    vectorIcon = null,
                                    text = { "Custom Button" },
                                    onClick = {},
                                    scope = buttonScope,
                                )
                            }
                            add { InspectorBar.Button.rememberDuplicate() }
                            add { InspectorBar.Button.rememberDelete() }
                            add { InspectorBar.Button.rememberAdjustments() }
                            add { InspectorBar.Button.rememberEffect() }
                            add { InspectorBar.Button.rememberBlur() }
                            add { InspectorBar.Button.rememberReplace() }
                            add { InspectorBar.Button.rememberEditText() }
                            add { InspectorBar.Button.rememberFormatText() }
                            add { InspectorBar.Button.rememberFillStroke() }
                            add { InspectorBar.Button.rememberVolume() }
                            add { InspectorBar.Button.rememberCrop() }
                            add { InspectorBar.Button.rememberShape() }
                            add { InspectorBar.Button.rememberLayer() }
                            add { InspectorBar.Button.rememberSplit() }
                            add { InspectorBar.Button.rememberMoveAsClip() }
                            add { InspectorBar.Button.rememberMoveAsOverlay() }
                        },
                    // highlight-newListBuilder
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
