package ly.img.cesdk.dock

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class OptionItemData(val optionType: OptionType, @StringRes val labelStringRes: Int, val icon: OptionIcon)

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

sealed interface OptionIcon {
    class Vector(val imageVector: ImageVector) : OptionIcon
    class FillStroke(val hasFill: Boolean, val fillColor: Color?, val hasStroke: Boolean, val strokeColor: Color?) : OptionIcon
}