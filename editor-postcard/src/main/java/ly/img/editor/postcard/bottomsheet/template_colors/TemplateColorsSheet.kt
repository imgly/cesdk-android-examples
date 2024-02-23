package ly.img.editor.postcard.bottomsheet.template_colors

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
import ly.img.editor.base.components.SectionHeader
import ly.img.editor.base.dock.BottomSheetContent
import ly.img.editor.base.dock.HalfHeightContainer
import ly.img.editor.base.dock.options.fillstroke.ColorOptions
import ly.img.editor.base.dock.options.fillstroke.ColorPickerSheet
import ly.img.editor.base.engine.toComposeColor
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.base.ui.Event
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.core.ui.UiDefaults
import ly.img.editor.core.ui.inspectorSheetPadding
import ly.img.editor.postcard.PostcardEvent
import ly.img.editor.postcard.R
import ly.img.editor.postcard.util.NamedColor

@Composable
fun TemplateColorsSheet(
    uiState: TemplateColorsUiState,
    onEvent: (Event) -> Unit,
) {
    var screenState: ScreenState by remember { mutableStateOf(ScreenState.Main) }

    BackHandler(enabled = screenState != ScreenState.Main) {
        screenState = ScreenState.Main
    }

    when (screenState) {
        ScreenState.Main -> {
            HalfHeightContainer {
                Column {
                    SheetHeader(
                        title = stringResource(id = R.string.ly_img_editor_template_colors),
                        onClose = { onEvent(Event.OnHideSheet) },
                    )
                    Column(
                        Modifier
                            .inspectorSheetPadding()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        uiState.colorSections.forEachIndexed { index, namedColor ->
                            SectionHeader(namedColor.name)
                            Card(
                                colors = UiDefaults.cardColors,
                            ) {
                                ColorOptions(
                                    enabled = true,
                                    allowDisableColor = false,
                                    selectedColor = namedColor.color.toComposeColor(),
                                    onNoColorSelected = { },
                                    onColorSelected = {
                                        onEvent(
                                            PostcardEvent.OnChangeTemplateColor(namedColor.name, it),
                                        )
                                        onEvent(BlockEvent.OnChangeFinish)
                                    },
                                    openColorPicker = {
                                        screenState = ScreenState.SectionColorPicker(namedColor)
                                    },
                                    colors = uiState.colorPalette,
                                )
                            }
                            if (index < uiState.colorSections.size - 1) {
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }

        is ScreenState.SectionColorPicker -> {
            val state = screenState as? ScreenState.SectionColorPicker ?: return
            ColorPickerSheet(
                color = state.namedColor.color.toComposeColor(),
                title = stringResource(R.string.ly_img_editor_template_color, state.namedColor.name),
                onBack = { screenState = ScreenState.Main },
                onColorChange = {
                    onEvent(PostcardEvent.OnChangeTemplateColor(state.namedColor.name, it))
                },
                onEvent = onEvent,
            )
        }
    }
}

private sealed interface ScreenState {
    data object Main : ScreenState

    data class SectionColorPicker(val namedColor: NamedColor) : ScreenState
}

class TemplateColorsBottomSheetContent(val uiState: TemplateColorsUiState) : BottomSheetContent
