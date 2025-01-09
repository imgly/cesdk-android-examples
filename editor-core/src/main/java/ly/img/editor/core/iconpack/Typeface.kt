package ly.img.editor.core.iconpack

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

val IconPack.Typeface: ImageVector
    get() {
        if (_typeface != null) {
            return _typeface!!
        }
        _typeface =
            Builder(
                name = "Typeface",
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
                    moveTo(13.9998f, 5.0f)
                    lineTo(14.8771f, 8.2747f)
                    lineTo(13.9998f, 8.5f)
                    curveTo(13.5886f, 7.746f, 13.2911f, 7.4873f, 12.8067f, 7.106f)
                    curveTo(12.3224f, 6.7333f, 11.7558f, 6.7333f, 11.1984f, 6.7333f)
                    horizontalLineTo(8.9999f)
                    verticalLineTo(15.7f)
                    curveTo(8.9999f, 16.1333f, 8.9999f, 16.5667f, 9.3015f, 16.7833f)
                    curveTo(9.6122f, 17.0f, 10.3877f, 17.0f, 11.0f, 17.0f)
                    verticalLineTo(18.0f)
                    horizontalLineTo(5.0f)
                    verticalLineTo(17.0f)
                    curveTo(5.6123f, 17.0f, 6.3877f, 17.0f, 6.6984f, 16.7833f)
                    curveTo(6.9999f, 16.5667f, 6.9999f, 16.1333f, 6.9999f, 15.7f)
                    verticalLineTo(6.7333f)
                    horizontalLineTo(4.7153f)
                    curveTo(4.1579f, 6.7333f, 3.6775f, 6.7333f, 3.1931f, 7.106f)
                    curveTo(2.7088f, 7.4873f, 2.4112f, 7.746f, 2.0f, 8.5f)
                    lineTo(1.1227f, 8.2747f)
                    lineTo(1.9998f, 5.0f)
                    horizontalLineTo(13.9998f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF49454F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(12.445f, 20.6116f)
                    curveTo(12.7362f, 20.8705f, 13.1354f, 21.0f, 13.6424f, 21.0f)
                    curveTo(14.9369f, 21.0f, 15.9455f, 20.1505f, 16.6683f, 18.4515f)
                    curveTo(17.103f, 17.4265f, 17.6367f, 15.4427f, 18.2694f, 12.5f)
                    horizontalLineTo(21.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(18.5835f)
                    curveTo(18.595f, 10.9437f, 18.6065f, 10.887f, 18.6181f, 10.8301f)
                    lineTo(18.7314f, 10.1424f)
                    lineTo(19.0388f, 8.9693f)
                    curveTo(19.2492f, 8.1602f, 19.4757f, 7.5453f, 19.7184f, 7.1246f)
                    curveTo(19.9666f, 6.7039f, 20.2416f, 6.4935f, 20.5437f, 6.4935f)
                    curveTo(20.6516f, 6.4935f, 20.7271f, 6.5205f, 20.7702f, 6.5744f)
                    curveTo(20.808f, 6.6338f, 20.8269f, 6.6823f, 20.8269f, 6.7201f)
                    curveTo(20.8269f, 6.747f, 20.781f, 6.8387f, 20.6893f, 6.9952f)
                    curveTo(20.603f, 7.1462f, 20.5599f, 7.2945f, 20.5599f, 7.4401f)
                    curveTo(20.5599f, 7.6505f, 20.6408f, 7.8339f, 20.8026f, 7.9903f)
                    curveTo(20.9644f, 8.1467f, 21.1559f, 8.2249f, 21.377f, 8.2249f)
                    curveTo(21.6036f, 8.2249f, 21.7977f, 8.1467f, 21.9595f, 7.9903f)
                    curveTo(22.1268f, 7.8285f, 22.2104f, 7.6154f, 22.2104f, 7.3511f)
                    curveTo(22.2104f, 6.9035f, 22.0485f, 6.5663f, 21.7249f, 6.3398f)
                    curveTo(21.4067f, 6.1133f, 20.9968f, 6.0f, 20.4951f, 6.0f)
                    curveTo(19.8695f, 6.0f, 19.26f, 6.2562f, 18.6667f, 6.7686f)
                    curveTo(18.1758f, 7.2001f, 17.7713f, 7.7691f, 17.4531f, 8.4757f)
                    curveTo(17.1402f, 9.1769f, 16.9353f, 9.7325f, 16.8382f, 10.1424f)
                    lineTo(16.6845f, 10.8301f)
                    lineTo(16.6443f, 11.0f)
                    horizontalLineTo(14.0f)
                    verticalLineTo(12.5f)
                    horizontalLineTo(16.2893f)
                    lineTo(16.0049f, 13.7023f)
                    curveTo(15.3252f, 16.8954f, 14.8776f, 18.8209f, 14.6618f, 19.479f)
                    curveTo(14.4461f, 20.1424f, 14.1305f, 20.4741f, 13.7152f, 20.4741f)
                    curveTo(13.6343f, 20.4741f, 13.5669f, 20.4579f, 13.5129f, 20.4256f)
                    curveTo(13.4536f, 20.3932f, 13.4239f, 20.3474f, 13.4239f, 20.288f)
                    curveTo(13.4239f, 20.2395f, 13.4698f, 20.1478f, 13.5615f, 20.0129f)
                    curveTo(13.6478f, 19.8835f, 13.6909f, 19.7513f, 13.6909f, 19.6165f)
                    curveTo(13.6909f, 19.363f, 13.6073f, 19.1607f, 13.4401f, 19.0097f)
                    curveTo(13.2675f, 18.8641f, 13.0734f, 18.7913f, 12.8576f, 18.7913f)
                    curveTo(12.6365f, 18.7913f, 12.4396f, 18.8722f, 12.267f, 19.034f)
                    curveTo(12.089f, 19.1958f, 12.0f, 19.4088f, 12.0f, 19.6731f)
                    curveTo(12.0f, 20.0399f, 12.1483f, 20.3527f, 12.445f, 20.6116f)
                    close()
                }
            }.build()
        return _typeface!!
    }

private var _typeface: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Typeface.IconPreview()
