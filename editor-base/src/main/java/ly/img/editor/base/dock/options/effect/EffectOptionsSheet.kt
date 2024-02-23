package ly.img.editor.base.dock.options.effect

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ly.img.editor.base.components.NestedSheetHeader
import ly.img.editor.base.components.PropertySlider
import ly.img.editor.base.engine.AdjustmentState
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.base.ui.Event
import ly.img.editor.core.ui.inspectorSheetPadding

@Composable
fun EffectOptionsSheet(
    onBack: () -> Unit,
    onEvent: (Event) -> Unit,
    title: String,
    adjustments: List<AdjustmentState>,
) {
    BackHandler {
        onBack()
    }
    Column {
        NestedSheetHeader(
            title = title,
            onBack = onBack,
            onClose = { onEvent(Event.OnHideSheet) },
        )
        Column(
            Modifier
                .inspectorSheetPadding()
                .verticalScroll(rememberScrollState()),
        ) {
            adjustments.forEach { (type, currentValue) ->
                PropertySlider(
                    title = type.nameRes,
                    value = currentValue,
                    valueRange = type.range,
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
