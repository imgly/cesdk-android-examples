package ly.img.editor.core.iconpack

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

val IconPack.Replace: ImageVector
    get() {
        if (_replace != null) {
            return _replace!!
        }
        _replace =
            Builder(
                name = "Replace",
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
                    moveTo(10.5f, 7.0f)
                    horizontalLineTo(13.0f)
                    horizontalLineTo(14.2f)
                    lineTo(12.6f, 8.6f)
                    lineTo(14.0f, 10.0f)
                    lineTo(18.0f, 6.0f)
                    lineTo(14.0f, 2.0f)
                    lineTo(12.6f, 3.4f)
                    lineTo(14.2f, 5.0f)
                    horizontalLineTo(13.0f)
                    horizontalLineTo(10.5f)
                    curveTo(8.7761f, 5.0f, 7.1228f, 5.6848f, 5.9038f, 6.9038f)
                    curveTo(4.6848f, 8.1228f, 4.0f, 9.7761f, 4.0f, 11.5f)
                    verticalLineTo(14.0f)
                    horizontalLineTo(6.0f)
                    verticalLineTo(11.5f)
                    curveTo(6.0f, 9.0f, 8.0f, 7.0f, 10.5f, 7.0f)
                    close()
                    moveTo(13.5f, 17.0f)
                    horizontalLineTo(11.0f)
                    horizontalLineTo(9.8f)
                    lineTo(11.4f, 15.4f)
                    lineTo(10.0f, 14.0f)
                    lineTo(6.0f, 18.0f)
                    lineTo(10.0f, 22.0f)
                    lineTo(11.4f, 20.6f)
                    lineTo(9.8f, 19.0f)
                    horizontalLineTo(11.0f)
                    horizontalLineTo(13.5f)
                    curveTo(15.2239f, 19.0f, 16.8772f, 18.3152f, 18.0962f, 17.0962f)
                    curveTo(19.3152f, 15.8772f, 20.0f, 14.2239f, 20.0f, 12.5f)
                    verticalLineTo(10.0f)
                    horizontalLineTo(18.0f)
                    verticalLineTo(12.5f)
                    curveTo(18.0f, 15.0f, 16.0f, 17.0f, 13.5f, 17.0f)
                    close()
                }
            }.build()
        return _replace!!
    }

private var _replace: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Replace.IconPreview()
