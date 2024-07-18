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

public val IconPack.Lowercasing: ImageVector
    get() {
        if (_lowercasing != null) {
            return _lowercasing!!
        }
        _lowercasing =
            Builder(
                name = "Lowercasing",
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
                    moveTo(8.6875f, 16.1962f)
                    verticalLineTo(11.913f)
                    curveTo(8.6875f, 11.592f, 8.6294f, 11.3153f, 8.5132f, 11.0829f)
                    curveTo(8.397f, 10.8505f, 8.2199f, 10.6706f, 7.9819f, 10.5433f)
                    curveTo(7.7495f, 10.4161f, 7.4562f, 10.3524f, 7.102f, 10.3524f)
                    curveTo(6.7755f, 10.3524f, 6.4933f, 10.4078f, 6.2554f, 10.5184f)
                    curveTo(6.0174f, 10.6291f, 5.832f, 10.7785f, 5.6992f, 10.9667f)
                    curveTo(5.5664f, 11.1548f, 5.5f, 11.3679f, 5.5f, 11.6058f)
                    horizontalLineTo(3.5078f)
                    curveTo(3.5078f, 11.2517f, 3.5936f, 10.9086f, 3.7651f, 10.5765f)
                    curveTo(3.9367f, 10.2445f, 4.1857f, 9.9485f, 4.5122f, 9.6883f)
                    curveTo(4.8387f, 9.4283f, 5.2288f, 9.2235f, 5.6826f, 9.0741f)
                    curveTo(6.1364f, 8.9247f, 6.6455f, 8.85f, 7.21f, 8.85f)
                    curveTo(7.8851f, 8.85f, 8.4827f, 8.9634f, 9.0029f, 9.1903f)
                    curveTo(9.5287f, 9.4172f, 9.9409f, 9.7603f, 10.2397f, 10.2196f)
                    curveTo(10.5441f, 10.6734f, 10.6963f, 11.2434f, 10.6963f, 11.9296f)
                    verticalLineTo(15.9222f)
                    curveTo(10.6963f, 16.3317f, 10.724f, 16.6997f, 10.7793f, 17.0262f)
                    curveTo(10.8402f, 17.3472f, 10.9259f, 17.6267f, 11.0366f, 17.8646f)
                    verticalLineTo(17.9974f)
                    horizontalLineTo(8.9863f)
                    curveTo(8.8923f, 17.7816f, 8.8175f, 17.5077f, 8.7622f, 17.1757f)
                    curveTo(8.7124f, 16.8381f, 8.6875f, 16.5116f, 8.6875f, 16.1962f)
                    close()
                    moveTo(8.978f, 12.5355f)
                    lineTo(8.9946f, 13.7723f)
                    horizontalLineTo(7.5586f)
                    curveTo(7.1878f, 13.7723f, 6.8613f, 13.8083f, 6.5791f, 13.8802f)
                    curveTo(6.2969f, 13.9467f, 6.0617f, 14.0463f, 5.8735f, 14.1791f)
                    curveTo(5.6854f, 14.3119f, 5.5443f, 14.4724f, 5.4502f, 14.6605f)
                    curveTo(5.3561f, 14.8487f, 5.3091f, 15.0617f, 5.3091f, 15.2997f)
                    curveTo(5.3091f, 15.5376f, 5.3644f, 15.7562f, 5.4751f, 15.9554f)
                    curveTo(5.5858f, 16.1491f, 5.7463f, 16.3013f, 5.9565f, 16.412f)
                    curveTo(6.1724f, 16.5227f, 6.4324f, 16.578f, 6.7368f, 16.578f)
                    curveTo(7.1463f, 16.578f, 7.5033f, 16.495f, 7.8076f, 16.329f)
                    curveTo(8.1175f, 16.1574f, 8.361f, 15.9499f, 8.5381f, 15.7064f)
                    curveTo(8.7152f, 15.4574f, 8.8092f, 15.2222f, 8.8203f, 15.0009f)
                    lineTo(9.4678f, 15.889f)
                    curveTo(9.4014f, 16.1159f, 9.2879f, 16.3594f, 9.1274f, 16.6195f)
                    curveTo(8.967f, 16.8796f, 8.7567f, 17.1286f, 8.4966f, 17.3666f)
                    curveTo(8.242f, 17.599f, 7.9349f, 17.7899f, 7.5752f, 17.9393f)
                    curveTo(7.221f, 18.0887f, 6.8115f, 18.1635f, 6.3467f, 18.1635f)
                    curveTo(5.7601f, 18.1635f, 5.2371f, 18.0472f, 4.7778f, 17.8148f)
                    curveTo(4.3185f, 17.5769f, 3.9588f, 17.2587f, 3.6987f, 16.8602f)
                    curveTo(3.4386f, 16.4563f, 3.3086f, 15.9997f, 3.3086f, 15.4906f)
                    curveTo(3.3086f, 15.0147f, 3.3971f, 14.5941f, 3.5742f, 14.2289f)
                    curveTo(3.7568f, 13.8581f, 4.0225f, 13.5482f, 4.3711f, 13.2992f)
                    curveTo(4.7253f, 13.0502f, 5.1569f, 12.862f, 5.666f, 12.7347f)
                    curveTo(6.1751f, 12.6019f, 6.7562f, 12.5355f, 7.4092f, 12.5355f)
                    horizontalLineTo(8.978f)
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
                    moveTo(18.5405f, 9.016f)
                    horizontalLineTo(20.3584f)
                    verticalLineTo(17.7484f)
                    curveTo(20.3584f, 18.5564f, 20.1868f, 19.2426f, 19.8438f, 19.807f)
                    curveTo(19.5007f, 20.3715f, 19.022f, 20.8003f, 18.4077f, 21.0936f)
                    curveTo(17.7935f, 21.3925f, 17.0824f, 21.5419f, 16.2744f, 21.5419f)
                    curveTo(15.9313f, 21.5419f, 15.5495f, 21.4921f, 15.1289f, 21.3925f)
                    curveTo(14.7139f, 21.2928f, 14.3099f, 21.1324f, 13.917f, 20.911f)
                    curveTo(13.5296f, 20.6952f, 13.2059f, 20.4102f, 12.9458f, 20.056f)
                    lineTo(13.8838f, 18.8773f)
                    curveTo(14.2048f, 19.2592f, 14.5589f, 19.5386f, 14.9463f, 19.7157f)
                    curveTo(15.3337f, 19.8928f, 15.7404f, 19.9813f, 16.1665f, 19.9813f)
                    curveTo(16.6258f, 19.9813f, 17.016f, 19.8955f, 17.3369f, 19.724f)
                    curveTo(17.6634f, 19.558f, 17.9152f, 19.3117f, 18.0923f, 18.9852f)
                    curveTo(18.2694f, 18.6587f, 18.3579f, 18.2603f, 18.3579f, 17.7899f)
                    verticalLineTo(11.0497f)
                    lineTo(18.5405f, 9.016f)
                    close()
                    moveTo(12.4395f, 13.6063f)
                    verticalLineTo(13.432f)
                    curveTo(12.4395f, 12.7513f, 12.5225f, 12.1316f, 12.6885f, 11.5726f)
                    curveTo(12.8545f, 11.0082f, 13.0924f, 10.524f, 13.4023f, 10.12f)
                    curveTo(13.7122f, 9.7105f, 14.0885f, 9.3978f, 14.5312f, 9.182f)
                    curveTo(14.974f, 8.9607f, 15.4748f, 8.85f, 16.0337f, 8.85f)
                    curveTo(16.6147f, 8.85f, 17.11f, 8.9551f, 17.5195f, 9.1654f)
                    curveTo(17.9346f, 9.3757f, 18.2804f, 9.6773f, 18.5571f, 10.0702f)
                    curveTo(18.8338f, 10.4576f, 19.0496f, 10.9224f, 19.2046f, 11.4647f)
                    curveTo(19.3651f, 12.0015f, 19.484f, 12.5992f, 19.5615f, 13.2577f)
                    verticalLineTo(13.8138f)
                    curveTo(19.4896f, 14.4558f, 19.3678f, 15.0424f, 19.1963f, 15.5736f)
                    curveTo(19.0247f, 16.1049f, 18.7979f, 16.5642f, 18.5156f, 16.9515f)
                    curveTo(18.2334f, 17.3389f, 17.8848f, 17.6377f, 17.4697f, 17.848f)
                    curveTo(17.0602f, 18.0583f, 16.576f, 18.1635f, 16.0171f, 18.1635f)
                    curveTo(15.4692f, 18.1635f, 14.974f, 18.05f, 14.5312f, 17.8231f)
                    curveTo(14.0941f, 17.5962f, 13.7178f, 17.278f, 13.4023f, 16.8685f)
                    curveTo(13.0924f, 16.459f, 12.8545f, 15.9776f, 12.6885f, 15.4242f)
                    curveTo(12.5225f, 14.8653f, 12.4395f, 14.2593f, 12.4395f, 13.6063f)
                    close()
                    moveTo(14.4399f, 13.432f)
                    verticalLineTo(13.6063f)
                    curveTo(14.4399f, 14.0158f, 14.4787f, 14.3977f, 14.5562f, 14.7518f)
                    curveTo(14.6392f, 15.106f, 14.7637f, 15.4187f, 14.9297f, 15.6898f)
                    curveTo(15.1012f, 15.9554f, 15.3171f, 16.1657f, 15.5771f, 16.3207f)
                    curveTo(15.8428f, 16.4701f, 16.1554f, 16.5448f, 16.5151f, 16.5448f)
                    curveTo(16.9855f, 16.5448f, 17.3701f, 16.4452f, 17.6689f, 16.246f)
                    curveTo(17.9733f, 16.0468f, 18.2057f, 15.7784f, 18.3662f, 15.4408f)
                    curveTo(18.5322f, 15.0977f, 18.6484f, 14.7159f, 18.7148f, 14.2953f)
                    verticalLineTo(12.7928f)
                    curveTo(18.6816f, 12.4663f, 18.6125f, 12.162f, 18.5073f, 11.8798f)
                    curveTo(18.4077f, 11.5975f, 18.2721f, 11.3513f, 18.1006f, 11.141f)
                    curveTo(17.929f, 10.9252f, 17.7132f, 10.7592f, 17.4531f, 10.6429f)
                    curveTo(17.193f, 10.5212f, 16.8859f, 10.4603f, 16.5317f, 10.4603f)
                    curveTo(16.172f, 10.4603f, 15.8594f, 10.5378f, 15.5938f, 10.6927f)
                    curveTo(15.3281f, 10.8477f, 15.1095f, 11.0608f, 14.938f, 11.3319f)
                    curveTo(14.772f, 11.6031f, 14.6475f, 11.9185f, 14.5645f, 12.2782f)
                    curveTo(14.4814f, 12.6379f, 14.4399f, 13.0225f, 14.4399f, 13.432f)
                    close()
                }
            }.build()
        return _lowercasing!!
    }

private var _lowercasing: ImageVector? = null

@Composable
private fun Preview() = IconPack.Lowercasing.IconPreview()
