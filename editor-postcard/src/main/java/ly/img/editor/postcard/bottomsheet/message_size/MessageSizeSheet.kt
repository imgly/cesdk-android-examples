package ly.img.editor.postcard.bottomsheet.message_size

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.base.dock.BottomSheetContent
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.sheet.SheetType
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.core.ui.UiDefaults
import ly.img.editor.core.ui.sheetScrollableContentModifier
import ly.img.editor.postcard.PostcardEvent
import ly.img.editor.postcard.R

@Composable
fun MessageSizeSheet(
    messageSize: MessageSize,
    onEvent: (EditorEvent) -> Unit,
) {
    Column {
        SheetHeader(
            title = stringResource(id = R.string.ly_img_editor_size),
            onClose = { onEvent(EditorEvent.Sheet.Close(animate = true)) },
        )

        Card(
            Modifier.sheetScrollableContentModifier(),
            colors = UiDefaults.cardColors,
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    stringResource(R.string.ly_img_editor_message),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Row {
                    MessageSize.values().forEach {
                        MessageSizeButton(
                            messageSize = it,
                            currentMessageSize = messageSize,
                            changeMessageSize = {
                                onEvent(
                                    PostcardEvent.OnChangeMessageSize(it),
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

class MessageSizeBottomSheetContent(
    override val type: SheetType,
    val messageSize: MessageSize,
) : BottomSheetContent
