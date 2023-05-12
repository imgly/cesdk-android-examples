package ly.img.cesdk.dock.options.fillstroke

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.cesdk.core.UiDefaults
import ly.img.cesdk.core.components.SectionHeader
import ly.img.cesdk.core.components.SheetHeader
import ly.img.cesdk.core.inspectorSheetPadding
import ly.img.cesdk.dock.HalfHeightContainer
import ly.img.cesdk.editorui.BlockEvent
import ly.img.cesdk.editorui.Event
import ly.img.cesdk.editorui.R

@Composable
fun FillStrokeOptionsSheet(
    uiState: FillStrokeUiState,
    onEvent: (Event) -> Unit
) {
    var screenState by remember { mutableStateOf(ScreenState.Main) }

    BackHandler(enabled = screenState != ScreenState.Main) {
        screenState = ScreenState.Main
    }

    when (screenState) {
        ScreenState.Main -> {
            HalfHeightContainer {
                Column {
                    SheetHeader(
                        title = stringResource(id = uiState.titleRes),
                        onClose = { onEvent(Event.HideSheet) }
                    )
                    Column(
                        Modifier
                            .inspectorSheetPadding()
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (uiState.fillUiState != null) {
                            SectionHeader(stringResource(id = R.string.cesdk_fill))
                            Card(
                                colors = UiDefaults.cardColors,
                            ) {
                                val fillUiState = uiState.fillUiState
                                ColorOptions(
                                    enabled = fillUiState.isFillEnabled,
                                    selectedColor = fillUiState.fillColor,
                                    onNoColorSelected = { onEvent(BlockEvent.OnDisableFill) },
                                    onColorSelected = {
                                        onEvent(BlockEvent.OnChangeFillColor(it))
                                        if (it != fillUiState.fillColor || !fillUiState.isFillEnabled) {
                                            onEvent(BlockEvent.OnChangeFinish)
                                        }
                                    },
                                    openColorPicker = {
                                        screenState = ScreenState.FillColorPicker
                                    },
                                    colors = uiState.fillUiState.colorPalette
                                )
                            }
                        }
                        if (uiState.fillUiState != null && uiState.strokeUiState != null) {
                            Spacer(Modifier.height(16.dp))
                        }
                        if (uiState.strokeUiState != null) {
                            StrokeOptions(
                                uiState = uiState.strokeUiState,
                                onEvent = onEvent,
                                openColorPicker = {
                                    screenState = ScreenState.StrokeColorPicker
                                }
                            )
                        }
                    }
                }
            }
        }

        else -> {
            ColorPickerSheet(
                color = if (screenState == ScreenState.StrokeColorPicker)
                    checkNotNull(uiState.strokeUiState).strokeColor else checkNotNull(uiState.fillUiState).fillColor,
                title = stringResource(id = if (screenState == ScreenState.StrokeColorPicker) R.string.cesdk_stroke_color else R.string.cesdk_fill_color),
                onBack = { screenState = ScreenState.Main },
                onColorChange = {
                    if (screenState == ScreenState.StrokeColorPicker) {
                        onEvent(BlockEvent.OnChangeStrokeColor(it))
                    } else if (screenState == ScreenState.FillColorPicker) {
                        onEvent(BlockEvent.OnChangeFillColor(it))
                    }
                },
                onEvent = onEvent
            )
        }
    }
}

private enum class ScreenState {
    Main, StrokeColorPicker, FillColorPicker
}