package ly.img.editor.base.dock.options.format

import androidx.compose.ui.text.font.FontWeight
import ly.img.editor.base.R
import ly.img.editor.base.components.Property
import ly.img.editor.core.ui.iconpack.AutoSize
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Textautoheight
import ly.img.editor.core.ui.iconpack.Textfixedsize
import ly.img.editor.core.ui.library.data.font.FontData
import ly.img.engine.FontStyle

enum class SizeModeUi {
    ABSOLUTE,
    AUTO_HEIGHT,
    AUTO_SIZE,
    UNKNOWN,
}

private val sizeModes =
    linkedMapOf(
        SizeModeUi.AUTO_SIZE to
            Property(
                R.string.ly_img_editor_auto_size,
                SizeModeUi.AUTO_SIZE.name,
                IconPack.AutoSize,
            ),
        SizeModeUi.AUTO_HEIGHT to
            Property(
                R.string.ly_img_editor_auto_height,
                SizeModeUi.AUTO_HEIGHT.name,
                IconPack.Textautoheight,
            ),
        SizeModeUi.ABSOLUTE to
            Property(
                R.string.ly_img_editor_fixed_size,
                SizeModeUi.ABSOLUTE.name,
                IconPack.Textfixedsize,
            ),
    )

val sizeModeList = sizeModes.map { it.value }

fun SizeModeUi.getText() = requireNotNull(sizeModes[this]).textRes

fun getWeightStringResource(
    weight: FontWeight,
    style: FontStyle,
): Int =
    if (style == FontStyle.NORMAL) {
        when (weight) {
            FontWeight.W100 -> R.string.ly_img_editor_weight_W100
            FontWeight.W200 -> R.string.ly_img_editor_weight_W200
            FontWeight.W300 -> R.string.ly_img_editor_weight_W300
            FontWeight.W400 -> R.string.ly_img_editor_weight_W400
            FontWeight.W500 -> R.string.ly_img_editor_weight_W500
            FontWeight.W600 -> R.string.ly_img_editor_weight_W600
            FontWeight.W700 -> R.string.ly_img_editor_weight_W700
            FontWeight.W800 -> R.string.ly_img_editor_weight_W800
            FontWeight.W900 -> R.string.ly_img_editor_weight_W900
            else -> throw IllegalArgumentException("Unknown weight $weight")
        }
    } else {
        when (weight) {
            FontWeight.W100 -> R.string.ly_img_editor_weight_W100_italic
            FontWeight.W200 -> R.string.ly_img_editor_weight_W200_italic
            FontWeight.W300 -> R.string.ly_img_editor_weight_W300_italic
            FontWeight.W400 -> R.string.ly_img_editor_weight_W400_italic
            FontWeight.W500 -> R.string.ly_img_editor_weight_W500_italic
            FontWeight.W600 -> R.string.ly_img_editor_weight_W600_italic
            FontWeight.W700 -> R.string.ly_img_editor_weight_W700_italic
            FontWeight.W800 -> R.string.ly_img_editor_weight_W800_italic
            FontWeight.W900 -> R.string.ly_img_editor_weight_W900_italic
            else -> throw IllegalArgumentException("Unknown weight $weight")
        }
    }

fun FontData.getWeightStringResource(): Int = getWeightStringResource(weight, style)
