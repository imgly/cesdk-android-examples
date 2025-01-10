package ly.img.editor.base.dock.options.format

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ly.img.editor.base.R
import ly.img.editor.base.components.NestedSheetHeader
import ly.img.editor.base.components.PropertyLink
import ly.img.editor.base.components.PropertyPicker
import ly.img.editor.base.components.PropertySlider
import ly.img.editor.base.components.PropertySwitch
import ly.img.editor.base.components.SectionHeader
import ly.img.editor.base.components.ToggleIconButton
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.core.ui.UiDefaults
import ly.img.editor.core.ui.iconpack.Formatbold
import ly.img.editor.core.ui.iconpack.Formatitalic
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.sheetScrollableContentModifier
import ly.img.engine.FontStyle
import ly.img.engine.FontWeight
import ly.img.engine.TextCase
import ly.img.editor.core.R as CoreR

@Composable
fun FormatOptionsSheet(
    uiState: FormatUiState,
    onEvent: (EditorEvent) -> Unit,
) {
    var screenState by remember { mutableStateOf(ScreenState.Main) }

    BackHandler(enabled = screenState != ScreenState.Main) {
        screenState = ScreenState.Main
    }

    when (screenState) {
        ScreenState.Main -> {
            Column {
                SheetHeader(
                    title = stringResource(id = CoreR.string.ly_img_editor_format),
                    onClose = { onEvent(EditorEvent.Sheet.Close(animate = true)) },
                )

                Column(
                    modifier = Modifier.sheetScrollableContentModifier(),
                ) {
                    Card(
                        colors = UiDefaults.cardColors,
                    ) {
                        PropertyLink(
                            title = stringResource(id = R.string.ly_img_editor_font),
                            value = uiState.fontFamily,
                        ) {
                            screenState = ScreenState.SelectFont
                        }
                        Divider(Modifier.padding(horizontal = 16.dp))
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 0.dp, top = 4.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row {
                                ToggleIconButton(
                                    checked = uiState.isBold,
                                    onCheckedChange = {
                                        onEvent(BlockEvent.OnBoldToggle)
                                    },
                                    enabled = uiState.canToggleBold,
                                ) {
                                    Icon(
                                        IconPack.Formatbold,
                                        contentDescription = stringResource(R.string.ly_img_editor_bold),
                                    )
                                }
                                ToggleIconButton(
                                    checked = uiState.isItalic,
                                    onCheckedChange = {
                                        onEvent(BlockEvent.OnItalicToggle)
                                    },
                                    enabled = uiState.canToggleItalic,
                                ) {
                                    Icon(
                                        IconPack.Formatitalic,
                                        contentDescription =
                                            stringResource(
                                                R.string.ly_img_editor_italic,
                                            ),
                                    )
                                }
                            }

                            PropertyLink(
                                value =
                                    stringResource(
                                        getWeightStringResource(
                                            androidx.compose.ui.text.font
                                                .FontWeight(uiState.fontFamilyWeight.value),
                                            uiState.fontFamilyStyle,
                                        ),
                                    ),
                            ) {
                                screenState = ScreenState.SelectFontWeight
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    PropertySlider(
                        title = stringResource(R.string.ly_img_editor_font_size),
                        value = uiState.fontSize,
                        valueRange = 6f..90f,
                        onValueChange = { onEvent(BlockEvent.OnChangeFontSize(it)) },
                        onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                    )

                    Spacer(Modifier.height(16.dp))
                    SectionHeader(text = stringResource(R.string.ly_img_editor_alignment))
                    Card(
                        colors = UiDefaults.cardColors,
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row {
                                HorizontalAlignment.entries.forEach {
                                    AlignmentButton(
                                        alignment = it,
                                        currentAlignment = uiState.horizontalAlignment,
                                        changeAlignment = {
                                            onEvent(
                                                BlockEvent.OnChangeHorizontalAlignment(it),
                                            )
                                        },
                                    )
                                }
                            }

                            Row {
                                VerticalAlignment.entries.forEach {
                                    AlignmentButton(
                                        alignment = it,
                                        currentAlignment = uiState.verticalAlignment,
                                        changeAlignment = {
                                            onEvent(
                                                BlockEvent.OnChangeVerticalAlignment(it),
                                            )
                                        },
                                    )
                                }
                            }
                        }
                    }
                    if (uiState.isArrangeResizeAllowed) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            colors = UiDefaults.cardColors,
                        ) {
                            PropertyPicker(
                                title = stringResource(R.string.ly_img_editor_frame_behaviour),
                                propertyTextRes = uiState.sizeModeRes,
                                properties = sizeModeList,
                                onPropertyPicked = { onEvent(BlockEvent.OnChangeSizeMode(it)) },
                            )
                            if (uiState.hasClippingOption) {
                                PropertySwitch(
                                    title = stringResource(R.string.ly_img_editor_frame_clipping),
                                    propertyTextRes = R.string.ly_img_editor_frame_clipping,
                                    isChecked = uiState.isClipped,
                                    onPropertyChange = {
                                        onEvent(BlockEvent.OnChangeClipping(it))
                                    },
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    PropertySlider(
                        title = stringResource(R.string.ly_img_editor_line_height),
                        value = uiState.lineHeight,
                        onValueChange = { onEvent(BlockEvent.OnChangeLineHeight(it)) },
                        onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                        valueRange = 0.5f..2.5f,
                    )
                    Spacer(Modifier.height(16.dp))
                    PropertySlider(
                        title = stringResource(R.string.ly_img_editor_paragraph_spacing),
                        value = uiState.paragraphSpacing,
                        onValueChange = { onEvent(BlockEvent.OnChangeParagraphSpacing(it)) },
                        onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                        valueRange = -0.15f..1.4f,
                    )

                    Spacer(Modifier.height(16.dp))
                    SectionHeader(text = stringResource(R.string.ly_img_editor_latter_case))
                    Card(
                        colors = UiDefaults.cardColors,
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            TextCase.entries.forEach {
                                TextCaseButton(
                                    casing = it,
                                    currentCasing = uiState.casing,
                                    changeCasing = {
                                        onEvent(
                                            BlockEvent.OnChangeLetterCasing(it),
                                        )
                                    },
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    PropertySlider(
                        title = stringResource(R.string.ly_img_editor_letter_spacing),
                        value = uiState.letterSpacing,
                        onValueChange = { onEvent(BlockEvent.OnChangeLetterSpacing(it)) },
                        onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                        valueRange = -0.15f..1.4f,
                    )
                }
            }
        }

        ScreenState.SelectFont -> {
            Column {
                NestedSheetHeader(
                    title = stringResource(R.string.ly_img_editor_font),
                    onBack = { screenState = ScreenState.Main },
                    onClose = { onEvent(EditorEvent.Sheet.Close(animate = true)) },
                )
                FontListUi(
                    libraryCategory = uiState.libraryCategory,
                    fontFamily = uiState.fontFamily,
                    filter = emptyList(),
                    onSelectFont = { fontData ->
                        onEvent(BlockEvent.OnChangeTypeface(fontData.typeface))
                    },
                )
            }
        }

        ScreenState.SelectFontWeight -> {
            Column {
                NestedSheetHeader(
                    title = stringResource(R.string.ly_img_editor_font),
                    onBack = { screenState = ScreenState.Main },
                    onClose = { onEvent(EditorEvent.Sheet.Close(animate = true)) },
                )
                FontListUi(
                    fontList = uiState.availableWeights,
                    selectedFontFamily = uiState.fontFamily,
                    selectedWeight = uiState.fontFamilyWeight,
                    selectedStyle = uiState.fontFamilyStyle,
                    labelMap = { stringResource(it.getWeightStringResource()) },
                    onSelectFont = { fontData ->
                        onEvent(BlockEvent.OnChangeFont(fontData.uri, fontData.typeface))
                    },
                )
            }
        }
    }
}

private enum class ScreenState {
    Main,
    SelectFont,
    SelectFontWeight,
}

@Composable
@Preview(showBackground = true)
fun DefaultPreview() {
    FormatOptionsSheet(
        uiState =
            FormatUiState(
                fontFamily = "Roboto",
                fontSize = 16f,
                letterSpacing = 0f,
                lineHeight = 1f,
                isBold = true,
                isItalic = false,
                canToggleBold = true,
                canToggleItalic = true,
                horizontalAlignment = HorizontalAlignment.Left,
                verticalAlignment = VerticalAlignment.Top,
                sizeModeRes = R.string.ly_img_editor_fixed_size,
                isArrangeResizeAllowed = true,
                libraryCategory = LibraryCategory.Text,
                casing = TextCase.UPPER_CASE,
                paragraphSpacing = 0f,
                fontFamilyWeight = FontWeight.NORMAL,
                availableWeights = emptyList(),
                fontFamilyStyle = FontStyle.NORMAL,
                hasClippingOption = true,
                isClipped = true,
            ),
        onEvent = {},
    )
}
