package ly.img.editor.core.ui.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private var _Play: ImageVector? = null

val IconPack.Play: ImageVector
    get() {
        if (_Play != null) {
            return _Play!!
        }
        _Play =
            ImageVector.Builder(
                name = "Play",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero,
                ) {
                    moveTo(8f, 5.13965f)
                    verticalLineTo(19.1396f)
                    lineTo(19f, 12.1396f)
                    lineTo(8f, 5.13965f)
                    close()
                }
            }.build()
        return _Play!!
    }
