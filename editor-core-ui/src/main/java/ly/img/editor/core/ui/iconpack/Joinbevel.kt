package ly.img.editor.core.ui.iconpack

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val IconPack.Joinbevel: ImageVector
    get() {
        if (_joinbevel != null) {
            return _joinbevel!!
        }
        _joinbevel =
            Builder(
                name = "Joinbevel",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f,
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF49454F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd,
                ) {
                    moveTo(3.0f, 11.3358f)
                    verticalLineTo(3.0f)
                    horizontalLineTo(6.0f)
                    verticalLineTo(11.1287f)
                    lineTo(12.8713f, 18.0f)
                    horizontalLineTo(21.0f)
                    verticalLineTo(21.0f)
                    horizontalLineTo(12.6642f)
                    curveTo(12.0012f, 21.0f, 11.3653f, 20.7366f, 10.8964f, 20.2678f)
                    lineTo(3.7322f, 13.1036f)
                    curveTo(3.2634f, 12.6347f, 3.0f, 11.9988f, 3.0f, 11.3358f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF49454F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd,
                ) {
                    moveTo(8.0f, 3.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(9.0429f)
                    lineTo(14.9571f, 15.0f)
                    horizontalLineTo(21.0f)
                    verticalLineTo(16.0f)
                    horizontalLineTo(14.5429f)
                    lineTo(8.0f, 9.4571f)
                    verticalLineTo(3.0f)
                    close()
                }
            }.build()
        return _joinbevel!!
    }

private var _joinbevel: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Joinbevel.IconPreview()
