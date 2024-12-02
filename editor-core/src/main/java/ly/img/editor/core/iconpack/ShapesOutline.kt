package ly.img.editor.core.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Deprecated("Use IconPack.ShapesOutline instead.", ReplaceWith("IconPack.ShapesOutline"))
val IconPack.Shapesoutline: ImageVector
    get() = ShapesOutline

val IconPack.ShapesOutline: ImageVector
    get() {
        if (_shapesoutline != null) {
            return _shapesoutline!!
        }
        _shapesoutline =
            Builder(
                name = "Shapesoutline",
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
                    pathFillType = NonZero,
                ) {
                    moveTo(8.0f, 17.95f)
                    curveTo(8.1667f, 17.9667f, 8.3293f, 17.9793f, 8.488f, 17.988f)
                    curveTo(8.646f, 17.996f, 8.8167f, 18.0f, 9.0f, 18.0f)
                    curveTo(9.1833f, 18.0f, 9.354f, 17.996f, 9.512f, 17.988f)
                    curveTo(9.6707f, 17.9793f, 9.8333f, 17.9667f, 10.0f, 17.95f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(20.0f)
                    verticalLineTo(10.0f)
                    horizontalLineTo(17.95f)
                    curveTo(17.9667f, 9.8333f, 17.9793f, 9.6707f, 17.988f, 9.512f)
                    curveTo(17.996f, 9.354f, 18.0f, 9.1833f, 18.0f, 9.0f)
                    curveTo(18.0f, 8.8167f, 17.996f, 8.646f, 17.988f, 8.488f)
                    curveTo(17.9793f, 8.3293f, 17.9667f, 8.1667f, 17.95f, 8.0f)
                    horizontalLineTo(20.0f)
                    curveTo(20.55f, 8.0f, 21.021f, 8.1957f, 21.413f, 8.587f)
                    curveTo(21.8043f, 8.979f, 22.0f, 9.45f, 22.0f, 10.0f)
                    verticalLineTo(20.0f)
                    curveTo(22.0f, 20.55f, 21.8043f, 21.021f, 21.413f, 21.413f)
                    curveTo(21.021f, 21.8043f, 20.55f, 22.0f, 20.0f, 22.0f)
                    horizontalLineTo(10.0f)
                    curveTo(9.45f, 22.0f, 8.9793f, 21.8043f, 8.588f, 21.413f)
                    curveTo(8.196f, 21.021f, 8.0f, 20.55f, 8.0f, 20.0f)
                    verticalLineTo(17.95f)
                    close()
                    moveTo(9.0f, 16.0f)
                    curveTo(7.05f, 16.0f, 5.3957f, 15.3207f, 4.037f, 13.962f)
                    curveTo(2.679f, 12.604f, 2.0f, 10.95f, 2.0f, 9.0f)
                    curveTo(2.0f, 7.05f, 2.679f, 5.3957f, 4.037f, 4.037f)
                    curveTo(5.3957f, 2.679f, 7.05f, 2.0f, 9.0f, 2.0f)
                    curveTo(10.95f, 2.0f, 12.6043f, 2.679f, 13.963f, 4.037f)
                    curveTo(15.321f, 5.3957f, 16.0f, 7.05f, 16.0f, 9.0f)
                    curveTo(16.0f, 10.95f, 15.321f, 12.604f, 13.963f, 13.962f)
                    curveTo(12.6043f, 15.3207f, 10.95f, 16.0f, 9.0f, 16.0f)
                    close()
                    moveTo(9.0f, 14.0f)
                    curveTo(10.3833f, 14.0f, 11.5627f, 13.5123f, 12.538f, 12.537f)
                    curveTo(13.5127f, 11.5623f, 14.0f, 10.3833f, 14.0f, 9.0f)
                    curveTo(14.0f, 7.6167f, 13.5127f, 6.4377f, 12.538f, 5.463f)
                    curveTo(11.5627f, 4.4877f, 10.3833f, 4.0f, 9.0f, 4.0f)
                    curveTo(7.6167f, 4.0f, 6.4377f, 4.4877f, 5.463f, 5.463f)
                    curveTo(4.4877f, 6.4377f, 4.0f, 7.6167f, 4.0f, 9.0f)
                    curveTo(4.0f, 10.3833f, 4.4877f, 11.5623f, 5.463f, 12.537f)
                    curveTo(6.4377f, 13.5123f, 7.6167f, 14.0f, 9.0f, 14.0f)
                    close()
                }
            }
                .build()
        return _shapesoutline!!
    }

private var _shapesoutline: ImageVector? = null
