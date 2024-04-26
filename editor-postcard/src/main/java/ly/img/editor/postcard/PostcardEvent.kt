package ly.img.editor.postcard

import android.net.Uri
import androidx.compose.ui.graphics.Color
import ly.img.editor.base.ui.Event
import ly.img.editor.postcard.bottomsheet.message_size.MessageSize
import ly.img.editor.postcard.rootbar.RootBarItemType
import ly.img.engine.Typeface

sealed interface PostcardEvent : Event {
    data class OnRootBarItemClick(val itemType: RootBarItemType) : PostcardEvent

    data class OnChangeMessageSize(val messageSize: MessageSize) : PostcardEvent

    data class OnChangeMessageColor(val color: Color) : PostcardEvent

    data class OnChangeFont(val fontUri: Uri, val typeface: Typeface) : PostcardEvent

    data class OnChangeTemplateColor(val name: String, val color: Color) : PostcardEvent
}
