package ly.img.editor.base.dock.options.volume

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.base.dock.BottomSheetContent
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.core.R
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.VolumeHigh
import ly.img.editor.core.sheet.SheetType
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.core.ui.UiDefaults
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Volumelow
import ly.img.editor.core.ui.iconpack.Volumemedium
import ly.img.editor.core.ui.iconpack.Volumeoff
import ly.img.editor.core.ui.sheetScrollableContentModifier
import ly.img.editor.core.iconpack.IconPack as CoreIconPack

@Composable
fun VolumeSheet(
    uiState: VolumeUiState,
    onEvent: (EditorEvent) -> Unit,
) {
    Column {
        SheetHeader(
            title = stringResource(id = R.string.ly_img_editor_volume),
            onClose = { onEvent(EditorEvent.Sheet.Close(animate = true)) },
        )

        Card(
            Modifier.sheetScrollableContentModifier(),
            colors = UiDefaults.cardColors,
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val volume = uiState.volume
                var sliderValue by remember(volume) { mutableStateOf(volume) }

                IconButton(
                    onClick = { onEvent(BlockEvent.OnToggleMute) },
                ) {
                    val icon =
                        when (sliderValue) {
                            0f -> IconPack.Volumeoff
                            in 0f..0.4f -> IconPack.Volumelow
                            in 0.4f..0.7f -> IconPack.Volumemedium
                            in 0.7f..1f -> CoreIconPack.VolumeHigh
                            else -> throw IllegalStateException()
                        }

                    Icon(icon, contentDescription = stringResource(R.string.ly_img_editor_volume))
                }

                Slider(
                    value = sliderValue,
                    onValueChange = {
                        sliderValue = it
                        onEvent(BlockEvent.OnVolumeChange(it))
                    },
                    valueRange = 0f..1f,
                    modifier = Modifier.padding(start = 16.dp),
                    onValueChangeFinished = {
                        if (sliderValue != volume) {
                            onEvent(BlockEvent.OnChangeFinish)
                        }
                    },
                )
            }
        }
    }
}

class VolumeBottomSheetContent(
    override val type: SheetType,
    val uiState: VolumeUiState,
) : BottomSheetContent
