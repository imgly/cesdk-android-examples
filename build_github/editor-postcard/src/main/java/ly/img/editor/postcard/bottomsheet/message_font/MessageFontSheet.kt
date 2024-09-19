package ly.img.editor.postcard.bottomsheet.message_font

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ly.img.editor.base.R
import ly.img.editor.base.dock.BottomSheetContent
import ly.img.editor.base.dock.HalfHeightContainer
import ly.img.editor.base.dock.options.format.FontListUi
import ly.img.editor.base.ui.Event
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.postcard.PostcardEvent

@Composable
fun MessageFontSheet(
    uiState: MessageFontUiState,
    onEvent: (Event) -> Unit,
) {
    HalfHeightContainer {
        Column {
            SheetHeader(
                title = stringResource(id = R.string.ly_img_editor_font),
                onClose = { onEvent(Event.OnHideSheet) },
            )

            FontListUi(
                libraryCategory = uiState.libraryCategory,
                fontFamily = uiState.fontFamily,
                filter = uiState.filter,
                onSelectFont = { fontData ->
                    onEvent(PostcardEvent.OnChangeFont(fontData.uri, fontData.typeface))
                },
            )
        }
    }
}

class MessageFontBottomSheetContent(val uiState: MessageFontUiState) : BottomSheetContent
