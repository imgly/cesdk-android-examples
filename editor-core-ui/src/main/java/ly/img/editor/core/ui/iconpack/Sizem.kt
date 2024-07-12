package ly.img.editor.core.ui.iconpack

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val IconPack.Sizem: ImageVector
    get() {
        if (_sizem != null) {
            return _sizem!!
        }
        _sizem =
            Builder(
                name = "Sizem",
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
                    pathFillType = NonZero,
                ) {
                    moveTo(9.0f, 7.0f)
                    curveTo(7.9f, 7.0f, 7.0f, 7.9f, 7.0f, 9.0f)
                    verticalLineTo(17.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(9.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(16.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(9.0f)
                    horizontalLineTo(15.0f)
                    verticalLineTo(17.0f)
                    horizontalLineTo(17.0f)
                    verticalLineTo(9.0f)
                    curveTo(17.0f, 8.4696f, 16.7893f, 7.9609f, 16.4142f, 7.5858f)
                    curveTo(16.0391f, 7.2107f, 15.5304f, 7.0f, 15.0f, 7.0f)
                    horizontalLineTo(9.0f)
                    close()
                }
            }.build()
        return _sizem!!
    }

private var _sizem: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Sizem.IconPreview()
