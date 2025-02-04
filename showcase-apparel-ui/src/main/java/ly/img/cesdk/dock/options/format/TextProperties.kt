package ly.img.cesdk.dock.options.format

import ly.img.cesdk.apparelui.R
import ly.img.cesdk.core.components.Property
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.iconpack.Textautoheight
import ly.img.cesdk.core.iconpack.Textfixedsize
import ly.img.engine.SizeMode

private val sizeModes = linkedMapOf(
    SizeMode.ABSOLUTE to Property(R.string.cesdk_fixed_size, SizeMode.ABSOLUTE.name, IconPack.Textfixedsize),
    SizeMode.AUTO to Property(R.string.cesdk_auto_height, SizeMode.AUTO.name, IconPack.Textautoheight)
)

val sizeModeList = sizeModes.map { it.value }

fun SizeMode.getText() = requireNotNull(sizeModes[this]).textRes