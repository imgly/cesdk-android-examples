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

val IconPack.Edit: ImageVector
    get() {
        if (_edit != null) {
            return _edit!!
        }
        _edit =
            Builder(
                name = "Edit",
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
                    moveTo(5.0f, 19.0f)
                    horizontalLineTo(6.4f)
                    lineTo(15.025f, 10.375f)
                    lineTo(13.625f, 8.975f)
                    lineTo(5.0f, 17.6f)
                    verticalLineTo(19.0f)
                    close()
                    moveTo(19.3f, 8.925f)
                    lineTo(15.05f, 4.725f)
                    lineTo(16.45f, 3.325f)
                    curveTo(16.8333f, 2.9417f, 17.3043f, 2.75f, 17.863f, 2.75f)
                    curveTo(18.421f, 2.75f, 18.8917f, 2.9417f, 19.275f, 3.325f)
                    lineTo(20.675f, 4.725f)
                    curveTo(21.0583f, 5.1083f, 21.2583f, 5.571f, 21.275f, 6.113f)
                    curveTo(21.2917f, 6.6543f, 21.1083f, 7.1167f, 20.725f, 7.5f)
                    lineTo(19.3f, 8.925f)
                    close()
                    moveTo(17.85f, 10.4f)
                    lineTo(7.25f, 21.0f)
                    horizontalLineTo(3.0f)
                    verticalLineTo(16.75f)
                    lineTo(13.6f, 6.15f)
                    lineTo(17.85f, 10.4f)
                    close()
                    moveTo(14.325f, 9.675f)
                    lineTo(13.625f, 8.975f)
                    lineTo(15.025f, 10.375f)
                    lineTo(14.325f, 9.675f)
                    close()
                }
            }
                .build()
        return _edit!!
    }

private var _edit: ImageVector? = null
