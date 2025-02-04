package ly.img.cesdk.dock.options.fillstroke

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.cesdk.apparel.BlockEvent
import ly.img.cesdk.apparel.Event
import ly.img.cesdk.apparelui.R
import ly.img.cesdk.core.UiDefaults
import ly.img.cesdk.core.components.NestedSheetHeader
import ly.img.cesdk.core.components.SectionHeader
import ly.img.cesdk.core.components.SheetHeader
import ly.img.cesdk.core.components.color_picker.ColorPicker
import ly.img.cesdk.core.inspectorSheetPadding
import ly.img.cesdk.dock.HalfHeightContainer

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
                                    }
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
            Column {
                NestedSheetHeader(
                    title = stringResource(
                        if (screenState == ScreenState.StrokeColorPicker) R.string.cesdk_stroke_color else R.string.cesdk_fill_color
                    ),
                    onBack = { screenState = ScreenState.Main },
                    onClose = { onEvent(Event.HideSheet) }
                )
                ColorPicker(
                    color = if (screenState == ScreenState.StrokeColorPicker)
                        checkNotNull(uiState.strokeUiState).strokeColor else checkNotNull(uiState.fillUiState).fillColor,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onColorChange = {
                        if (screenState == ScreenState.StrokeColorPicker) {
                            onEvent(BlockEvent.OnChangeStrokeColor(it))
                        } else if (screenState == ScreenState.FillColorPicker) {
                            onEvent(BlockEvent.OnChangeFillColor(it))
                        }
                    },
                    onColorChangeFinished = {
                        onEvent(BlockEvent.OnChangeFinish)
                    }
                )

                // Disable allowing undo/redo while color picker is open
                DisposableEffect(Unit) {
                    onEvent(Event.EnableHistory(false))
                    onDispose {
                        onEvent(Event.EnableHistory(true))
                    }
                }
            }
        }
    }
}

private enum class ScreenState {
    Main, StrokeColorPicker, FillColorPicker
}