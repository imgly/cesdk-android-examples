package ly.img.editor.base.dock.options.fillstroke

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ly.img.editor.base.components.color_picker.ColorPickerButton
import ly.img.editor.base.components.color_picker.fillAndStrokeColors
import ly.img.editor.core.theme.EditorTheme
import ly.img.editor.core.ui.ColorButton
import ly.img.editor.core.ui.utils.ThemePreview

@Composable
fun ColorOptions(
    enabled: Boolean,
    selectedColor: Color,
    onNoColorSelected: () -> Unit,
    onColorSelected: (Color) -> Unit,
    openColorPicker: () -> Unit,
    allowDisableColor: Boolean = true,
    punchHole: Boolean = false,
    colors: List<Color> = fillAndStrokeColors,
) {
    Row(
        modifier =
            Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (allowDisableColor) {
            ColorButton(
                color = null,
                modifier = Modifier.weight(1f),
                selected = !enabled,
                onClick = onNoColorSelected,
                punchHole = punchHole,
            )
        }
        colors.forEach { color ->
            ColorButton(
                color = color,
                modifier = Modifier.weight(1f),
                selected = color == selectedColor && enabled,
                punchHole = punchHole,
                onClick = {
                    onColorSelected(color)
                },
            )
        }
        ColorPickerButton(
            color = selectedColor,
            modifier = Modifier.weight(1f),
            onClick = openColorPicker,
            punchHole = punchHole,
        )
    }
}

@ThemePreview
@Composable
private fun ColorOptionsPreview() {
    EditorTheme {
        ColorOptions(
            enabled = true,
            selectedColor = fillAndStrokeColors.random(),
            onNoColorSelected = { },
            onColorSelected = {},
            openColorPicker = { },
            colors = fillAndStrokeColors,
        )
    }
}

@ThemePreview
@Composable
private fun ColorOptionsWithoutDisableColorPreview() {
    EditorTheme {
        ColorOptions(
            enabled = true,
            selectedColor = fillAndStrokeColors.random(),
            onNoColorSelected = { },
            onColorSelected = {},
            openColorPicker = { },
            allowDisableColor = false,
            colors = fillAndStrokeColors,
        )
    }
}

@ThemePreview
@Composable
private fun ColorOptionsWithPunchHolePreview() {
    EditorTheme {
        ColorOptions(
            enabled = true,
            selectedColor = fillAndStrokeColors.random(),
            onNoColorSelected = { },
            onColorSelected = {},
            openColorPicker = { },
            punchHole = true,
            colors = fillAndStrokeColors,
        )
    }
}
