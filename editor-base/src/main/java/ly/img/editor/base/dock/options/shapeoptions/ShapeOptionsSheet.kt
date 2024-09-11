package ly.img.editor.base.dock.options.shapeoptions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.base.R
import ly.img.editor.base.components.PropertySlider
import ly.img.editor.base.dock.HalfHeightContainer
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.base.ui.Event
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.core.ui.inspectorSheetPadding

@Composable
fun ShapeOptionsSheet(
    uiState: ShapeOptionsUiState,
    onEvent: (Event) -> Unit,
) {
    HalfHeightContainer {
        Column {
            SheetHeader(
                title = stringResource(R.string.ly_img_editor_shape_options),
                onClose = { onEvent(Event.OnHideSheet) },
            )

            Column(
                Modifier
                    .inspectorSheetPadding()
                    .verticalScroll(rememberScrollState()),
            ) {
                when (uiState) {
                    is PolygonShapeOptionsUiState -> {
                        PropertySlider(
                            title = stringResource(R.string.ly_img_editor_sides),
                            value = uiState.sides,
                            onValueChange = { onEvent(BlockEvent.OnChangePolygonSides(it)) },
                            onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                            valueRange = 3f..12f,
                            steps = 8,
                        )
                    }

                    is LineShapeOptionsUiState -> {
                        PropertySlider(
                            title = stringResource(R.string.ly_img_editor_line_width),
                            value = uiState.width,
                            onValueChange = { onEvent(BlockEvent.OnChangeLineWidth(it)) },
                            onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                            valueRange = 0.1f..30f,
                        )
                    }

                    is StarShapeOptionsUiState -> {
                        PropertySlider(
                            title = stringResource(R.string.ly_img_editor_points),
                            value = uiState.points,
                            onValueChange = { onEvent(BlockEvent.OnChangeStarPoints(it)) },
                            onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                            valueRange = 3f..12f,
                            steps = 8,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        PropertySlider(
                            title = stringResource(R.string.ly_img_editor_inner_diameter),
                            value = uiState.innerDiameter,
                            onValueChange = { onEvent(BlockEvent.OnChangeStarInnerDiameter(it)) },
                            onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                            valueRange = 0.1f..1f,
                        )
                    }
                }
            }
        }
    }
}
