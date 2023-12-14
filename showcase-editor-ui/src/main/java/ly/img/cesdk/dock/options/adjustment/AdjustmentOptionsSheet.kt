package ly.img.cesdk.dock.options.adjustment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.cesdk.components.PropertySlider
import ly.img.cesdk.core.ui.SheetHeader
import ly.img.cesdk.core.ui.inspectorSheetPadding
import ly.img.cesdk.dock.HalfHeightContainer
import ly.img.cesdk.editorui.BlockEvent
import ly.img.cesdk.editorui.Event

import ly.img.cesdk.editorui.R

@Composable
fun AdjustmentOptionsSheet(
    uiState: AdjustmentUiState,
    onEvent: (Event) -> Unit
) {

    HalfHeightContainer {
        Column {
            SheetHeader(
                title = stringResource(id = R.string.cesdk_adjustment),
                onClose = { onEvent(Event.OnHideSheet) }
            )
            Column(
                Modifier
                    .inspectorSheetPadding()
                    .verticalScroll(rememberScrollState())
            ) {
                uiState.adjustments.forEach { (type, currentValue) ->
                    PropertySlider(
                        title = type.nameRes,
                        value = currentValue,
                        valueRange = -1f..1f,
                        onValueChange = { newValue ->
                            onEvent(BlockEvent.OnChangeEffectSettings(type, newValue))
                        },
                        onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
