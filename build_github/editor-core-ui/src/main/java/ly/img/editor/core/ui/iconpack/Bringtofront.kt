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

val IconPack.Bringtofront: ImageVector
    get() {
        if (_bringtofront != null) {
            return _bringtofront!!
        }
        _bringtofront =
            Builder(
                name = "Bringtofront",
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
                    moveTo(12.0f, 2.0002f)
                    lineTo(3.0f, 7.0002f)
                    lineTo(12.0f, 12.0002f)
                    lineTo(21.0f, 7.0002f)
                    lineTo(12.0f, 2.0002f)
                    close()
                    moveTo(12.001f, 15.2841f)
                    lineTo(13.0005f, 14.7288f)
                    verticalLineTo(16.4447f)
                    lineTo(12.001f, 17.0f)
                    lineTo(4.5453f, 12.858f)
                    lineTo(3.001f, 12.0f)
                    lineTo(4.5453f, 11.142f)
                    lineTo(5.7005f, 10.5002f)
                    lineTo(7.2449f, 11.3582f)
                    lineTo(6.0897f, 12.0f)
                    lineTo(12.001f, 15.2841f)
                    close()
                    moveTo(12.0f, 20.2838f)
                    lineTo(13.7498f, 19.3117f)
                    curveTo(14.029f, 19.7468f, 14.3772f, 20.1333f, 14.779f, 20.4559f)
                    lineTo(12.0f, 21.9998f)
                    lineTo(4.5443f, 17.8577f)
                    lineTo(3.0f, 16.9998f)
                    lineTo(4.5443f, 16.1418f)
                    lineTo(5.6999f, 15.4998f)
                    lineTo(7.2443f, 16.3578f)
                    lineTo(6.0887f, 16.9998f)
                    lineTo(12.0f, 20.2838f)
                    close()
                    moveTo(18.0f, 14.8f)
                    verticalLineTo(19.0f)
                    horizontalLineTo(20.0f)
                    verticalLineTo(14.8f)
                    lineTo(21.6f, 16.4f)
                    lineTo(23.0f, 15.0f)
                    lineTo(19.0f, 11.0f)
                    lineTo(15.0f, 15.0f)
                    lineTo(16.4f, 16.4f)
                    lineTo(18.0f, 14.8f)
                    close()
                    moveTo(20.0f, 22.0f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(18.0f)
                    verticalLineTo(22.0f)
                    horizontalLineTo(20.0f)
                    close()
                }
            }.build()
        return _bringtofront!!
    }

private var _bringtofront: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Bringtofront.IconPreview()
