package ly.img.cesdk.bottomsheet.message_size

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.cesdk.PostcardEvent
import ly.img.cesdk.core.ui.SheetHeader
import ly.img.cesdk.core.ui.UiDefaults
import ly.img.cesdk.core.ui.inspectorSheetPadding
import ly.img.cesdk.dock.BottomSheetContent
import ly.img.cesdk.dock.HalfHeightContainer
import ly.img.cesdk.editorui.Event
import ly.img.cesdk.postcardui.R

@Composable
fun MessageSizeSheet(
    messageSize: MessageSize,
    onEvent: (Event) -> Unit
) {
    HalfHeightContainer {
        Column {
            SheetHeader(
                title = stringResource(id = R.string.cesdk_size),
                onClose = { onEvent(Event.OnHideSheet) }
            )

            Card(
                Modifier
                    .inspectorSheetPadding()
                    .verticalScroll(rememberScrollState()),
                colors = UiDefaults.cardColors
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.cesdk_message),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Row {
                        MessageSize.values().forEach {
                            MessageSizeButton(
                                messageSize = it,
                                currentMessageSize = messageSize,
                                changeMessageSize = { onEvent(PostcardEvent.OnChangeMessageSize(it)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

class MessageSizeBottomSheetContent(val messageSize: MessageSize) : BottomSheetContent