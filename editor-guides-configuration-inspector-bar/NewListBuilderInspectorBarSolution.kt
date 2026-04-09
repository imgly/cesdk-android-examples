import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ly.img.editor.Editor
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.remember
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
import ly.img.editor.core.component.rememberTextBackground
import ly.img.editor.core.component.rememberVolume
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember

// Add this composable to your NavHost
@Composable
fun NewListBuilderInspectorBarSolution(navController: NavHostController) {
    Editor(
        license = null, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                inspectorBar = {
                    InspectorBar.remember {
                        listBuilder = {
                            // highlight-newListBuilder
                            InspectorBar.ListBuilder.remember {
                                add {
                                    InspectorBar.Button.remember {
                                        id = { EditorComponentId("my.package.inspectorBar.button.custom") }
                                        onClick = {}
                                        vectorIcon = null
                                        textString = { "Custom Button" }
                                    }
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
                                add { InspectorBar.Button.rememberTextBackground() }
                                add { InspectorBar.Button.rememberVolume() }
                                add { InspectorBar.Button.rememberCrop() }
                                add { InspectorBar.Button.rememberShape() }
                                add { InspectorBar.Button.rememberLayer() }
                                add { InspectorBar.Button.rememberSplit() }
                                add { InspectorBar.Button.rememberMoveAsClip() }
                                add { InspectorBar.Button.rememberMoveAsOverlay() }
                            }
                            // highlight-newListBuilder
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
