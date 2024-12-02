package ly.img.editor.postcard.bottomsheet.message_font

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ly.img.editor.base.R
import ly.img.editor.base.dock.BottomSheetContent
import ly.img.editor.base.dock.options.format.FontListUi
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.sheet.SheetType
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.postcard.PostcardEvent

@Composable
fun MessageFontSheet(
    uiState: MessageFontUiState,
    onEvent: (EditorEvent) -> Unit,
) {
    Column {
        SheetHeader(
            title = stringResource(id = R.string.ly_img_editor_font),
            onClose = { onEvent(EditorEvent.Sheet.Close(animate = true)) },
        )

        FontListUi(
            libraryCategory = uiState.libraryCategory,
            fontFamily = uiState.fontFamily,
            filter = uiState.filter,
            onSelectFont = { fontData ->
                onEvent(PostcardEvent.OnChangeTypeface(fontData.typeface))
            },
        )
    }
}

class MessageFontBottomSheetContent(
    override val type: SheetType,
    val uiState: MessageFontUiState,
) : BottomSheetContent
