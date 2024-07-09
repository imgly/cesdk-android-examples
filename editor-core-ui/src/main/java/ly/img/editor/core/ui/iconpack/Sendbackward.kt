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

val IconPack.Sendbackward: ImageVector
    get() {
        if (_sendbackward != null) {
            return _sendbackward!!
        }
        _sendbackward =
            Builder(
                name = "Sendbackward",
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
                    moveTo(4.5443f, 8.858f)
                    lineTo(3.0f, 8.0f)
                    lineTo(4.5443f, 7.142f)
                    lineTo(12.0f, 3.0f)
                    lineTo(14.2433f, 4.2463f)
                    curveTo(13.8877f, 4.6022f, 13.595f, 5.0209f, 13.383f, 5.4843f)
                    lineTo(12.0f, 4.7159f)
                    lineTo(6.0887f, 8.0f)
                    lineTo(12.0f, 11.2841f)
                    lineTo(13.2208f, 10.6058f)
                    curveTo(13.3832f, 11.0886f, 13.6299f, 11.5326f, 13.9435f, 11.9203f)
                    lineTo(12.0f, 13.0f)
                    lineTo(4.5443f, 8.858f)
                    close()
                    moveTo(8.4f, 13.0f)
                    lineTo(3.0f, 16.0f)
                    lineTo(12.0f, 21.0f)
                    lineTo(21.0f, 16.0f)
                    lineTo(15.6f, 13.0f)
                    lineTo(12.0f, 15.0f)
                    lineTo(8.4f, 13.0f)
                    close()
                    moveTo(19.0f, 12.0f)
                    lineTo(15.0f, 8.0f)
                    lineTo(16.4f, 6.6f)
                    lineTo(18.0f, 8.2f)
                    verticalLineTo(4.0f)
                    horizontalLineTo(20.0f)
                    verticalLineTo(8.2f)
                    lineTo(21.6f, 6.6f)
                    lineTo(23.0f, 8.0f)
                    lineTo(19.0f, 12.0f)
                    close()
                }
            }.build()
        return _sendbackward!!
    }

private var _sendbackward: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Sendbackward.IconPreview()
