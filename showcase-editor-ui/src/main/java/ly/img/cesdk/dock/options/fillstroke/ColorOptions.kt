package ly.img.cesdk.dock.options.fillstroke

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ly.img.cesdk.components.ColorButton
import ly.img.cesdk.components.color_picker.ColorPickerButton
import ly.img.cesdk.components.color_picker.fillAndStrokeColors

@Composable
fun ColorOptions(
    enabled: Boolean,
    selectedColor: Color,
    onNoColorSelected: () -> Unit,
    onColorSelected: (Color) -> Unit,
    openColorPicker: () -> Unit,
    allowDisableColor: Boolean = true,
    punchHole: Boolean = false,
    colors: List<Color> = fillAndStrokeColors
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (allowDisableColor) {
            ColorButton(color = null, selected = !enabled, onClick = onNoColorSelected, punchHole = punchHole)
        }
        colors.forEach { color ->
            ColorButton(color = color, selected = color == selectedColor && enabled, punchHole = punchHole, onClick = {
                onColorSelected(color)
            })
        }
        ColorPickerButton(color = selectedColor, onClick = openColorPicker, punchHole = punchHole)
    }
}