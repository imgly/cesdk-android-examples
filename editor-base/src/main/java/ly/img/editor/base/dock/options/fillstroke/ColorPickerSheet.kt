package ly.img.editor.base.dock.options.fillstroke

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ly.img.editor.base.components.NestedSheetHeader
import ly.img.editor.base.components.color_picker.ColorPicker
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.base.ui.Event
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.ui.sheetCardContentModifier

@Composable
fun ColorPickerSheet(
    color: Color,
    title: String,
    showOpacity: Boolean = true,
    onBack: () -> Unit,
    onColorChange: (Color) -> Unit,
    onEvent: (EditorEvent) -> Unit,
) {
    BackHandler {
        onBack()
    }
    NestedSheetHeader(
        title = title,
        onBack = onBack,
        onClose = { onEvent(EditorEvent.Sheet.Close(animate = true)) },
    )
    Column(
        modifier = Modifier.sheetCardContentModifier(top = 0.dp),
    ) {
        ColorPicker(
            color = color,
            showOpacity = showOpacity,
            onColorChange = onColorChange,
            onColorChangeFinished = {
                onEvent(BlockEvent.OnChangeFinish)
            },
        )

        // Disable allowing undo/redo while color picker is open
        DisposableEffect(Unit) {
            onEvent(Event.EnableHistory(false))
            onDispose {
                onEvent(Event.EnableHistory(true))
            }
        }
    }
}
