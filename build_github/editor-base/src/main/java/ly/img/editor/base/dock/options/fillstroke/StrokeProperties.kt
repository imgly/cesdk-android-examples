package ly.img.editor.base.dock.options.fillstroke

import ly.img.editor.base.R
import ly.img.editor.base.components.Property
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Joinbevel
import ly.img.editor.core.ui.iconpack.Joinmiter
import ly.img.editor.core.ui.iconpack.Joinround
import ly.img.editor.core.ui.iconpack.Strokepositioncenter
import ly.img.editor.core.ui.iconpack.Strokepositioninside
import ly.img.editor.core.ui.iconpack.Strokepositionoutside
import ly.img.engine.StrokeCornerGeometry
import ly.img.engine.StrokePosition
import ly.img.engine.StrokeStyle

private val strokeStyles =
    linkedMapOf(
        StrokeStyle.SOLID to Property(R.string.ly_img_editor_stroke_solid, StrokeStyle.SOLID.name),
        StrokeStyle.DASHED to Property(R.string.ly_img_editor_stroke_dashed, StrokeStyle.DASHED.name),
        StrokeStyle.DASHED_ROUND to Property(R.string.ly_img_editor_stroke_dashed_round, StrokeStyle.DASHED_ROUND.name),
        StrokeStyle.LONG_DASHED to Property(R.string.ly_img_editor_stroke_long_dashed, StrokeStyle.LONG_DASHED.name),
        StrokeStyle.LONG_DASHED_ROUND to
            Property(
                R.string.ly_img_editor_stroke_long_dashed_round,
                StrokeStyle.LONG_DASHED_ROUND.name,
            ),
        StrokeStyle.DOTTED to Property(R.string.ly_img_editor_stroke_dotted, StrokeStyle.DOTTED.name),
    )

private val strokePositions =
    linkedMapOf(
        StrokePosition.INNER to
            Property(
                R.string.ly_img_editor_stroke_inner,
                StrokePosition.INNER.name,
                IconPack.Strokepositioninside,
            ),
        StrokePosition.CENTER to
            Property(
                R.string.ly_img_editor_stroke_center,
                StrokePosition.CENTER.name,
                IconPack.Strokepositioncenter,
            ),
        StrokePosition.OUTER to
            Property(
                R.string.ly_img_editor_stroke_outer,
                StrokePosition.OUTER.name,
                IconPack.Strokepositionoutside,
            ),
    )

private val strokeJoins =
    linkedMapOf(
        StrokeCornerGeometry.MITER to
            Property(
                R.string.ly_img_editor_stroke_miter,
                StrokeCornerGeometry.MITER.name,
                IconPack.Joinmiter,
            ),
        StrokeCornerGeometry.BEVEL to
            Property(
                R.string.ly_img_editor_stroke_bevel,
                StrokeCornerGeometry.BEVEL.name,
                IconPack.Joinbevel,
            ),
        StrokeCornerGeometry.ROUND to
            Property(
                R.string.ly_img_editor_stroke_round,
                StrokeCornerGeometry.ROUND.name,
                IconPack.Joinround,
            ),
    )

val strokeStylePropertiesList = strokeStyles.map { it.value }
val strokePositionPropertiesList = strokePositions.map { it.value }
val strokeJoinPropertiesList = strokeJoins.map { it.value }

fun StrokeStyle.getText() = requireNotNull(strokeStyles[this]).textRes

fun StrokePosition.getText() = requireNotNull(strokePositions[this]).textRes

fun StrokeCornerGeometry.getText() = requireNotNull(strokeJoins[this]).textRes
