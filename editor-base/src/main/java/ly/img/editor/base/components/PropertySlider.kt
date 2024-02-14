package ly.img.editor.base.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.core.ui.UiDefaults

@Composable
fun PropertySlider(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
) {
    Column {
        SectionHeader(text = title)
        var sliderValue by remember(value) { mutableStateOf(value) }
        Card(
            colors = UiDefaults.cardColors,
        ) {
            Slider(
                value = sliderValue,
                onValueChange = {
                    sliderValue = it
                    onValueChange(it)
                },
                valueRange = valueRange,
                steps = steps,
                modifier = Modifier.padding(horizontal = 16.dp),
                onValueChangeFinished = {
                    if (sliderValue != value) {
                        onValueChangeFinished()
                    }
                },
            )
        }
    }
}

@Composable
fun PropertySlider(
    @StringRes title: Int,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
) = PropertySlider(
    title = stringResource(title),
    value = value,
    onValueChange = onValueChange,
    onValueChangeFinished = onValueChangeFinished,
    valueRange = valueRange,
    steps = steps,
)
