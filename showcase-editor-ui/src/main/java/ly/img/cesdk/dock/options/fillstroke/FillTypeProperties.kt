package ly.img.cesdk.dock.options.fillstroke

import ly.img.cesdk.components.Property
import ly.img.engine.FillType
import ly.img.cesdk.editorui.R

val fillTypePropertiesList = listOf(
    Property(R.string.cesdk_fill_none, "NONE"),
    Property(R.string.cesdk_fill_solid, FillType.Color.key),
    Property(R.string.cesdk_fill_type_gradient_linear, FillType.LinearGradient.key)
)