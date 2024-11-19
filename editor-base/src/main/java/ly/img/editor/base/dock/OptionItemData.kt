package ly.img.editor.base.dock

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ly.img.editor.core.component.data.EditorIcon

data class OptionItemData(
    val optionType: OptionType,
    @StringRes val labelStringRes: Int,
    val icon: EditorIcon,
    val textColor: (@Composable () -> Color)? = null,
)

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
    Reorder,
    Filter,
    Effect,
    Blur,
    Adjustments,
    Volume,
    Split,
    Duplicate,
    Delete,
    AttachBackground,
    DetachBackground,
}
