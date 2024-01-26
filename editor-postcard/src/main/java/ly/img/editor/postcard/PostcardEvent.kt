package ly.img.editor.postcard

import androidx.compose.ui.graphics.Color
import ly.img.editor.base.ui.Event
import ly.img.editor.core.ui.library.data.font.FontData
import ly.img.editor.postcard.bottomsheet.message_size.MessageSize
import ly.img.editor.postcard.rootbar.RootBarItemType

sealed interface PostcardEvent : Event {
    data object OnNextPage : PostcardEvent
    data object OnPreviousPage : PostcardEvent
    data class OnRootBarItemClick(val itemType: RootBarItemType) : PostcardEvent
    data class OnChangeMessageSize(val messageSize: MessageSize) : PostcardEvent
    data class OnChangeMessageColor(val color: Color) : PostcardEvent
    data class OnChangeMessageFont(val fontData: FontData) : PostcardEvent
    data class OnChangeTemplateColor(val name: String, val color: Color) : PostcardEvent
}