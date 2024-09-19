package ly.img.editor.core.ui.iconpack

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val IconPack.Sizescircled: ImageVector
    get() {
        if (_sizescircled != null) {
            return _sizescircled!!
        }
        _sizescircled =
            Builder(
                name = "Sizescircled",
                defaultWidth = 24.0.dp,
                defaultHeight =
                    24.0.dp,
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
                    pathFillType = NonZero,
                ) {
                    moveTo(11.0f, 7.0f)
                    curveTo(9.9f, 7.0f, 9.0f, 7.9f, 9.0f, 9.0f)
                    verticalLineTo(11.0f)
                    curveTo(9.0f, 11.5304f, 9.2107f, 12.0391f, 9.5858f, 12.4142f)
                    curveTo(9.9609f, 12.7893f, 10.4696f, 13.0f, 11.0f, 13.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(15.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(17.0f)
                    horizontalLineTo(13.0f)
                    curveTo(14.11f, 17.0f, 15.0f, 16.11f, 15.0f, 15.0f)
                    verticalLineTo(13.0f)
                    curveTo(15.0f, 12.4696f, 14.7893f, 11.9609f, 14.4142f, 11.5858f)
                    curveTo(14.0391f, 11.2107f, 13.5304f, 11.0f, 13.0f, 11.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(9.0f)
                    horizontalLineTo(15.0f)
                    verticalLineTo(7.0f)
                    horizontalLineTo(11.0f)
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
                    moveTo(12.0f, 21.0f)
                    curveTo(16.9706f, 21.0f, 21.0f, 16.9706f, 21.0f, 12.0f)
                    curveTo(21.0f, 7.0294f, 16.9706f, 3.0f, 12.0f, 3.0f)
                    curveTo(7.0294f, 3.0f, 3.0f, 7.0294f, 3.0f, 12.0f)
                    curveTo(3.0f, 16.9706f, 7.0294f, 21.0f, 12.0f, 21.0f)
                    close()
                    moveTo(12.0f, 23.0f)
                    curveTo(18.0751f, 23.0f, 23.0f, 18.0751f, 23.0f, 12.0f)
                    curveTo(23.0f, 5.9249f, 18.0751f, 1.0f, 12.0f, 1.0f)
                    curveTo(5.9249f, 1.0f, 1.0f, 5.9249f, 1.0f, 12.0f)
                    curveTo(1.0f, 18.0751f, 5.9249f, 23.0f, 12.0f, 23.0f)
                    close()
                }
            }.build()
        return _sizescircled!!
    }

private var _sizescircled: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Sizescircled.IconPreview()
