package ly.img.editor.base.dock.options.effect

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.base.components.NestedSheetHeader
import ly.img.editor.base.components.PropertySlider
import ly.img.editor.base.components.color_picker.ColorPickerButton
import ly.img.editor.base.engine.AdjustmentState
import ly.img.editor.base.engine.EffectAndBlurOptions
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.compose.material3.Card
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.ui.UiDefaults
import ly.img.editor.core.ui.sheetScrollableContentModifier
import kotlin.math.roundToInt

@Composable
fun EffectOptionsSheet(
    onBack: () -> Unit,
    onEvent: (EditorEvent) -> Unit,
    openColorPicker: (EffectAndBlurOptions) -> Unit,
    title: String,
    adjustments: List<AdjustmentState>,
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
        Modifier.sheetScrollableContentModifier(),
    ) {
        adjustments.forEach { (type, currentValue) ->
            when (type.valueOptions) {
                is EffectAndBlurOptions.ValueOptions.Float -> {
                    PropertySlider(
                        title = type.nameRes,
                        value = (currentValue as AdjustmentState.Value.Float).value,
                        valueRange = type.valueOptions.range,
                        onValueChange = { newValue ->
                            onEvent(BlockEvent.OnChangeEffectSettings(type, AdjustmentState.Value.Float(newValue)))
                        },
                        onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                    )
                }
                is EffectAndBlurOptions.ValueOptions.Int -> {
                    val range =
                        type.valueOptions.range.let {
                            it.first.toFloat()..it.last.toFloat()
                        }
                    PropertySlider(
                        title = type.nameRes,
                        value = (currentValue as AdjustmentState.Value.Int).value.toFloat(),
                        valueRange = range,
                        onValueChange = { newValue ->
                            onEvent(BlockEvent.OnChangeEffectSettings(type, AdjustmentState.Value.Int(newValue.roundToInt())))
                        },
                        onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                    )
                }
                is EffectAndBlurOptions.ValueOptions.Color -> {
                    val currentColor = (currentValue as AdjustmentState.Value.Color).value
                    Card(
                        colors = UiDefaults.cardColorsExperimental,
                        onClick = {
                            openColorPicker(type)
                        },
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(id = type.nameRes),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            ColorPickerButton(
                                color = currentColor,
                                onClick = null,
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
