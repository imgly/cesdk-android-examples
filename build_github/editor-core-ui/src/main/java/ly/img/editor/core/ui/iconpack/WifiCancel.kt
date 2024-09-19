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

val IconPack.WifiCancel: ImageVector
    get() {
        if (_wificancel != null) {
            return _wificancel!!
        }
        _wificancel =
            Builder(
                name = "Wificancel",
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
                    moveTo(12.0002f, 6.0f)
                    curveTo(8.6202f, 6.0f, 5.5002f, 7.12f, 3.0002f, 9.0f)
                    lineTo(1.2002f, 6.6f)
                    curveTo(4.2102f, 4.34f, 7.9502f, 3.0f, 12.0002f, 3.0f)
                    curveTo(16.0502f, 3.0f, 19.7902f, 4.34f, 22.8002f, 6.6f)
                    lineTo(21.0002f, 9.0f)
                    curveTo(18.5002f, 7.12f, 15.3802f, 6.0f, 12.0002f, 6.0f)
                    close()
                    moveTo(15.5302f, 12.72f)
                    curveTo(16.4202f, 12.26f, 17.4302f, 12.0f, 18.5002f, 12.0f)
                    horizontalLineTo(18.7402f)
                    lineTo(19.2002f, 11.4f)
                    curveTo(17.1902f, 9.89f, 14.7002f, 9.0f, 12.0002f, 9.0f)
                    curveTo(9.3002f, 9.0f, 6.8102f, 9.89f, 4.8002f, 11.4f)
                    lineTo(6.6002f, 13.8f)
                    curveTo(8.1002f, 12.67f, 9.9702f, 12.0f, 12.0002f, 12.0f)
                    curveTo(13.2602f, 12.0f, 14.4502f, 12.26f, 15.5302f, 12.72f)
                    close()
                    moveTo(12.0002f, 15.0f)
                    curveTo(10.6502f, 15.0f, 9.4002f, 15.45f, 8.4002f, 16.2f)
                    lineTo(12.0002f, 21.0f)
                    lineTo(12.3402f, 20.54f)
                    curveTo(12.1302f, 19.9f, 12.0002f, 19.22f, 12.0002f, 18.5f)
                    curveTo(12.0002f, 17.24f, 12.3602f, 16.08f, 13.0002f, 15.08f)
                    curveTo(12.6602f, 15.03f, 12.3302f, 15.0f, 12.0002f, 15.0f)
                    close()
                    moveTo(23.0002f, 18.5f)
                    curveTo(23.0002f, 21.0f, 21.0002f, 23.0f, 18.5002f, 23.0f)
                    curveTo(16.0002f, 23.0f, 14.0002f, 21.0f, 14.0002f, 18.5f)
                    curveTo(14.0002f, 16.0f, 16.0002f, 14.0f, 18.5002f, 14.0f)
                    curveTo(21.0002f, 14.0f, 23.0002f, 16.0f, 23.0002f, 18.5f)
                    close()
                    moveTo(20.0002f, 21.08f)
                    lineTo(15.9202f, 17.0f)
                    curveTo(15.6502f, 17.42f, 15.5002f, 17.94f, 15.5002f, 18.5f)
                    curveTo(15.5002f, 20.16f, 16.8402f, 21.5f, 18.5002f, 21.5f)
                    curveTo(19.0602f, 21.5f, 19.5802f, 21.35f, 20.0002f, 21.08f)
                    close()
                    moveTo(21.5002f, 18.5f)
                    curveTo(21.5002f, 16.84f, 20.1602f, 15.5f, 18.5002f, 15.5f)
                    curveTo(17.9402f, 15.5f, 17.4202f, 15.65f, 17.0002f, 15.92f)
                    lineTo(21.0802f, 20.0f)
                    curveTo(21.3502f, 19.58f, 21.5002f, 19.06f, 21.5002f, 18.5f)
                    close()
                }
            }.build()
        return _wificancel!!
    }

private var _wificancel: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.WifiCancel.IconPreview()
