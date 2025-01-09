package ly.img.editor.base.dock.options.shapeoptions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.base.R
import ly.img.editor.base.components.PropertySlider
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.core.ui.sheetScrollableContentModifier
import ly.img.editor.core.R as CoreR

@Composable
fun ShapeOptionsSheet(
    uiState: ShapeOptionsUiState,
    onEvent: (EditorEvent) -> Unit,
) {
    Column {
        SheetHeader(
            title = stringResource(CoreR.string.ly_img_editor_shape),
            onClose = { onEvent(EditorEvent.Sheet.Close(animate = true)) },
        )

        Column(
            Modifier.sheetScrollableContentModifier(),
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
                    Spacer(modifier = Modifier.height(8.dp))
                    PropertySlider(
                        title = stringResource(R.string.ly_img_editor_round_corners),
                        value = uiState.cornerRadius,
                        onValueChange = { onEvent(BlockEvent.OnChangePolygonCornerRadius(it)) },
                        onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                        valueRange = 0f..1f,
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

                is RectShapeUiState -> {
                    PropertySlider(
                        title = stringResource(R.string.ly_img_editor_round_corners),
                        value = uiState.cornerRadiusTopLeft,
                        onValueChange = {
                            onEvent(
                                BlockEvent.OnChangeRectCornerRadius(
                                    it,
                                    it,
                                    it,
                                    it,
                                ),
                            )
                        },
                        onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                        valueRange = 0f..1f,
                    )
                }
            }
        }
    }
}
