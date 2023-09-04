package ly.img.cesdk.rootbar

import androidx.compose.runtime.Composable
import ly.img.cesdk.PostcardEvent
import ly.img.cesdk.core.ui.tab_item.TabItem
import ly.img.cesdk.editorui.Event

@Composable
fun RootBarItem(
    data: RootBarItemData,
    onEvent: (Event) -> Unit
) {
    TabItem(
        onClick = { onEvent(PostcardEvent.OnRootBarItemClick(data.type)) },
        textRes = data.labelStringRes,
        icon = data.icon
    )
}