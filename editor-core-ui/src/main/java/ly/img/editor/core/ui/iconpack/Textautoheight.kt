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

val IconPack.Textautoheight: ImageVector
    get() {
        if (_textautoheight != null) {
            return _textautoheight!!
        }
        _textautoheight =
            Builder(
                name = "Textautoheight",
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
                    moveTo(6.0f, 20.0f)
                    lineTo(2.0f, 16.0f)
                    lineTo(3.4f, 14.6f)
                    lineTo(5.0f, 16.15f)
                    verticalLineTo(7.85f)
                    lineTo(3.4f, 9.4f)
                    lineTo(2.0f, 8.0f)
                    lineTo(6.0f, 4.0f)
                    lineTo(10.0f, 8.0f)
                    lineTo(8.6f, 9.4f)
                    lineTo(7.0f, 7.85f)
                    verticalLineTo(16.15f)
                    lineTo(8.6f, 14.6f)
                    lineTo(10.0f, 16.0f)
                    lineTo(6.0f, 20.0f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF49454F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(12.0f, 15.0f)
                    verticalLineTo(13.0f)
                    horizontalLineTo(18.0f)
                    verticalLineTo(15.0f)
                    horizontalLineTo(12.0f)
                    close()
                    moveTo(12.0f, 11.0f)
                    verticalLineTo(9.0f)
                    horizontalLineTo(22.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(12.0f)
                    close()
                }
            }.build()
        return _textautoheight!!
    }

private var _textautoheight: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Textautoheight.IconPreview()
