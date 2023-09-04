package ly.img.cesdk.dock.options.fillstroke

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ly.img.cesdk.components.NestedSheetHeader
import ly.img.cesdk.components.color_picker.ColorPicker
import ly.img.cesdk.editorui.BlockEvent
import ly.img.cesdk.editorui.Event

@Composable
fun ColorPickerSheet(
    color: Color,
    title: String,
    onBack: () -> Unit,
    onColorChange: (Color) -> Unit,
    onEvent: (Event) -> Unit
) {
    Column {
        NestedSheetHeader(
            title = title,
            onBack = onBack,
            onClose = { onEvent(Event.OnHideSheet) }
        )
        ColorPicker(
            color = color,
            modifier = Modifier.padding(horizontal = 16.dp),
            onColorChange = onColorChange,
            onColorChangeFinished = {
                onEvent(BlockEvent.OnChangeFinish)
            }
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