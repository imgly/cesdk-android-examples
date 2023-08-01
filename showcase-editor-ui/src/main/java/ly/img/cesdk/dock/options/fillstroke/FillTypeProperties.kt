package ly.img.cesdk.dock.options.fillstroke

import ly.img.cesdk.core.components.Property
import ly.img.cesdk.editorui.R
import ly.img.engine.FillType
import ly.img.engine.GradientType

val fillTypePropertiesList = listOf(
    Property(R.string.cesdk_fill_none, "NONE"),
    Property(R.string.cesdk_fill_solid, FillType.SOLID.name),
    Property(R.string.cesdk_fill_type_gradient_linear, GradientType.LINEAR.name)
)