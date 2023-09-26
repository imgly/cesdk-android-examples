package ly.img.cesdk.dock.options.fillstroke

import ly.img.cesdk.components.Property
import ly.img.cesdk.editorui.R
import ly.img.engine.DesignBlockType

val fillTypePropertiesList = listOf(
    Property(R.string.cesdk_fill_none, "NONE"),
    Property(R.string.cesdk_fill_solid, DesignBlockType.COLOR_FILL.key),
    Property(R.string.cesdk_fill_type_gradient_linear, DesignBlockType.LINEAR_GRADIENT_FILL.key)
)