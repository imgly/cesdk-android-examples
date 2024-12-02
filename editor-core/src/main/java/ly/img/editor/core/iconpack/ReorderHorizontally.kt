package ly.img.editor.core.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Deprecated("Use IconPack.ReorderHorizontally instead.", ReplaceWith("IconPack.ReorderHorizontally"))
val IconPack.Reorderhorizontally: ImageVector
    get() = ReorderHorizontally

val IconPack.ReorderHorizontally: ImageVector
    get() {
        if (_Reorderhorizontally != null) {
            return _Reorderhorizontally!!
        }
        _Reorderhorizontally =
            ImageVector.Builder(
                name = "Reorderhorizontally",
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
                    pathFillType = PathFillType.EvenOdd,
                ) {
                    moveTo(16f, 5f)
                    horizontalLineTo(8f)
                    verticalLineTo(19f)
                    horizontalLineTo(16f)
                    verticalLineTo(5f)
                    close()
                    moveTo(8f, 3f)
                    curveTo(6.8954f, 3f, 6f, 3.8954f, 6f, 5f)
                    verticalLineTo(19f)
                    curveTo(6f, 20.1046f, 6.8954f, 21f, 8f, 21f)
                    horizontalLineTo(16f)
                    curveTo(17.1046f, 21f, 18f, 20.1046f, 18f, 19f)
                    verticalLineTo(5f)
                    curveTo(18f, 3.8954f, 17.1046f, 3f, 16f, 3f)
                    horizontalLineTo(8f)
                    close()
                    moveTo(20f, 8f)
                    lineTo(24f, 12f)
                    lineTo(20f, 16f)
                    verticalLineTo(13f)
                    horizontalLineTo(19f)
                    verticalLineTo(11f)
                    horizontalLineTo(20f)
                    verticalLineTo(8f)
                    close()
                    moveTo(0f, 12f)
                    lineTo(4f, 8f)
                    verticalLineTo(11f)
                    horizontalLineTo(5f)
                    verticalLineTo(13f)
                    horizontalLineTo(4f)
                    verticalLineTo(16f)
                    lineTo(0f, 12f)
                    close()
                }
            }.build()
        return _Reorderhorizontally!!
    }

private var _Reorderhorizontally: ImageVector? = null
