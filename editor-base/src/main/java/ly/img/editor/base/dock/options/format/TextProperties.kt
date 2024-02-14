package ly.img.editor.base.dock.options.format

import ly.img.editor.base.R
import ly.img.editor.base.components.Property
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Textautoheight
import ly.img.editor.core.ui.iconpack.Textfixedsize
import ly.img.engine.SizeMode

private val sizeModes =
    linkedMapOf(
        SizeMode.ABSOLUTE to Property(R.string.ly_img_editor_fixed_size, SizeMode.ABSOLUTE.name, IconPack.Textfixedsize),
        SizeMode.AUTO to Property(R.string.ly_img_editor_auto_height, SizeMode.AUTO.name, IconPack.Textautoheight),
    )

val sizeModeList = sizeModes.map { it.value }

fun SizeMode.getText() = requireNotNull(sizeModes[this]).textRes
