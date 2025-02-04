package ly.img.cesdk.dock.options.fillstroke

import ly.img.cesdk.apparelui.R
import ly.img.cesdk.core.components.Property
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.iconpack.Joinbevel
import ly.img.cesdk.core.iconpack.Joinmiter
import ly.img.cesdk.core.iconpack.Joinround
import ly.img.cesdk.core.iconpack.Strokepositioncenter
import ly.img.cesdk.core.iconpack.Strokepositioninside
import ly.img.cesdk.core.iconpack.Strokepositionoutside
import ly.img.engine.StrokeCornerGeometry
import ly.img.engine.StrokePosition
import ly.img.engine.StrokeStyle

private val strokeStyles = linkedMapOf(
    StrokeStyle.SOLID to Property(R.string.cesdk_stroke_solid, StrokeStyle.SOLID.name),
    StrokeStyle.DASHED to Property(R.string.cesdk_stroke_dashed, StrokeStyle.DASHED.name),
    StrokeStyle.DASHED_ROUND to Property(R.string.cesdk_stroke_dashed_round, StrokeStyle.DASHED_ROUND.name),
    StrokeStyle.LONG_DASHED to Property(R.string.cesdk_stroke_long_dashed, StrokeStyle.LONG_DASHED.name),
    StrokeStyle.LONG_DASHED_ROUND to Property(R.string.cesdk_stroke_long_dashed_round, StrokeStyle.LONG_DASHED_ROUND.name),
    StrokeStyle.DOTTED to Property(R.string.cesdk_stroke_dotted, StrokeStyle.DOTTED.name)
)

private val strokePositions = linkedMapOf(
    StrokePosition.INNER to Property(R.string.cesdk_stroke_inner, StrokePosition.INNER.name, IconPack.Strokepositioninside),
    StrokePosition.CENTER to Property(R.string.cesdk_stroke_center, StrokePosition.CENTER.name, IconPack.Strokepositioncenter),
    StrokePosition.OUTER to Property(R.string.cesdk_stroke_outer, StrokePosition.OUTER.name, IconPack.Strokepositionoutside)
)

private val strokeJoins = linkedMapOf(
    StrokeCornerGeometry.MITER to Property(R.string.cesdk_stroke_miter, StrokeCornerGeometry.MITER.name, IconPack.Joinmiter),
    StrokeCornerGeometry.BEVEL to Property(R.string.cesdk_stroke_bevel, StrokeCornerGeometry.BEVEL.name, IconPack.Joinbevel),
    StrokeCornerGeometry.ROUND to Property(R.string.cesdk_stroke_round, StrokeCornerGeometry.ROUND.name, IconPack.Joinround)
)

val strokeStylePropertiesList = strokeStyles.map { it.value }
val strokePositionPropertiesList = strokePositions.map { it.value }
val strokeJoinPropertiesList = strokeJoins.map { it.value }

fun StrokeStyle.getText() = requireNotNull(strokeStyles[this]).textRes
fun StrokePosition.getText() = requireNotNull(strokePositions[this]).textRes
fun StrokeCornerGeometry.getText() = requireNotNull(strokeJoins[this]).textRes