import androidx.compose.runtime.Composable
import ly.img.editor.core.component.CanvasMenu
import ly.img.editor.core.component.rememberBringForward
import ly.img.editor.core.component.rememberDelete
import ly.img.editor.core.component.rememberDuplicate
import ly.img.editor.core.component.rememberSelectGroup
import ly.img.editor.core.component.rememberSendBackward

// highlight-listBuilders
@Composable
fun CanvasMenu.ListBuilder.remember() = CanvasMenu.ListBuilder.remember {
    add { CanvasMenu.Button.rememberSelectGroup() }
    add { CanvasMenu.Divider.remember(visible = { editorContext.isSelectionInGroup }) }
    add { CanvasMenu.Button.rememberBringForward() }
    add { CanvasMenu.Button.rememberSendBackward() }
    add { CanvasMenu.Divider.remember(visible = { editorContext.canSelectionMove }) }
    add { CanvasMenu.Button.rememberDuplicate() }
    add { CanvasMenu.Button.rememberDelete() }
}
// highlight-listBuilders
