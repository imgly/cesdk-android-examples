package ly.img.editor.configuration.contentmoderation.iconPack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val IconPack.ShieldAlert: ImageVector
    get() {
        if (shieldAlert != null) {
            return shieldAlert!!
        }
        shieldAlert = Builder(
            name = "ShieldAlert",
            defaultWidth = 18.0.dp,
            defaultHeight =
                22.0.dp,
            viewportWidth = 18.0f,
            viewportHeight = 22.0f,
        ).apply {
            path(
                fill = SolidColor(Color(0xFF1A1A1A)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(8.915f, 21.5903f)
                curveTo(8.8154f, 21.5903f, 8.6992f, 21.571f, 8.5664f, 21.5322f)
                curveTo(8.4336f, 21.4935f, 8.3008f, 21.4382f, 8.168f, 21.3662f)
                curveTo(6.956f, 20.6855f, 5.8963f, 20.0685f, 4.9888f, 19.5151f)
                curveTo(4.0812f, 18.9618f, 3.3092f, 18.436f, 2.6728f, 17.938f)
                curveTo(2.0365f, 17.4399f, 1.519f, 16.9364f, 1.1206f, 16.4272f)
                curveTo(0.7277f, 15.9126f, 0.4427f, 15.3564f, 0.2656f, 14.7588f)
                curveTo(0.0885f, 14.1611f, 0.0f, 13.4832f, 0.0f, 12.7251f)
                verticalLineTo(4.6318f)
                curveTo(0.0f, 4.2113f, 0.0526f, 3.8709f, 0.1577f, 3.6108f)
                curveTo(0.2629f, 3.3507f, 0.4289f, 3.1349f, 0.6558f, 2.9634f)
                curveTo(0.8827f, 2.7918f, 1.1759f, 2.6341f, 1.5356f, 2.4902f)
                curveTo(1.7957f, 2.3851f, 2.1388f, 2.2523f, 2.5649f, 2.0918f)
                curveTo(2.9911f, 1.9258f, 3.4531f, 1.7515f, 3.9512f, 1.5689f)
                curveTo(4.4492f, 1.3807f, 4.9473f, 1.1981f, 5.4453f, 1.021f)
                curveTo(5.9489f, 0.8384f, 6.411f, 0.6724f, 6.8315f, 0.5229f)
                curveTo(7.2576f, 0.3735f, 7.6035f, 0.2601f, 7.8691f, 0.1826f)
                curveTo(8.0352f, 0.1383f, 8.2067f, 0.0968f, 8.3838f, 0.0581f)
                curveTo(8.5664f, 0.0194f, 8.7435f, 0.0f, 8.915f, 0.0f)
                curveTo(9.0866f, 0.0f, 9.2609f, 0.0166f, 9.438f, 0.0498f)
                curveTo(9.6206f, 0.083f, 9.7977f, 0.1273f, 9.9692f, 0.1826f)
                curveTo(10.2293f, 0.2712f, 10.5697f, 0.3901f, 10.9902f, 0.5396f)
                curveTo(11.4163f, 0.689f, 11.8784f, 0.855f, 12.3765f, 1.0376f)
                curveTo(12.8745f, 1.2147f, 13.3726f, 1.3945f, 13.8706f, 1.5771f)
                curveTo(14.3742f, 1.7542f, 14.839f, 1.923f, 15.2651f, 2.0835f)
                curveTo(15.6912f, 2.244f, 16.0343f, 2.3796f, 16.2944f, 2.4902f)
                curveTo(16.6541f, 2.6397f, 16.9474f, 2.8001f, 17.1743f, 2.9717f)
                curveTo(17.4012f, 3.1377f, 17.5672f, 3.3507f, 17.6724f, 3.6108f)
                curveTo(17.7775f, 3.8709f, 17.8301f, 4.2113f, 17.8301f, 4.6318f)
                verticalLineTo(12.7251f)
                curveTo(17.8301f, 13.4832f, 17.7388f, 14.1639f, 17.5562f, 14.7671f)
                curveTo(17.3791f, 15.3647f, 17.0968f, 15.9209f, 16.7095f, 16.4355f)
                curveTo(16.3221f, 16.9447f, 15.8102f, 17.4482f, 15.1738f, 17.9463f)
                curveTo(14.5374f, 18.4499f, 13.7627f, 18.9756f, 12.8496f, 19.5234f)
                curveTo(11.9421f, 20.0768f, 10.8768f, 20.6911f, 9.6538f, 21.3662f)
                curveTo(9.5265f, 21.4382f, 9.3965f, 21.4935f, 9.2637f, 21.5322f)
                curveTo(9.1309f, 21.571f, 9.0147f, 21.5903f, 8.915f, 21.5903f)
                close()
                moveTo(8.915f, 19.6396f)
                curveTo(8.998f, 19.6396f, 9.0838f, 19.6203f, 9.1724f, 19.5815f)
                curveTo(9.2664f, 19.5428f, 9.3827f, 19.4792f, 9.521f, 19.3906f)
                curveTo(10.495f, 18.793f, 11.3444f, 18.2673f, 12.0693f, 17.8135f)
                curveTo(12.7998f, 17.3597f, 13.4196f, 16.9364f, 13.9287f, 16.5435f)
                curveTo(14.4378f, 16.1561f, 14.8501f, 15.7632f, 15.1655f, 15.3647f)
                curveTo(15.481f, 14.9663f, 15.7106f, 14.5291f, 15.8545f, 14.0532f)
                curveTo(15.9984f, 13.5773f, 16.0703f, 13.0212f, 16.0703f, 12.3848f)
                verticalLineTo(4.9307f)
                curveTo(16.0703f, 4.6982f, 16.0399f, 4.5267f, 15.979f, 4.416f)
                curveTo(15.9237f, 4.3053f, 15.8075f, 4.2168f, 15.6304f, 4.1504f)
                curveTo(15.326f, 4.0397f, 14.9635f, 3.9097f, 14.543f, 3.7603f)
                curveTo(14.1279f, 3.6108f, 13.6825f, 3.4504f, 13.2065f, 3.2788f)
                curveTo(12.7362f, 3.1073f, 12.2686f, 2.9357f, 11.8037f, 2.7642f)
                curveTo(11.3444f, 2.5926f, 10.9128f, 2.4321f, 10.5088f, 2.2827f)
                curveTo(10.1048f, 2.1333f, 9.7645f, 2.0033f, 9.4878f, 1.8926f)
                curveTo(9.366f, 1.8538f, 9.2581f, 1.8234f, 9.1641f, 1.8013f)
                curveTo(9.07f, 1.7736f, 8.987f, 1.7598f, 8.915f, 1.7598f)
                curveTo(8.8376f, 1.7598f, 8.7518f, 1.7708f, 8.6577f, 1.793f)
                curveTo(8.5636f, 1.8151f, 8.4585f, 1.8483f, 8.3423f, 1.8926f)
                curveTo(8.0656f, 2.0033f, 7.7253f, 2.1333f, 7.3213f, 2.2827f)
                curveTo(6.9173f, 2.4266f, 6.4829f, 2.5815f, 6.0181f, 2.7476f)
                curveTo(5.5532f, 2.9136f, 5.0828f, 3.0824f, 4.6069f, 3.2539f)
                curveTo(4.1366f, 3.4255f, 3.6911f, 3.5887f, 3.2705f, 3.7436f)
                curveTo(2.8555f, 3.8931f, 2.5013f, 4.0286f, 2.208f, 4.1504f)
                curveTo(2.0309f, 4.2223f, 1.9119f, 4.3136f, 1.8511f, 4.4243f)
                curveTo(1.7902f, 4.5295f, 1.7598f, 4.6982f, 1.7598f, 4.9307f)
                verticalLineTo(12.3848f)
                curveTo(1.7598f, 13.0212f, 1.8289f, 13.5745f, 1.9673f, 14.0449f)
                curveTo(2.1112f, 14.5153f, 2.338f, 14.9497f, 2.6479f, 15.3481f)
                curveTo(2.9578f, 15.741f, 3.3674f, 16.1312f, 3.8765f, 16.5186f)
                curveTo(4.3856f, 16.9115f, 5.0054f, 17.3376f, 5.7358f, 17.7969f)
                curveTo(6.4663f, 18.2562f, 7.3241f, 18.7874f, 8.3091f, 19.3906f)
                curveTo(8.4474f, 19.4792f, 8.5609f, 19.5428f, 8.6494f, 19.5815f)
                curveTo(8.7435f, 19.6203f, 8.832f, 19.6396f, 8.915f, 19.6396f)
                close()
                moveTo(8.915f, 12.0444f)
                curveTo(8.3617f, 12.0444f, 8.0794f, 11.7622f, 8.0684f, 11.1978f)
                lineTo(7.9272f, 5.852f)
                curveTo(7.9217f, 5.5754f, 8.0103f, 5.3485f, 8.1929f, 5.1714f)
                curveTo(8.3755f, 4.9888f, 8.6134f, 4.8975f, 8.9067f, 4.8975f)
                curveTo(9.2f, 4.8975f, 9.438f, 4.9888f, 9.6206f, 5.1714f)
                curveTo(9.8088f, 5.354f, 9.8973f, 5.5837f, 9.8862f, 5.8604f)
                lineTo(9.7451f, 11.1895f)
                curveTo(9.7285f, 11.7594f, 9.4518f, 12.0444f, 8.915f, 12.0444f)
                close()
                moveTo(8.915f, 15.5059f)
                curveTo(8.5996f, 15.5059f, 8.3285f, 15.4007f, 8.1016f, 15.1904f)
                curveTo(7.8747f, 14.9746f, 7.7612f, 14.7145f, 7.7612f, 14.4102f)
                curveTo(7.7612f, 14.1003f, 7.8747f, 13.8402f, 8.1016f, 13.6299f)
                curveTo(8.3285f, 13.4196f, 8.5996f, 13.3145f, 8.915f, 13.3145f)
                curveTo(9.236f, 13.3145f, 9.5099f, 13.4196f, 9.7368f, 13.6299f)
                curveTo(9.9637f, 13.8346f, 10.0771f, 14.0947f, 10.0771f, 14.4102f)
                curveTo(10.0771f, 14.7201f, 9.9609f, 14.9801f, 9.7285f, 15.1904f)
                curveTo(9.5016f, 15.4007f, 9.2305f, 15.5059f, 8.915f, 15.5059f)
                close()
            }
        }
            .build()
        return shieldAlert!!
    }

private var shieldAlert: ImageVector? = null
