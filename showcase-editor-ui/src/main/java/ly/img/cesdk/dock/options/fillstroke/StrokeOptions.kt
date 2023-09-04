package ly.img.cesdk.dock.options.fillstroke

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.cesdk.components.PropertyPicker
import ly.img.cesdk.components.SectionHeader
import ly.img.cesdk.core.ui.UiDefaults
import ly.img.cesdk.editorui.BlockEvent
import ly.img.cesdk.editorui.Event
import ly.img.cesdk.editorui.R

@Composable
fun StrokeOptions(
    uiState: StrokeUiState,
    onEvent: (Event) -> Unit,
    openColorPicker: () -> Unit
) {
    SectionHeader(stringResource(R.string.cesdk_stroke))
    Card(
        colors = UiDefaults.cardColors,
    ) {
        ColorOptions(
            enabled = uiState.isStrokeEnabled,
            selectedColor = uiState.strokeColor,
            onNoColorSelected = { onEvent(BlockEvent.OnDisableStroke) },
            onColorSelected = {
                onEvent(BlockEvent.OnChangeStrokeColor(it))
                if (it != uiState.strokeColor || !uiState.isStrokeEnabled) {
                    onEvent(BlockEvent.OnChangeFinish)
                }
            },
            openColorPicker = openColorPicker,
            colors = uiState.colorPalette,
            punchHole = true
        )

        if (uiState.isStrokeEnabled) {
            Divider(Modifier.padding(horizontal = 16.dp))

            var sliderValue by remember(uiState) { mutableStateOf(uiState.strokeWidth) }
            Slider(
                value = sliderValue,
                onValueChange = {
                    sliderValue = it
                    onEvent(BlockEvent.OnChangeStrokeWidth(it))
                },
                valueRange = STROKE_WIDTH_LOWER_BOUND..STROKE_WIDTH_UPPER_BOUND,
                onValueChangeFinished = {
                    if (uiState.strokeWidth != sliderValue) {
                        onEvent(BlockEvent.OnChangeFinish)
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Divider(Modifier.padding(horizontal = 16.dp))
            PropertyPicker(
                title = stringResource(R.string.cesdk_style),
                propertyTextRes = uiState.strokeStyleRes,
                properties = strokeStylePropertiesList,
                onPropertyPicked = { onEvent(BlockEvent.OnChangeStrokeStyle(it)) }
            )
            Divider(Modifier.padding(horizontal = 16.dp))
            PropertyPicker(
                title = stringResource(R.string.cesdk_position),
                propertyTextRes = uiState.strokePositionRes,
                enabled = uiState.isStrokePositionEnabled,
                properties = strokePositionPropertiesList,
                onPropertyPicked = { onEvent(BlockEvent.OnChangeStrokePosition(it)) }
            )
            Divider(Modifier.padding(horizontal = 16.dp))
            PropertyPicker(
                title = stringResource(R.string.cesdk_join),
                propertyTextRes = uiState.strokeJoinRes,
                properties = strokeJoinPropertiesList,
                enabled = uiState.isStrokeJointEnabled,
                onPropertyPicked = { onEvent(BlockEvent.OnChangeStrokeJoin(it)) }
            )
        }
    }
}