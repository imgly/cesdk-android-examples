package ly.img.cesdk.bottomsheet.template_colors

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
import ly.img.cesdk.PostcardEvent
import ly.img.cesdk.core.UiDefaults
import ly.img.cesdk.core.components.SectionHeader
import ly.img.cesdk.core.components.SheetHeader
import ly.img.cesdk.core.inspectorSheetPadding
import ly.img.cesdk.dock.BottomSheetContent
import ly.img.cesdk.dock.HalfHeightContainer
import ly.img.cesdk.dock.options.fillstroke.ColorOptions
import ly.img.cesdk.dock.options.fillstroke.ColorPickerSheet
import ly.img.cesdk.editorui.BlockEvent
import ly.img.cesdk.editorui.Event
import ly.img.cesdk.engine.toComposeColor
import ly.img.cesdk.postcardui.R
import ly.img.cesdk.util.NamedColor

@Composable
fun TemplateColorsSheet(
    uiState: TemplateColorsUiState,
    onEvent: (Event) -> Unit
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
                        title = stringResource(id = R.string.cesdk_template_colors),
                        onClose = { onEvent(Event.HideSheet) }
                    )
                    Column(
                        Modifier
                            .inspectorSheetPadding()
                            .verticalScroll(rememberScrollState())
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
                                        onEvent(PostcardEvent.OnChangeTemplateColor(namedColor.name, it))
                                        onEvent(BlockEvent.OnChangeFinish)
                                    },
                                    openColorPicker = {
                                        screenState = ScreenState.SectionColorPicker(namedColor)
                                    },
                                    colors = uiState.colorPalette
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
                title = stringResource(R.string.cesdk_template_color, state.namedColor.name),
                onBack = { screenState = ScreenState.Main },
                onColorChange = {
                    onEvent(PostcardEvent.OnChangeTemplateColor(state.namedColor.name, it))
                },
                onEvent = onEvent
            )
        }
    }
}

private sealed interface ScreenState {
    object Main : ScreenState
    data class SectionColorPicker(val namedColor: NamedColor) : ScreenState
}

class TemplateColorsBottomSheetContent(val uiState: TemplateColorsUiState) : BottomSheetContent