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

public val IconPack.AutoSize: ImageVector
    get() {
        if (_autosize != null) {
            return _autosize!!
        }
        _autosize =
            Builder(
                name = "Trailing element",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f,
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF2A4BE6)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(17.15f, 5.0f)
                    lineTo(15.6f, 3.4f)
                    lineTo(17.0f, 2.0f)
                    lineTo(21.0f, 6.0f)
                    lineTo(17.0f, 10.0f)
                    lineTo(15.6f, 8.6f)
                    lineTo(17.15f, 7.0f)
                    horizontalLineTo(7.0f)
                    verticalLineTo(17.15f)
                    lineTo(8.6f, 15.6f)
                    lineTo(10.0f, 17.0f)
                    lineTo(6.0f, 21.0f)
                    lineTo(2.0f, 17.0f)
                    lineTo(3.4f, 15.6f)
                    lineTo(5.0f, 17.15f)
                    lineTo(5.0f, 5.0f)
                    lineTo(17.15f, 5.0f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF2A4BE6)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(12.0f, 16.0f)
                    horizontalLineTo(18.0f)
                    verticalLineTo(18.0f)
                    horizontalLineTo(12.0f)
                    verticalLineTo(16.0f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF2A4BE6)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(12.0f, 14.0f)
                    verticalLineTo(12.0f)
                    horizontalLineTo(22.0f)
                    verticalLineTo(14.0f)
                    horizontalLineTo(12.0f)
                    close()
                }
            }.build()
        return _autosize!!
    }

private var _autosize: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.AutoSize.IconPreview()
