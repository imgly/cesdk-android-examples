package ly.img.editor.postcard.bottomsheet.message_color

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import ly.img.editor.base.dock.BottomSheetContent
import ly.img.editor.base.dock.options.fillstroke.ColorOptions
import ly.img.editor.base.dock.options.fillstroke.ColorPickerSheet
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.sheet.SheetType
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.core.ui.UiDefaults
import ly.img.editor.core.ui.sheetScrollableContentModifier
import ly.img.editor.postcard.PostcardEvent
import ly.img.editor.postcard.R

@Composable
fun MessageColorSheet(
    color: Color,
    onColorPickerActiveChanged: (active: Boolean) -> Unit,
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
                    title = stringResource(id = R.string.ly_img_editor_color),
                    onClose = { onEvent(EditorEvent.Sheet.Close(animate = true)) },
                )

                Card(
                    Modifier.sheetScrollableContentModifier(),
                    colors = UiDefaults.cardColors,
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
                            onColorPickerActiveChanged(true)
                            screenState = ScreenState.ColorPicker
                        },
                        colors =
                            listOf(
                                Color(0xFF263BAA),
                                Color(0xFF002094),
                                Color(0xFF001346),
                                Color(0xFF000000),
                                Color(0xFF696969),
                                Color(0xFF999999),
                            ),
                    )
                }
            }
        }

        ScreenState.ColorPicker -> {
            ColorPickerSheet(
                color = color,
                title = stringResource(id = R.string.ly_img_editor_message_color),
                onBack = {
                    onColorPickerActiveChanged(false)
                    screenState = ScreenState.Main
                },
                onColorChange = {
                    onEvent(PostcardEvent.OnChangeMessageColor(it))
                },
                onEvent = onEvent,
            )
        }
    }
}

private enum class ScreenState {
    Main,
    ColorPicker,
}

class MessageColorBottomSheetContent(
    override val type: SheetType,
    val color: Color,
) : BottomSheetContent
