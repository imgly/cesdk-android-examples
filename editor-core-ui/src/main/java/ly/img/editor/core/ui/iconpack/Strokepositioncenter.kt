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

val IconPack.Strokepositioncenter: ImageVector
    get() {
        if (_strokepositioncenter != null) {
            return _strokepositioncenter!!
        }
        _strokepositioncenter =
            Builder(
                name = "Strokepositioncenter",
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
                    moveTo(3.0f, 4.0f)
                    curveTo(3.0f, 3.4477f, 3.4477f, 3.0f, 4.0f, 3.0f)
                    horizontalLineTo(6.0f)
                    curveTo(6.5523f, 3.0f, 7.0f, 3.4477f, 7.0f, 4.0f)
                    horizontalLineTo(17.0f)
                    curveTo(17.0f, 3.4477f, 17.4477f, 3.0f, 18.0f, 3.0f)
                    horizontalLineTo(20.0f)
                    curveTo(20.5523f, 3.0f, 21.0f, 3.4477f, 21.0f, 4.0f)
                    verticalLineTo(6.0f)
                    curveTo(21.0f, 6.5523f, 20.5523f, 7.0f, 20.0f, 7.0f)
                    verticalLineTo(17.0f)
                    curveTo(20.5523f, 17.0f, 21.0f, 17.4477f, 21.0f, 18.0f)
                    verticalLineTo(20.0f)
                    curveTo(21.0f, 20.5523f, 20.5523f, 21.0f, 20.0f, 21.0f)
                    horizontalLineTo(18.0f)
                    curveTo(17.4477f, 21.0f, 17.0f, 20.5523f, 17.0f, 20.0f)
                    horizontalLineTo(7.0f)
                    curveTo(7.0f, 20.5523f, 6.5523f, 21.0f, 6.0f, 21.0f)
                    horizontalLineTo(4.0f)
                    curveTo(3.4477f, 21.0f, 3.0f, 20.5523f, 3.0f, 20.0f)
                    verticalLineTo(18.0f)
                    curveTo(3.0f, 17.4477f, 3.4477f, 17.0f, 4.0f, 17.0f)
                    verticalLineTo(7.0f)
                    curveTo(3.4477f, 7.0f, 3.0f, 6.5523f, 3.0f, 6.0f)
                    verticalLineTo(4.0f)
                    close()
                    moveTo(6.0f, 7.0f)
                    verticalLineTo(17.0f)
                    curveTo(6.5523f, 17.0f, 7.0f, 17.4477f, 7.0f, 18.0f)
                    horizontalLineTo(17.0f)
                    curveTo(17.0f, 17.4477f, 17.4477f, 17.0f, 18.0f, 17.0f)
                    verticalLineTo(7.0f)
                    curveTo(17.4477f, 7.0f, 17.0f, 6.5523f, 17.0f, 6.0f)
                    horizontalLineTo(7.0f)
                    curveTo(7.0f, 6.5523f, 6.5523f, 7.0f, 6.0f, 7.0f)
                    close()
                }
            }.build()
        return _strokepositioncenter!!
    }

private var _strokepositioncenter: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Strokepositioncenter.IconPreview()
