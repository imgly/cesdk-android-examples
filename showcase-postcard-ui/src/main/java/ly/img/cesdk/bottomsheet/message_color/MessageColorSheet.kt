package ly.img.cesdk.bottomsheet.message_color

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import ly.img.cesdk.PostcardEvent
import ly.img.cesdk.core.UiDefaults
import ly.img.cesdk.core.components.SheetHeader
import ly.img.cesdk.core.inspectorSheetPadding
import ly.img.cesdk.dock.BottomSheetContent
import ly.img.cesdk.dock.HalfHeightContainer
import ly.img.cesdk.dock.options.fillstroke.ColorOptions
import ly.img.cesdk.dock.options.fillstroke.ColorPickerSheet
import ly.img.cesdk.editorui.BlockEvent
import ly.img.cesdk.editorui.Event
import ly.img.cesdk.postcardui.R

@Composable
fun MessageColorSheet(
    color: Color,
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
                        title = stringResource(id = R.string.cesdk_color),
                        onClose = { onEvent(Event.HideSheet) }
                    )

                    Card(
                        Modifier
                            .inspectorSheetPadding()
                            .verticalScroll(rememberScrollState()),
                        colors = UiDefaults.cardColors
                    ) {
                        ColorOptions(
                            enabled = true,
                            allowDisableColor = false,
                            selectedColor = color,
                            onNoColorSelected = { },
                            onColorSelected = {
                                onEvent(PostcardEvent.OnChangeMessageColor(it))
                                if (it != color) {
                                    onEvent(BlockEvent.OnChangeFinish)
                                }
                            },
                            openColorPicker = {
                                screenState = ScreenState.ColorPicker
                            },
                            colors = listOf(
                                Color(0xFF263BAA),
                                Color(0xFF002094),
                                Color(0xFF001346),
                                Color(0xFF000000),
                                Color(0xFF696969),
                                Color(0xFF999999)
                            )
                        )
                    }
                }
            }
        }

        ScreenState.ColorPicker -> {
            ColorPickerSheet(
                color = color,
                title = stringResource(id = R.string.cesdk_message_color),
                onBack = { screenState = ScreenState.Main },
                onColorChange = {
                    onEvent(PostcardEvent.OnChangeMessageColor(it))
                },
                onEvent = onEvent
            )
        }
    }
}

private enum class ScreenState {
    Main, ColorPicker
}

class MessageColorBottomSheetContent(val color: Color) : BottomSheetContent