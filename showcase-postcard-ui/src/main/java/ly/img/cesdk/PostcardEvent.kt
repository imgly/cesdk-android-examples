package ly.img.cesdk

import androidx.compose.ui.graphics.Color
import ly.img.cesdk.bottomsheet.message_size.MessageSize
import ly.img.cesdk.editorui.Event
import ly.img.cesdk.library.data.font.FontData
import ly.img.cesdk.rootbar.RootBarItemType

sealed interface PostcardEvent : Event {
    object OnNextPage : PostcardEvent
    object OnPreviousPage : PostcardEvent
    data class OnRootBarItemClick(val itemType: RootBarItemType) : PostcardEvent
    data class OnChangeMessageSize(val messageSize: MessageSize) : PostcardEvent
    data class OnChangeMessageColor(val color: Color) : PostcardEvent
    data class OnChangeMessageFont(val fontData: FontData) : PostcardEvent
    data class OnChangeTemplateColor(val name: String, val color: Color) : PostcardEvent
}