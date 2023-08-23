package ly.img.cesdk.bottomsheet.message_size

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import ly.img.cesdk.components.ToggleIconButton

@Composable
fun MessageSizeButton(
    messageSize: MessageSize,
    currentMessageSize: MessageSize,
    changeMessageSize: (MessageSize) -> Unit
) {
    ToggleIconButton(
        checked = currentMessageSize == messageSize,
        onCheckedChange = {
            changeMessageSize(messageSize)
        },
    ) {
        Icon(
            imageVector = messageSize.icon,
            contentDescription = null
        )
    }
}