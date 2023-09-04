package ly.img.cesdk.rootbar.tab_icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ly.img.cesdk.components.ColorButton
import ly.img.cesdk.core.ui.tab_item.TabIcon
import ly.img.cesdk.core.ui.tab_item.TabIconComposable

object MessageColorIconComposable : TabIconComposable<MessageColorIcon>() {

    @Composable
    override fun IconContent(icon: MessageColorIcon) {
        ColorButton(color = icon.messageColor, buttonSize = 24.dp, selectionStrokeWidth = 0.dp)
    }
}

class MessageColorIcon(val messageColor: Color) : TabIcon