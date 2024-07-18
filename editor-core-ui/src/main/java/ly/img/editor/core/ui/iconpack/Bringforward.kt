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

val IconPack.Bringforward: ImageVector
    get() {
        if (_bringforward != null) {
            return _bringforward!!
        }
        _bringforward =
            Builder(
                name = "Bringforward",
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
                    pathFillType = EvenOdd,
                ) {
                    moveTo(3.0f, 8.0f)
                    lineTo(12.0f, 3.0f)
                    lineTo(21.0f, 8.0f)
                    lineTo(12.0f, 13.0f)
                    lineTo(3.0f, 8.0f)
                    close()
                    moveTo(13.2208f, 18.6058f)
                    lineTo(12.0f, 19.2841f)
                    lineTo(6.0887f, 16.0f)
                    lineTo(9.9443f, 13.858f)
                    lineTo(8.4f, 13.0f)
                    lineTo(4.5443f, 15.142f)
                    lineTo(3.0f, 16.0f)
                    lineTo(4.5443f, 16.858f)
                    lineTo(12.0f, 21.0f)
                    lineTo(13.9435f, 19.9203f)
                    curveTo(13.6299f, 19.5326f, 13.3832f, 19.0886f, 13.2208f, 18.6058f)
                    close()
                    moveTo(18.0f, 20.0f)
                    verticalLineTo(15.8f)
                    lineTo(16.4f, 17.4f)
                    lineTo(15.0f, 16.0f)
                    lineTo(19.0f, 12.0f)
                    lineTo(23.0f, 16.0f)
                    lineTo(21.6f, 17.4f)
                    lineTo(20.0f, 15.8f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(18.0f)
                    close()
                }
            }.build()
        return _bringforward!!
    }

private var _bringforward: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Bringforward.IconPreview()
