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

val IconPack.Homeoutline: ImageVector
    get() {
        if (_homeoutline != null) {
            return _homeoutline!!
        }
        _homeoutline =
            Builder(
                name = "Homeoutline",
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
                    moveTo(4.0f, 21.0f)
                    verticalLineTo(9.0f)
                    lineTo(12.0f, 3.0f)
                    lineTo(20.0f, 9.0f)
                    verticalLineTo(21.0f)
                    horizontalLineTo(14.0f)
                    verticalLineTo(14.0f)
                    horizontalLineTo(10.0f)
                    verticalLineTo(21.0f)
                    horizontalLineTo(4.0f)
                    close()
                    moveTo(6.0f, 19.0f)
                    horizontalLineTo(8.0f)
                    verticalLineTo(12.0f)
                    horizontalLineTo(16.0f)
                    verticalLineTo(19.0f)
                    horizontalLineTo(18.0f)
                    verticalLineTo(10.0f)
                    lineTo(12.0f, 5.5f)
                    lineTo(6.0f, 10.0f)
                    verticalLineTo(19.0f)
                    close()
                }
            }.build()
        return _homeoutline!!
    }

private var _homeoutline: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Homeoutline.IconPreview()
