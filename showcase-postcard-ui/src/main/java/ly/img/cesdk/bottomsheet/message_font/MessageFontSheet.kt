package ly.img.cesdk.bottomsheet.message_font

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ly.img.cesdk.PostcardEvent
import ly.img.cesdk.core.components.SheetHeader
import ly.img.cesdk.dock.BottomSheetContent
import ly.img.cesdk.dock.HalfHeightContainer
import ly.img.cesdk.dock.options.format.FontListUi
import ly.img.cesdk.editorui.Event

@Composable
fun MessageFontSheet(
    uiState: MessageFontUiState,
    onEvent: (Event) -> Unit
) {
    HalfHeightContainer {
        Column {
            SheetHeader(
                title = stringResource(id = ly.img.cesdk.editorui.R.string.cesdk_font),
                onClose = { onEvent(Event.HideSheet) }
            )

            FontListUi(
                fontFamily = uiState.fontFamily,
                fontFamilies = uiState.fontFamilies,
                onSelectFont = { onEvent(PostcardEvent.OnChangeMessageFont(it)) }
            )
        }
    }
}

class MessageFontBottomSheetContent(val uiState: MessageFontUiState) : BottomSheetContent