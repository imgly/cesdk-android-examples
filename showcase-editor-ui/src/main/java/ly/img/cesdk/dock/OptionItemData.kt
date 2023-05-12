package ly.img.cesdk.dock

import androidx.annotation.StringRes
import ly.img.cesdk.core.components.tab_item.TabIcon

data class OptionItemData(val optionType: OptionType, @StringRes val labelStringRes: Int, val icon: TabIcon)

enum class OptionType {
    Replace,
    Layer,
    Edit,
    Format,
    ShapeOptions,
    FillStroke,
    EnterGroup,
    SelectGroup
}