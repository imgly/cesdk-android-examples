package ly.img.editor.base.dock.options.adjustment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.base.R
import ly.img.editor.base.components.PropertySlider
import ly.img.editor.base.dock.HalfHeightContainer
import ly.img.editor.base.engine.AdjustmentState
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.base.ui.Event
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.core.ui.halfSheetScrollableContentModifier

@Composable
fun AdjustmentOptionsSheet(
    uiState: AdjustmentUiState,
    onEvent: (Event) -> Unit,
) {
    HalfHeightContainer {
        Column {
            SheetHeader(
                title = stringResource(id = R.string.ly_img_editor_adjustment),
                onClose = { onEvent(Event.OnHideSheet) },
            )
            Column(
                Modifier
                    .halfSheetScrollableContentModifier(rememberScrollState()),
            ) {
                uiState.adjustments.forEach { (type, currentValue) ->
                    currentValue as AdjustmentState.Value.Float
                    PropertySlider(
                        title = type.nameRes,
                        value = currentValue.value,
                        valueRange = -1F..1F,
                        onValueChange = { newValue ->
                            onEvent(BlockEvent.OnChangeEffectSettings(type, AdjustmentState.Value.Float(newValue)))
                        },
                        onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
