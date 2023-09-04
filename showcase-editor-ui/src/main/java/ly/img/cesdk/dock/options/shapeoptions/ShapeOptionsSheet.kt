package ly.img.cesdk.dock.options.shapeoptions

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
fun ShapeOptionsSheet(
    uiState: ShapeOptionsUiState,
    onEvent: (Event) -> Unit
) {
    HalfHeightContainer {
        Column {
            SheetHeader(
                title = stringResource(R.string.cesdk_shape_options),
                onClose = { onEvent(Event.OnHideSheet) }
            )

            Column(
                Modifier
                    .inspectorSheetPadding()
                    .verticalScroll(rememberScrollState())
            ) {
                when (uiState) {
                    is PolygonShapeOptionsUiState -> {
                        PropertySlider(
                            title = stringResource(R.string.cesdk_sides),
                            value = uiState.sides,
                            onValueChange = { onEvent(BlockEvent.OnChangePolygonSides(it)) },
                            onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                            valueRange = 3f..12f,
                            steps = 8
                        )
                    }

                    is LineShapeOptionsUiState -> {
                        PropertySlider(
                            title = stringResource(R.string.cesdk_line_width),
                            value = uiState.width,
                            onValueChange = { onEvent(BlockEvent.OnChangeLineWidth(it)) },
                            onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                            valueRange = 0.1f..30f,
                        )
                    }

                    is StarShapeOptionsUiState -> {
                        PropertySlider(
                            title = stringResource(R.string.cesdk_points),
                            value = uiState.points,
                            onValueChange = { onEvent(BlockEvent.OnChangeStarPoints(it)) },
                            onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                            valueRange = 3f..12f,
                            steps = 8
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        PropertySlider(
                            title = stringResource(R.string.cesdk_inner_diameter),
                            value = uiState.innerDiameter,
                            onValueChange = { onEvent(BlockEvent.OnChangeStarInnerDiameter(it)) },
                            onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                            valueRange = 0.1f..1f
                        )
                    }
                }
            }
        }
    }
}