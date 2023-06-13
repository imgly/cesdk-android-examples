package ly.img.cesdk.dock.options.crop

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
import ly.img.cesdk.core.UiDefaults
import ly.img.cesdk.core.components.CardButton
import ly.img.cesdk.core.components.SectionHeader
import ly.img.cesdk.core.components.SheetHeader
import ly.img.cesdk.core.iconpack.Flip
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.iconpack.Rotate90degreesccwoutline
import ly.img.cesdk.core.iconpack.Undo
import ly.img.cesdk.core.inspectorSheetPadding
import ly.img.cesdk.dock.BottomSheetContent
import ly.img.cesdk.dock.HalfHeightContainer
import ly.img.cesdk.editorui.BlockEvent
import ly.img.cesdk.editorui.Event
import ly.img.cesdk.editorui.R

@Composable
fun CropSheet(
    uiState: CropUiState,
    onEvent: (Event) -> Unit
) {

    HalfHeightContainer {
        Column {
            SheetHeader(
                title = stringResource(id = R.string.cesdk_crop),
                onClose = { onEvent(Event.HideSheet) }
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