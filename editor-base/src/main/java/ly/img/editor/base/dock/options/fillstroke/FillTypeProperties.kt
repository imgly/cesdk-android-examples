package ly.img.editor.base.dock.options.fillstroke

import ly.img.editor.base.R
import ly.img.editor.base.components.Property
import ly.img.engine.FillType

val fillTypePropertiesList = listOf(
    Property(R.string.cesdk_fill_none, "NONE"),
    Property(R.string.cesdk_fill_solid, FillType.Color.key),
    Property(R.string.cesdk_fill_type_gradient_linear, FillType.LinearGradient.key)
)