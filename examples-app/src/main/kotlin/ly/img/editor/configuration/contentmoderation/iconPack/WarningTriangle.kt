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

public val IconPack.WarningTriangle: ImageVector
    get() {
        if (warningTriangle != null) {
            return warningTriangle!!
        }
        warningTriangle = Builder(
            name = "WarningTriangle",
            defaultWidth = 23.0.dp,
            defaultHeight =
                21.0.dp,
            viewportWidth = 23.0f,
            viewportHeight = 21.0f,
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
                moveTo(2.9053f, 20.4614f)
                curveTo(2.3076f, 20.4614f, 1.7902f, 20.3314f, 1.353f, 20.0713f)
                curveTo(0.9214f, 19.8167f, 0.5866f, 19.4709f, 0.3486f, 19.0337f)
                curveTo(0.1162f, 18.5965f, 0.0f, 18.1123f, 0.0f, 17.5811f)
                curveTo(0.0f, 17.3265f, 0.0332f, 17.0692f, 0.0996f, 16.8091f)
                curveTo(0.1715f, 16.549f, 0.2795f, 16.2972f, 0.4233f, 16.0537f)
                lineTo(8.7822f, 1.4443f)
                curveTo(9.0534f, 0.9629f, 9.4131f, 0.6032f, 9.8613f, 0.3652f)
                curveTo(10.3096f, 0.1217f, 10.7772f, 0.0f, 11.2642f, 0.0f)
                curveTo(11.7456f, 0.0f, 12.2077f, 0.1217f, 12.6504f, 0.3652f)
                curveTo(13.0986f, 0.6032f, 13.4639f, 0.9629f, 13.7461f, 1.4443f)
                lineTo(22.105f, 16.0537f)
                curveTo(22.2433f, 16.2972f, 22.3457f, 16.549f, 22.4121f, 16.8091f)
                curveTo(22.484f, 17.0692f, 22.52f, 17.3265f, 22.52f, 17.5811f)
                curveTo(22.52f, 18.1123f, 22.401f, 18.5965f, 22.1631f, 19.0337f)
                curveTo(21.9307f, 19.4709f, 21.5986f, 19.8167f, 21.167f, 20.0713f)
                curveTo(20.7354f, 20.3314f, 20.2179f, 20.4614f, 19.6147f, 20.4614f)
                horizontalLineTo(2.9053f)
                close()
                moveTo(11.2642f, 13.1982f)
                curveTo(11.8231f, 13.1982f, 12.1136f, 12.8994f, 12.1357f, 12.3018f)
                lineTo(12.2769f, 6.8232f)
                curveTo(12.2879f, 6.53f, 12.1966f, 6.2892f, 12.0029f, 6.1011f)
                curveTo(11.8092f, 5.9129f, 11.5602f, 5.8189f, 11.2559f, 5.8189f)
                curveTo(10.9515f, 5.8189f, 10.7025f, 5.9129f, 10.5088f, 6.1011f)
                curveTo(10.3206f, 6.2837f, 10.2293f, 6.5216f, 10.2349f, 6.8149f)
                lineTo(10.3843f, 12.3101f)
                curveTo(10.3953f, 12.9022f, 10.6886f, 13.1982f, 11.2642f, 13.1982f)
                close()
                moveTo(11.2642f, 16.7676f)
                curveTo(11.5962f, 16.7676f, 11.8784f, 16.6597f, 12.1108f, 16.4438f)
                curveTo(12.3488f, 16.228f, 12.4678f, 15.9596f, 12.4678f, 15.6387f)
                curveTo(12.4678f, 15.3122f, 12.3488f, 15.041f, 12.1108f, 14.8252f)
                curveTo(11.8784f, 14.6094f, 11.5962f, 14.5015f, 11.2642f, 14.5015f)
                curveTo(10.9321f, 14.5015f, 10.6471f, 14.6121f, 10.4092f, 14.8335f)
                curveTo(10.1768f, 15.0493f, 10.0605f, 15.3177f, 10.0605f, 15.6387f)
                curveTo(10.0605f, 15.9596f, 10.1795f, 16.228f, 10.4175f, 16.4438f)
                curveTo(10.6554f, 16.6597f, 10.9377f, 16.7676f, 11.2642f, 16.7676f)
                close()
            }
        }
            .build()
        return warningTriangle!!
    }

private var warningTriangle: ImageVector? = null
