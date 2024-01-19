package ly.img.editor.base.dock

import androidx.annotation.StringRes
import ly.img.editor.core.ui.tab_item.TabIcon

data class OptionItemData(val optionType: OptionType, @StringRes val labelStringRes: Int, val icon: TabIcon)

enum class OptionType {
    Replace,
    Layer,
    Edit,
    Format,
    ShapeOptions,
    FillStroke,
    EnterGroup,
    SelectGroup,
    Crop,
    Filter,
    Effect,
    Blur,
    Adjustments,
}