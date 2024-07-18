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

val IconPack.Sendtoback: ImageVector
    get() {
        if (_sendtoback != null) {
            return _sendtoback!!
        }
        _sendtoback =
            Builder(
                name = "Sendtoback",
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
                    moveTo(4.5443f, 7.858f)
                    lineTo(3.0f, 7.0f)
                    lineTo(4.5443f, 6.142f)
                    lineTo(12.0f, 2.0f)
                    lineTo(14.4319f, 3.3511f)
                    curveTo(14.0706f, 3.7039f, 13.7653f, 4.1138f, 13.5306f, 4.5663f)
                    lineTo(12.0f, 3.7159f)
                    lineTo(6.0887f, 7.0f)
                    lineTo(12.0f, 10.2841f)
                    lineTo(13.0f, 9.7285f)
                    verticalLineTo(9.75f)
                    curveTo(13.0f, 10.293f, 13.0911f, 10.8147f, 13.2589f, 11.3006f)
                    lineTo(12.0f, 12.0f)
                    lineTo(4.5443f, 7.858f)
                    close()
                    moveTo(16.7068f, 14.3851f)
                    curveTo(16.0806f, 14.2448f, 15.501f, 13.9807f, 14.9952f, 13.62f)
                    lineTo(12.0f, 15.2841f)
                    lineTo(6.0887f, 12.0f)
                    lineTo(7.2443f, 11.358f)
                    lineTo(5.7f, 10.5f)
                    lineTo(4.5443f, 11.142f)
                    lineTo(3.0f, 12.0f)
                    lineTo(4.5443f, 12.858f)
                    lineTo(12.0f, 17.0f)
                    lineTo(16.7068f, 14.3851f)
                    close()
                    moveTo(3.0f, 17.0002f)
                    lineTo(5.7f, 15.5002f)
                    lineTo(12.0f, 19.0002f)
                    lineTo(18.3f, 15.5002f)
                    lineTo(21.0f, 17.0002f)
                    lineTo(12.0f, 22.0002f)
                    lineTo(3.0f, 17.0002f)
                    close()
                    moveTo(15.0f, 9.0f)
                    lineTo(19.0f, 13.0f)
                    lineTo(23.0f, 9.0f)
                    lineTo(21.6f, 7.6f)
                    lineTo(20.0f, 9.2f)
                    verticalLineTo(5.0f)
                    horizontalLineTo(18.0f)
                    verticalLineTo(9.2f)
                    lineTo(16.4f, 7.6f)
                    lineTo(15.0f, 9.0f)
                    close()
                    moveTo(20.0f, 2.0f)
                    horizontalLineTo(18.0f)
                    verticalLineTo(4.0f)
                    horizontalLineTo(20.0f)
                    verticalLineTo(2.0f)
                    close()
                }
            }.build()
        return _sendtoback!!
    }

private var _sendtoback: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Sendtoback.IconPreview()
