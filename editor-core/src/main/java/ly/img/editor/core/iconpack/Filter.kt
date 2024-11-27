package ly.img.editor.core.iconpack

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

val IconPack.Filter: ImageVector
    get() {
        if (_filter != null) {
            return _filter!!
        }
        _filter =
            Builder(
                name = "Filter",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f,
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd,
                ) {
                    moveTo(18.9997f, 8.9392f)
                    curveTo(18.967f, 5.1071f, 15.8397f, 2.0f, 12.0f, 2.0f)
                    curveTo(8.1603f, 2.0f, 5.033f, 5.1071f, 5.0003f, 8.9392f)
                    curveTo(2.9093f, 10.1512f, 1.5f, 12.414f, 1.5f, 15.0f)
                    curveTo(1.5f, 18.86f, 4.64f, 22.0f, 8.5f, 22.0f)
                    curveTo(9.7741f, 22.0f, 10.9698f, 21.6579f, 12.0f, 21.0607f)
                    curveTo(13.0302f, 21.6579f, 14.2259f, 22.0f, 15.5f, 22.0f)
                    curveTo(19.36f, 22.0f, 22.5f, 18.86f, 22.5f, 15.0f)
                    curveTo(22.5f, 12.414f, 21.0907f, 10.1512f, 18.9997f, 8.9392f)
                    close()
                    moveTo(12.0f, 14.0f)
                    curveTo(12.4702f, 14.0f, 12.9255f, 13.9346f, 13.3573f, 13.8125f)
                    curveTo(13.1327f, 12.8949f, 12.6538f, 12.0755f, 12.0f, 11.4337f)
                    curveTo(11.3462f, 12.0755f, 10.8673f, 12.8949f, 10.6427f, 13.8125f)
                    curveTo(11.0745f, 13.9346f, 11.5298f, 14.0f, 12.0f, 14.0f)
                    close()
                    moveTo(8.8328f, 12.8652f)
                    curveTo(9.1371f, 11.9168f, 9.6385f, 11.056f, 10.2887f, 10.3311f)
                    curveTo(9.7329f, 10.1173f, 9.1298f, 10.0f, 8.5f, 10.0f)
                    curveTo(8.0298f, 10.0f, 7.5745f, 10.0654f, 7.1427f, 10.1875f)
                    curveTo(7.4039f, 11.2545f, 8.0088f, 12.1886f, 8.8328f, 12.8652f)
                    close()
                    moveTo(12.0f, 8.9393f)
                    curveTo(10.9698f, 8.3421f, 9.7741f, 8.0f, 8.5f, 8.0f)
                    curveTo(8.0112f, 8.0f, 7.5339f, 8.0504f, 7.0731f, 8.1462f)
                    curveTo(7.4798f, 5.7955f, 9.5356f, 4.0f, 12.0f, 4.0f)
                    curveTo(14.4644f, 4.0f, 16.5202f, 5.7955f, 16.9269f, 8.1462f)
                    curveTo(16.4661f, 8.0504f, 15.9888f, 8.0f, 15.5f, 8.0f)
                    curveTo(14.2259f, 8.0f, 13.0302f, 8.3421f, 12.0f, 8.9393f)
                    close()
                    moveTo(16.8573f, 10.1875f)
                    curveTo(16.4255f, 10.0654f, 15.9702f, 10.0f, 15.5f, 10.0f)
                    curveTo(14.8702f, 10.0f, 14.2671f, 10.1173f, 13.7113f, 10.3311f)
                    curveTo(14.3615f, 11.056f, 14.8629f, 11.9168f, 15.1672f, 12.8652f)
                    curveTo(15.9912f, 12.1886f, 16.5961f, 11.2545f, 16.8573f, 10.1875f)
                    close()
                    moveTo(18.6672f, 11.1348f)
                    curveTo(19.7852f, 12.0529f, 20.5f, 13.4451f, 20.5f, 15.0f)
                    curveTo(20.5f, 17.7554f, 18.2554f, 20.0f, 15.5f, 20.0f)
                    curveTo(14.8702f, 20.0f, 14.2671f, 19.8827f, 13.7114f, 19.6689f)
                    curveTo(14.8104f, 18.4434f, 15.4846f, 16.8293f, 15.4997f, 15.0608f)
                    curveTo(16.989f, 14.1976f, 18.1325f, 12.8013f, 18.6672f, 11.1348f)
                    close()
                    moveTo(12.0f, 18.5663f)
                    curveTo(12.7321f, 17.8477f, 13.2448f, 16.9064f, 13.4269f, 15.8538f)
                    curveTo(12.9661f, 15.9496f, 12.4888f, 16.0f, 12.0f, 16.0f)
                    curveTo(11.5112f, 16.0f, 11.0339f, 15.9496f, 10.5731f, 15.8538f)
                    curveTo(10.7552f, 16.9064f, 11.268f, 17.8477f, 12.0f, 18.5663f)
                    close()
                    moveTo(10.2887f, 19.6689f)
                    curveTo(9.1896f, 18.4434f, 8.5154f, 16.8293f, 8.5003f, 15.0608f)
                    curveTo(7.011f, 14.1976f, 5.8675f, 12.8013f, 5.3328f, 11.1348f)
                    curveTo(4.2148f, 12.0528f, 3.5f, 13.4451f, 3.5f, 15.0f)
                    curveTo(3.5f, 17.7554f, 5.7446f, 20.0f, 8.5f, 20.0f)
                    curveTo(9.1298f, 20.0f, 9.7329f, 19.8827f, 10.2887f, 19.6689f)
                    close()
                }
            }.build()
        return _filter!!
    }

private var _filter: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Filter.IconPreview()
