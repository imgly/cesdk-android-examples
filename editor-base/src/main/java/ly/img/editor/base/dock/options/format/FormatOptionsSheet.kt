package ly.img.editor.base.dock.options.format

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.base.R
import ly.img.editor.base.components.NestedSheetHeader
import ly.img.editor.base.components.PropertyLink
import ly.img.editor.base.components.PropertyPicker
import ly.img.editor.base.components.PropertySlider
import ly.img.editor.base.components.ToggleIconButton
import ly.img.editor.base.dock.HalfHeightContainer
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.base.ui.Event
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.core.ui.UiDefaults
import ly.img.editor.core.ui.iconpack.Formatbold
import ly.img.editor.core.ui.iconpack.Formatitalic
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.inspectorSheetPadding

@Composable
fun FormatOptionsSheet(
    uiState: FormatUiState,
    onEvent: (Event) -> Unit
) {
    HalfHeightContainer {
        var screenState by remember { mutableStateOf(ScreenState.Main) }

        BackHandler(enabled = screenState != ScreenState.Main) {
            screenState = ScreenState.Main
        }

        when (screenState) {
            ScreenState.Main -> {
                Column {
                    SheetHeader(
                        title = stringResource(id = R.string.cesdk_format),
                        onClose = { onEvent(Event.OnHideSheet) }
                    )

                    Column(
                        modifier = Modifier
                            .inspectorSheetPadding()
                            .verticalScroll(
                                rememberScrollState()
                            )
                    ) {
                        Card(
                            colors = UiDefaults.cardColors,
                        ) {
                            PropertyLink(title = stringResource(id = R.string.cesdk_font), value = uiState.fontFamily) {
                                screenState = ScreenState.SelectFont
                            }
                            Divider(Modifier.padding(horizontal = 16.dp))
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row {
                                    ToggleIconButton(
                                        checked = uiState.isBold ?: false,
                                        onCheckedChange = { onEvent(BlockEvent.OnBold(uiState.fontFamily, it)) },
                                        enabled = uiState.isBold != null
                                    ) {
                                        Icon(
                                            IconPack.Formatbold,
                                            contentDescription = stringResource(R.string.cesdk_bold)
                                        )
                                    }
                                    ToggleIconButton(
                                        checked = uiState.isItalic ?: false,
                                        onCheckedChange = { onEvent(BlockEvent.OnItalicize(uiState.fontFamily, it)) },
                                        enabled = uiState.isItalic != null
                                    ) {
                                        Icon(
                                            IconPack.Formatitalic,
                                            contentDescription = stringResource(R.string.cesdk_italic)
                                        )
                                    }
                                }

                                Row {
                                    HorizontalAlignment.values().forEach {
                                        AlignmentButton(
                                            alignment = it,
                                            currentAlignment = uiState.horizontalAlignment,
                                            changeAlignment = { onEvent(BlockEvent.OnChangeHorizontalAlignment(it)) }
                                        )
                                    }
                                }
                            }
                            Divider(Modifier.padding(horizontal = 16.dp))
                            PropertyLink(title = stringResource(id = R.string.cesdk_advanced_text)) {
                                screenState = ScreenState.AdvancedOptions
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        PropertySlider(
                            title = stringResource(R.string.cesdk_font_size),
                            value = uiState.fontSize,
                            valueRange = 6f..90f,
                            onValueChange = { onEvent(BlockEvent.OnChangeFontSize(it)) },
                            onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) }
                        )
                    }
                }
            }

            ScreenState.SelectFont -> {
                Column {
                    NestedSheetHeader(
                        title = stringResource(R.string.cesdk_font),
                        onBack = { screenState = ScreenState.Main },
                        onClose = { onEvent(Event.OnHideSheet) }
                    )
                    FontListUi(
                        fontFamily = uiState.fontFamily,
                        fontFamilies = uiState.fontFamilies,
                        onSelectFont = { onEvent(BlockEvent.OnChangeFont(it)) }
                    )
                }
            }

            ScreenState.AdvancedOptions -> {
                Column {
                    NestedSheetHeader(
                        title = stringResource(R.string.cesdk_advanced_text),
                        onBack = { screenState = ScreenState.Main },
                        onClose = { onEvent(Event.OnHideSheet) }
                    )
                    Column(
                        Modifier
                            .inspectorSheetPadding()
                            .verticalScroll(rememberScrollState())
                    ) {
                        PropertySlider(
                            title = stringResource(R.string.cesdk_letter_spacing),
                            value = uiState.letterSpacing,
                            onValueChange = { onEvent(BlockEvent.OnChangeLetterSpacing(it)) },
                            onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                            valueRange = -0.15f..1.4f,
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PropertySlider(
                            title = stringResource(R.string.cesdk_line_height),
                            value = uiState.lineHeight,
                            onValueChange = { onEvent(BlockEvent.OnChangeLineHeight(it)) },
                            onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                            valueRange = 0.5f..2.5f,
                        )

                        if (uiState.isArrangeResizeAllowed) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                colors = UiDefaults.cardColors
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        stringResource(R.string.cesdk_vertical_alignment),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Row {
                                        VerticalAlignment.values().forEach {
                                            AlignmentButton(
                                                alignment = it,
                                                currentAlignment = uiState.verticalAlignment,
                                                changeAlignment = { onEvent(BlockEvent.OnChangeVerticalAlignment(it)) }
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Divider(Modifier.padding(horizontal = 16.dp))
                                PropertyPicker(
                                    title = stringResource(R.string.cesdk_auto_size),
                                    propertyTextRes = uiState.sizeModeRes,
                                    properties = sizeModeList,
                                    onPropertyPicked = { onEvent(BlockEvent.OnChangeSizeMode(it)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private enum class ScreenState {
    Main, SelectFont, AdvancedOptions
}