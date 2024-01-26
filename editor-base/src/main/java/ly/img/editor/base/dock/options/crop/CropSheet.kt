package ly.img.editor.base.dock.options.crop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.base.R
import ly.img.editor.base.components.CardButton
import ly.img.editor.base.components.SectionHeader
import ly.img.editor.base.dock.BottomSheetContent
import ly.img.editor.base.dock.HalfHeightContainer
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.base.ui.Event
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.core.ui.UiDefaults
import ly.img.editor.core.ui.iconpack.Flip
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Rotate90degreesccwoutline
import ly.img.editor.core.ui.iconpack.Undo
import ly.img.editor.core.ui.inspectorSheetPadding

@Composable
fun CropSheet(
    uiState: CropUiState,
    onEvent: (Event) -> Unit
) {

    HalfHeightContainer {
        Column {
            SheetHeader(
                title = stringResource(id = R.string.cesdk_crop),
                onClose = { onEvent(Event.OnHideSheet) }
            )

            Column(
                Modifier
                    .inspectorSheetPadding()
                    .verticalScroll(rememberScrollState())
            ) {

                SectionHeader(text = stringResource(R.string.cesdk_straighten))

                Card(
                    colors = UiDefaults.cardColors
                ) {
                    ScalePicker(
                        value = uiState.straightenAngle,
                        valueRange = -45f..45f,
                        // Use +-44.999 as bound to guarantee that `decomposedDegrees` is stable and thus
                        // `straightenDegrees` won't jump from -45 to +45 or vice versa for some 90 degree rotations.
                        rangeInclusionType = RangeInclusionType.RangeExclusiveExclusive,
                        onValueChange = {
                            onEvent(BlockEvent.OnCropStraighten(it, uiState.cropScaleRatio))
                        },
                        onValueChangeFinished = {
                            if (it != uiState.straightenAngle) {
                                onEvent(BlockEvent.OnChangeFinish)
                            }
                        },
                        modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CardButton(
                        text = stringResource(R.string.cesdk_reset),
                        icon = IconPack.Undo,
                        modifier = Modifier.weight(1f),
                        enabled = uiState.canResetCrop,
                        onClick = { onEvent(BlockEvent.OnResetCrop) }
                    )
                    CardButton(
                        text = stringResource(R.string.cesdk_rotate),
                        icon = IconPack.Rotate90degreesccwoutline,
                        modifier = Modifier.weight(1f),
                        enabled = true,
                        onClick = { onEvent(BlockEvent.OnCropRotate(uiState.cropScaleRatio)) }
                    )
                    CardButton(
                        text = stringResource(R.string.cesdk_flip),
                        icon = IconPack.Flip,
                        modifier = Modifier.weight(1f),
                        enabled = true,
                        onClick = { onEvent(BlockEvent.OnFlipCropHorizontal) }
                    )
                }
            }
        }
    }
}

class CropBottomSheetContent(val uiState: CropUiState) : BottomSheetContent