package ly.img.editor.base.dock

import androidx.compose.runtime.Composable
import ly.img.editor.core.ui.tab_item.TabItem

@Composable
fun DockMenuOption(
    data: OptionItemData,
    onClick: (OptionType) -> Unit,
) {
    TabItem(
        onClick = { onClick(data.optionType) },
        textRes = data.labelStringRes,
        icon = data.icon,
    )
}
