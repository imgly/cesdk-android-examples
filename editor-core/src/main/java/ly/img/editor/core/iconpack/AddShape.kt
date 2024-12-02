package ly.img.editor.core.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val IconPack.AddShape: ImageVector
    get() {
        if (_addshape != null) {
            return _addshape!!
        }
        _addshape =
            Builder(
                name = "Addshape",
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
                    pathFillType = NonZero,
                ) {
                    moveTo(13.4482f, 11.3182f)
                    curveTo(13.2173f, 11.7548f, 12.9139f, 12.1611f, 12.538f, 12.537f)
                    curveTo(11.5627f, 13.5123f, 10.3833f, 14.0f, 9.0f, 14.0f)
                    curveTo(7.6167f, 14.0f, 6.4377f, 13.5123f, 5.463f, 12.537f)
                    curveTo(4.4877f, 11.5623f, 4.0f, 10.3833f, 4.0f, 9.0f)
                    curveTo(4.0f, 7.6167f, 4.4877f, 6.4377f, 5.463f, 5.463f)
                    curveTo(6.4377f, 4.4877f, 7.6167f, 4.0f, 9.0f, 4.0f)
                    curveTo(9.7885f, 4.0f, 10.5106f, 4.1584f, 11.1665f, 4.4753f)
                    curveTo(11.3155f, 3.8049f, 11.5605f, 3.1706f, 11.8865f, 2.5878f)
                    curveTo(11.0097f, 2.1959f, 10.0475f, 2.0f, 9.0f, 2.0f)
                    curveTo(7.05f, 2.0f, 5.3957f, 2.679f, 4.037f, 4.037f)
                    curveTo(2.679f, 5.3957f, 2.0f, 7.05f, 2.0f, 9.0f)
                    curveTo(2.0f, 10.95f, 2.679f, 12.604f, 4.037f, 13.962f)
                    curveTo(5.3957f, 15.3207f, 7.05f, 16.0f, 9.0f, 16.0f)
                    curveTo(10.95f, 16.0f, 12.6043f, 15.3207f, 13.963f, 13.962f)
                    curveTo(14.4468f, 13.4782f, 14.8444f, 12.9569f, 15.1558f, 12.398f)
                    curveTo(14.5334f, 12.1209f, 13.9589f, 11.7557f, 13.4482f, 11.3182f)
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
                    moveTo(20.0f, 12.7101f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(10.0f)
                    verticalLineTo(17.95f)
                    curveTo(9.8333f, 17.9667f, 9.6707f, 17.9793f, 9.512f, 17.988f)
                    curveTo(9.354f, 17.996f, 9.1833f, 18.0f, 9.0f, 18.0f)
                    curveTo(8.8167f, 18.0f, 8.646f, 17.996f, 8.488f, 17.988f)
                    curveTo(8.3293f, 17.9793f, 8.1667f, 17.9667f, 8.0f, 17.95f)
                    verticalLineTo(20.0f)
                    curveTo(8.0f, 20.55f, 8.196f, 21.021f, 8.588f, 21.413f)
                    curveTo(8.9793f, 21.8043f, 9.45f, 22.0f, 10.0f, 22.0f)
                    horizontalLineTo(20.0f)
                    curveTo(20.55f, 22.0f, 21.021f, 21.8043f, 21.413f, 21.413f)
                    curveTo(21.8043f, 21.021f, 22.0f, 20.55f, 22.0f, 20.0f)
                    verticalLineTo(11.7453f)
                    curveTo(21.396f, 12.1666f, 20.7224f, 12.4951f, 20.0f, 12.7101f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd,
                ) {
                    moveTo(18.0f, 1.0f)
                    curveTo(15.2386f, 1.0f, 13.0f, 3.2386f, 13.0f, 6.0f)
                    curveTo(13.0f, 8.7614f, 15.2386f, 11.0f, 18.0f, 11.0f)
                    curveTo(20.7614f, 11.0f, 23.0f, 8.7614f, 23.0f, 6.0f)
                    curveTo(23.0f, 3.2386f, 20.7614f, 1.0f, 18.0f, 1.0f)
                    close()
                    moveTo(17.5f, 5.5f)
                    verticalLineTo(3.0f)
                    horizontalLineTo(18.5f)
                    verticalLineTo(5.5f)
                    horizontalLineTo(21.0f)
                    verticalLineTo(6.5f)
                    horizontalLineTo(18.5f)
                    verticalLineTo(9.0f)
                    horizontalLineTo(17.5f)
                    verticalLineTo(6.5f)
                    horizontalLineTo(15.0f)
                    verticalLineTo(5.5f)
                    horizontalLineTo(17.5f)
                    close()
                }
            }.build()
        return _addshape!!
    }

private var _addshape: ImageVector? = null
