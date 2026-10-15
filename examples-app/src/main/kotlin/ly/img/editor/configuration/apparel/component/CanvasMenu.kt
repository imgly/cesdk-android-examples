@file:Suppress("UnusedReceiverParameter")

package ly.img.editor.configuration.apparel.component

import androidx.compose.runtime.Composable
import ly.img.editor.configuration.apparel.ApparelConfigurationBuilder
import ly.img.editor.core.component.CanvasMenu
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberBringForward
import ly.img.editor.core.component.rememberDelete
import ly.img.editor.core.component.rememberDuplicate
import ly.img.editor.core.component.rememberSelectGroup
import ly.img.editor.core.component.rememberSendBackward

/**
 * The configuration of the component that is displayed as horizontal list of items next to
 * the selected design block.
 */
@Composable
fun ApparelConfigurationBuilder.rememberCanvasMenu() = CanvasMenu.remember {
    listBuilder = {
        CanvasMenu.ListBuilder.remember {
            add { CanvasMenu.Button.rememberSelectGroup() }
            if (editorContext.isSelectionInGroup) {
                add {
                    CanvasMenu.Divider.remember {
                        id = { EditorComponentId("ly.img.component.canvasMenu.divider1") }
                    }
                }
            }
            add { CanvasMenu.Button.rememberBringForward() }
            add { CanvasMenu.Button.rememberSendBackward() }
            if (editorContext.canSelectionMove) {
                add {
                    CanvasMenu.Divider.remember {
                        id = { EditorComponentId("ly.img.component.canvasMenu.divider2") }
                    }
                }
            }
            add { CanvasMenu.Button.rememberDuplicate() }
            add { CanvasMenu.Button.rememberDelete() }
        }
    }
}
