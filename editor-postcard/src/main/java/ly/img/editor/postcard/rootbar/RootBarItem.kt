package ly.img.editor.postcard.rootbar

import androidx.compose.runtime.Composable
import ly.img.editor.base.ui.Event
import ly.img.editor.core.ui.tab_item.TabItem
import ly.img.editor.postcard.PostcardEvent

@Composable
fun RootBarItem(
    data: RootBarItemData,
    onEvent: (Event) -> Unit,
) {
    TabItem(
        onClick = { onEvent(PostcardEvent.OnRootBarItemClick(data.type)) },
        textRes = data.labelStringRes,
        icon = data.icon,
    )
}
