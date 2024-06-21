package ly.img.editor.base.rootdock

import androidx.compose.runtime.Composable
import ly.img.editor.core.ui.tab_item.TabItem

@Composable
fun RootDockItem(
    data: RootDockItemData,
    onClick: () -> Unit,
) {
    TabItem(
        onClick = onClick,
        textRes = data.labelStringRes,
        icon = data.icon,
    )
}
