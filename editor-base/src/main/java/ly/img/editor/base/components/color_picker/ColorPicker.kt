package ly.img.editor.base.components.color_picker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.base.R
import ly.img.editor.base.components.SectionHeader
import ly.img.editor.core.ui.ColorButton

@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    color: Color,
    showOpacity: Boolean,
    onColorChange: (Color) -> Unit,
    onColorChangeFinished: () -> Unit,
) {
    var hsvColor by remember {
        mutableStateOf(HsvColor.from(color))
    }
    val onFinish = {
        if (hsvColor.toComposeColor() != color) {
            onColorChangeFinished()
        }
    }
    Column(modifier = modifier) {
        Spacer(Modifier.height(16.dp))
        SaturationValueArea(
            currentColor = hsvColor,
            onSaturationValueChanged = { saturation, value ->
                hsvColor = hsvColor.copy(saturation = saturation, value = value)
                onColorChange(hsvColor.toComposeColor())
            },
            onSaturationValueChangeFinished = onFinish,
        )

        Spacer(Modifier.height(16.dp))
        SectionHeader(text = stringResource(R.string.ly_img_editor_hue))
        Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            HueSlider(
                modifier = Modifier.weight(1f),
                value = hsvColor.hue,
                onValueChange = {
                    hsvColor = hsvColor.copy(hue = it)
                    onColorChange(hsvColor.toComposeColor())
                },
                onValueChangeFinished = onFinish,
            )
            Spacer(Modifier.width(16.dp))
            Text(
                hsvColor.hue.toInt().toString(),
                modifier = Modifier.widthIn(32.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Spacer(Modifier.height(16.dp))

        if (showOpacity) {
            SectionHeader(text = stringResource(R.string.ly_img_editor_opacity))
            Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                OpacitySlider(
                    modifier = Modifier.weight(1f),
                    color = hsvColor.toComposeColor(),
                    onValueChange = {
                        hsvColor = hsvColor.copy(alpha = it)
                        onColorChange(hsvColor.toComposeColor())
                    },
                    onValueChangeFinished = onFinish,
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    "${(hsvColor.alpha * 100).toInt()}%",
                    modifier = Modifier.widthIn(32.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Spacer(Modifier.height(16.dp))
        }
        Divider(Modifier.padding(horizontal = 16.dp))

        val onPresetColorClick: (Color) -> Unit = {
            hsvColor = HsvColor.from(it)
            onColorChange(it)
            onFinish()
        }

        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val composeColor by remember(hsvColor) {
                mutableStateOf(hsvColor.toComposeColor())
            }
            ColorPreview(
                color = composeColor,
                modifier = Modifier.padding(vertical = 4.dp),
            )
            Spacer(modifier = Modifier.width(24.dp))
            LazyVerticalGrid(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                columns = GridCells.Fixed(5),
            ) {
                items(fillAndStrokeColors + secondaryColors) {
                    ColorButton(color = it, selected = composeColor == it, onClick = {
                        onPresetColorClick(it)
                    })
                }
            }
        }
    }
}
