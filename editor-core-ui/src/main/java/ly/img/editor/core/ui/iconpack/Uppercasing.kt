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
import androidx.compose.ui.unit.dp

public val IconPack.Uppercasing: ImageVector
    get() {
        if (_uppercasing != null) {
            return _uppercasing!!
        }
        _uppercasing =
            Builder(
                name = "Uppercasing",
                defaultWidth = 24.0.dp,
                defaultHeight =
                    25.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 25.0f,
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(7.4111f, 7.5264f)
                    lineTo(3.8003f, 18.002f)
                    horizontalLineTo(1.6172f)
                    lineTo(6.166f, 5.916f)
                    horizontalLineTo(7.5606f)
                    lineTo(7.4111f, 7.5264f)
                    close()
                    moveTo(10.4326f, 18.002f)
                    lineTo(6.8135f, 7.5264f)
                    lineTo(6.6558f, 5.916f)
                    horizontalLineTo(8.0586f)
                    lineTo(12.624f, 18.002f)
                    horizontalLineTo(10.4326f)
                    close()
                    moveTo(10.2583f, 13.5195f)
                    verticalLineTo(15.1714f)
                    horizontalLineTo(3.6841f)
                    verticalLineTo(13.5195f)
                    horizontalLineTo(10.2583f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(22.4445f, 11.8262f)
                    verticalLineTo(16.4414f)
                    curveTo(22.273f, 16.6683f, 22.0046f, 16.9173f, 21.6393f, 17.1885f)
                    curveTo(21.2796f, 17.4541f, 20.801f, 17.6838f, 20.2033f, 17.8774f)
                    curveTo(19.6056f, 18.0711f, 18.8613f, 18.168f, 17.9704f, 18.168f)
                    curveTo(17.2123f, 18.168f, 16.5178f, 18.0407f, 15.8869f, 17.7861f)
                    curveTo(15.256f, 17.526f, 14.7109f, 17.147f, 14.2516f, 16.6489f)
                    curveTo(13.7979f, 16.1509f, 13.4465f, 15.5449f, 13.1974f, 14.8311f)
                    curveTo(12.9484f, 14.1117f, 12.8239f, 13.2926f, 12.8239f, 12.374f)
                    verticalLineTo(11.5356f)
                    curveTo(12.8239f, 10.6226f, 12.9374f, 9.8091f, 13.1642f, 9.0952f)
                    curveTo(13.3967f, 8.3758f, 13.7287f, 7.7671f, 14.1603f, 7.269f)
                    curveTo(14.592f, 6.771f, 15.1122f, 6.3947f, 15.7209f, 6.1401f)
                    curveTo(16.3351f, 5.8801f, 17.0296f, 5.75f, 17.8044f, 5.75f)
                    curveTo(18.7949f, 5.75f, 19.6139f, 5.916f, 20.2614f, 6.2481f)
                    curveTo(20.9144f, 6.5745f, 21.418f, 7.0283f, 21.7721f, 7.6094f)
                    curveTo(22.1263f, 8.1904f, 22.3504f, 8.8545f, 22.4445f, 9.6016f)
                    horizontalLineTo(20.4025f)
                    curveTo(20.3361f, 9.181f, 20.2061f, 8.8047f, 20.0124f, 8.4727f)
                    curveTo(19.8242f, 8.1406f, 19.5531f, 7.8805f, 19.1989f, 7.6924f)
                    curveTo(18.8503f, 7.4987f, 18.3965f, 7.4019f, 17.8376f, 7.4019f)
                    curveTo(17.3561f, 7.4019f, 16.9328f, 7.4932f, 16.5676f, 7.6758f)
                    curveTo(16.2023f, 7.8584f, 15.898f, 8.1268f, 15.6545f, 8.481f)
                    curveTo(15.4165f, 8.8351f, 15.2367f, 9.2668f, 15.1149f, 9.7759f)
                    curveTo(14.9932f, 10.285f, 14.9323f, 10.866f, 14.9323f, 11.519f)
                    verticalLineTo(12.374f)
                    curveTo(14.9323f, 13.0381f, 15.0015f, 13.6274f, 15.1398f, 14.1421f)
                    curveTo(15.2837f, 14.6567f, 15.4885f, 15.0911f, 15.7541f, 15.4453f)
                    curveTo(16.0252f, 15.7995f, 16.3545f, 16.0679f, 16.7419f, 16.2505f)
                    curveTo(17.1292f, 16.4276f, 17.5664f, 16.5161f, 18.0534f, 16.5161f)
                    curveTo(18.5293f, 16.5161f, 18.9194f, 16.4774f, 19.2238f, 16.3999f)
                    curveTo(19.5282f, 16.3169f, 19.7689f, 16.2201f, 19.946f, 16.1094f)
                    curveTo(20.1286f, 15.9932f, 20.2697f, 15.8825f, 20.3693f, 15.7773f)
                    verticalLineTo(13.3784f)
                    horizontalLineTo(17.8542f)
                    verticalLineTo(11.8262f)
                    horizontalLineTo(22.4445f)
                    close()
                }
            }.build()
        return _uppercasing!!
    }

private var _uppercasing: ImageVector? = null

@Composable
private fun Preview() = IconPack.Uppercasing.IconPreview()
