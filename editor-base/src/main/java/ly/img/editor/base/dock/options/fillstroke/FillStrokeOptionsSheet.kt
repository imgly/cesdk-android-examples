package ly.img.editor.base.dock.options.fillstroke

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.base.R
import ly.img.editor.base.components.PropertyPicker
import ly.img.editor.base.components.SectionHeader
import ly.img.editor.base.dock.HalfHeightContainer
import ly.img.editor.base.dock.options.crop.RangeInclusionType
import ly.img.editor.base.dock.options.crop.ScalePicker
import ly.img.editor.base.engine.GradientFill
import ly.img.editor.base.engine.LinearGradientFill
import ly.img.editor.base.engine.SolidFill
import ly.img.editor.base.engine.toComposeColor
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.base.ui.Event
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.core.ui.UiDefaults
import ly.img.editor.core.ui.inspectorSheetPadding
import ly.img.engine.RGBAColor

@Composable
fun FillStrokeOptionsSheet(
    uiState: FillStrokeUiState,
    onEvent: (Event) -> Unit,
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
                        onClose = { onEvent(Event.OnHideSheet) },
                    )
                    Column(
                        Modifier
                            .inspectorSheetPadding()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        if (uiState.fillUiState != null) {
                            SectionHeader(stringResource(R.string.ly_img_editor_fill))
                            Card(
                                colors = UiDefaults.cardColors,
                            ) {
                                val fillState = uiState.fillUiState.fillState.takeIf { uiState.fillUiState.isFillEnabled }
                                if (uiState.fillUiState.supportFillTypes) {
                                    PropertyPicker(
                                        title = stringResource(R.string.ly_img_editor_type),
                                        propertyTextRes = uiState.fillUiState.fillTypeRes,
                                        properties = fillTypePropertiesList,
                                        onPropertyPicked = {
                                            if (it == "NONE") {
                                                onEvent(BlockEvent.OnDisableFill)
                                            } else if (uiState.fillUiState.isFillEnabled) {
                                                onEvent(BlockEvent.OnChangeFillStyle(it))
                                            } else {
                                                onEvent(BlockEvent.OnEnableFill)
                                                onEvent(BlockEvent.OnChangeFillStyle(it))
                                            }
                                        },
                                    )
                                    Divider(Modifier.padding(horizontal = 16.dp))
                                }
                                when (fillState) {
                                    null -> {
                                        ColorOptions(
                                            enabled = uiState.fillUiState.isFillEnabled,
                                            selectedColor = Color.Black,
                                            onNoColorSelected = {
                                                onEvent(
                                                    BlockEvent.OnDisableFill,
                                                )
                                            },
                                            onColorSelected = {
                                                onEvent(BlockEvent.OnChangeFillColor(it))
                                                onEvent(BlockEvent.OnChangeFinish)
                                            },
                                            openColorPicker = {
                                                screenState = ScreenState.FillColorPicker
                                            },
                                            colors = uiState.fillUiState.colorPalette,
                                        )
                                    }

                                    is SolidFill -> {
                                        ColorOptions(
                                            enabled = uiState.fillUiState.isFillEnabled,
                                            selectedColor = fillState.fillColor,
                                            onNoColorSelected = {
                                                onEvent(
                                                    BlockEvent.OnDisableFill,
                                                )
                                            },
                                            onColorSelected = {
                                                onEvent(BlockEvent.OnChangeFillColor(it))
                                                if (it != fillState.fillColor) {
                                                    onEvent(BlockEvent.OnChangeFinish)
                                                }
                                            },
                                            openColorPicker = {
                                                screenState = ScreenState.FillColorPicker
                                            },
                                            colors = uiState.fillUiState.colorPalette,
                                        )
                                    }

                                    is LinearGradientFill -> {
                                        ColorOptions(
                                            enabled = true,
                                            selectedColor = (fillState.colorStops[0].color as RGBAColor).toComposeColor(),
                                            onNoColorSelected = {
                                                onEvent(BlockEvent.OnDisableFill)
                                            },
                                            allowDisableColor = false,
                                            onColorSelected = {
                                                onEvent(
                                                    BlockEvent.OnChangeGradientFillColors(0, it),
                                                )
                                                if (it != fillState.fillColor) {
                                                    onEvent(BlockEvent.OnChangeFinish)
                                                }
                                            },
                                            openColorPicker = {
                                                screenState = ScreenState.FirstGradientColorPicker
                                            },
                                            colors = uiState.fillUiState.colorPalette,
                                        )
                                        Divider(Modifier.padding(horizontal = 16.dp))
                                        ScalePicker(
                                            value = fillState.gradientRotation,
                                            valueRange = FILL_ROTATION_LOWER_BOUND..FILL_ROTATION_UPPER_BOUND,
                                            rangeInclusionType = RangeInclusionType.RangeInclusiveExclusive,
                                            onValueChange = {
                                                val newFill =
                                                    LinearGradientFill.calculateLinearGradientFromRotation(
                                                        it,
                                                        fillState.colorStops,
                                                    )
                                                onEvent(
                                                    BlockEvent.OnChangeLinearGradientParams(
                                                        newFill.startPointX,
                                                        newFill.startPointY,
                                                        newFill.endPointX,
                                                        newFill.endPointY,
                                                    ),
                                                )
                                            },
                                            onValueChangeFinished = {
                                                if (fillState.gradientRotation != it) {
                                                    onEvent(BlockEvent.OnChangeFinish)
                                                }
                                            },
                                            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
                                        )
                                        Divider(Modifier.padding(horizontal = 16.dp))
                                        ColorOptions(
                                            enabled = true,
                                            selectedColor = (fillState.colorStops[1].color as RGBAColor).toComposeColor(),
                                            onNoColorSelected = {
                                                onEvent(
                                                    BlockEvent.OnDisableFill,
                                                )
                                            },
                                            allowDisableColor = false,
                                            onColorSelected = {
                                                onEvent(
                                                    BlockEvent.OnChangeGradientFillColors(1, it),
                                                )
                                                if (it != fillState.fillColor) {
                                                    onEvent(BlockEvent.OnChangeFinish)
                                                }
                                            },
                                            openColorPicker = {
                                                screenState = ScreenState.SecondGradientColorPicker
                                            },
                                            colors = uiState.fillUiState.colorPalette,
                                        )
                                    }

                                    else -> throw UnsupportedOperationException(
                                        "Fill type not supported yet",
                                    )
                                }
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
                                },
                            )
                        }
                    }
                }
            }
        }

        else -> {
            ColorPickerSheet(
                color =
                    when (screenState) {
                        ScreenState.StrokeColorPicker -> {
                            checkNotNull(uiState.strokeUiState).strokeColor
                        }

                        ScreenState.FirstGradientColorPicker -> {
                            checkNotNull(
                                (uiState.fillUiState?.fillState as? GradientFill)?.colorStops?.getOrNull(
                                    0,
                                )?.color as? RGBAColor,
                            ).toComposeColor()
                        }

                        ScreenState.SecondGradientColorPicker -> {
                            checkNotNull(
                                (uiState.fillUiState?.fillState as? GradientFill)?.colorStops?.getOrNull(
                                    1,
                                )?.color as? RGBAColor,
                            ).toComposeColor()
                        }

                        else -> {
                            checkNotNull(uiState.fillUiState?.fillState?.fillColor)
                        }
                    },
                title =
                    stringResource(
                        id = if (screenState == ScreenState.StrokeColorPicker) R.string.ly_img_editor_stroke_color else R.string.ly_img_editor_fill_color,
                    ),
                onBack = { screenState = ScreenState.Main },
                onColorChange = {
                    when (screenState) {
                        ScreenState.StrokeColorPicker -> {
                            onEvent(BlockEvent.OnChangeStrokeColor(it))
                        }

                        ScreenState.FillColorPicker -> {
                            onEvent(BlockEvent.OnChangeFillColor(it))
                        }

                        ScreenState.FirstGradientColorPicker -> {
                            onEvent(BlockEvent.OnChangeGradientFillColors(0, it))
                        }

                        ScreenState.SecondGradientColorPicker -> {
                            onEvent(BlockEvent.OnChangeGradientFillColors(1, it))
                        }

                        else -> {}
                    }
                },
                onEvent = onEvent,
            )
        }
    }
}

private const val FILL_ROTATION_LOWER_BOUND = 0f
private const val FILL_ROTATION_UPPER_BOUND = 360f

private enum class ScreenState {
    Main,
    StrokeColorPicker,
    FillColorPicker,
    FirstGradientColorPicker,
    SecondGradientColorPicker,
}
