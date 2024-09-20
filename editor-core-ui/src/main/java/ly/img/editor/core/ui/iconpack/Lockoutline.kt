package ly.img.editor.core.ui.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val IconPack.Lockoutline: ImageVector
    get() {
        if (_lockoutline != null) {
            return _lockoutline!!
        }
        _lockoutline =
            Builder(
                name = "Lockoutline",
                defaultWidth = 24.0.dp,
                defaultHeight =
                    24.0.dp,
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
                    moveTo(6.0f, 22.0f)
                    curveTo(5.45f, 22.0f, 4.9793f, 21.8043f, 4.588f, 21.413f)
                    curveTo(4.196f, 21.021f, 4.0f, 20.55f, 4.0f, 20.0f)
                    verticalLineTo(10.0f)
                    curveTo(4.0f, 9.45f, 4.196f, 8.979f, 4.588f, 8.587f)
                    curveTo(4.9793f, 8.1957f, 5.45f, 8.0f, 6.0f, 8.0f)
                    horizontalLineTo(7.0f)
                    verticalLineTo(6.0f)
                    curveTo(7.0f, 4.6167f, 7.4877f, 3.4373f, 8.463f, 2.462f)
                    curveTo(9.4377f, 1.4873f, 10.6167f, 1.0f, 12.0f, 1.0f)
                    curveTo(13.3833f, 1.0f, 14.5627f, 1.4873f, 15.538f, 2.462f)
                    curveTo(16.5127f, 3.4373f, 17.0f, 4.6167f, 17.0f, 6.0f)
                    verticalLineTo(8.0f)
                    horizontalLineTo(18.0f)
                    curveTo(18.55f, 8.0f, 19.021f, 8.1957f, 19.413f, 8.587f)
                    curveTo(19.8043f, 8.979f, 20.0f, 9.45f, 20.0f, 10.0f)
                    verticalLineTo(20.0f)
                    curveTo(20.0f, 20.55f, 19.8043f, 21.021f, 19.413f, 21.413f)
                    curveTo(19.021f, 21.8043f, 18.55f, 22.0f, 18.0f, 22.0f)
                    horizontalLineTo(6.0f)
                    close()
                    moveTo(6.0f, 20.0f)
                    horizontalLineTo(18.0f)
                    verticalLineTo(10.0f)
                    horizontalLineTo(6.0f)
                    verticalLineTo(20.0f)
                    close()
                    moveTo(12.0f, 17.0f)
                    curveTo(12.55f, 17.0f, 13.021f, 16.8043f, 13.413f, 16.413f)
                    curveTo(13.8043f, 16.021f, 14.0f, 15.55f, 14.0f, 15.0f)
                    curveTo(14.0f, 14.45f, 13.8043f, 13.979f, 13.413f, 13.587f)
                    curveTo(13.021f, 13.1957f, 12.55f, 13.0f, 12.0f, 13.0f)
                    curveTo(11.45f, 13.0f, 10.9793f, 13.1957f, 10.588f, 13.587f)
                    curveTo(10.196f, 13.979f, 10.0f, 14.45f, 10.0f, 15.0f)
                    curveTo(10.0f, 15.55f, 10.196f, 16.021f, 10.588f, 16.413f)
                    curveTo(10.9793f, 16.8043f, 11.45f, 17.0f, 12.0f, 17.0f)
                    close()
                    moveTo(9.0f, 8.0f)
                    horizontalLineTo(15.0f)
                    verticalLineTo(6.0f)
                    curveTo(15.0f, 5.1667f, 14.7083f, 4.4583f, 14.125f, 3.875f)
                    curveTo(13.5417f, 3.2917f, 12.8333f, 3.0f, 12.0f, 3.0f)
                    curveTo(11.1667f, 3.0f, 10.4583f, 3.2917f, 9.875f, 3.875f)
                    curveTo(9.2917f, 4.4583f, 9.0f, 5.1667f, 9.0f, 6.0f)
                    verticalLineTo(8.0f)
                    close()
                    moveTo(6.0f, 20.0f)
                    verticalLineTo(10.0f)
                    verticalLineTo(20.0f)
                    close()
                }
            }
                .build()
        return _lockoutline!!
    }

private var _lockoutline: ImageVector? = null
