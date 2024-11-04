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

val IconPack.SplitscreenLeftOutline: ImageVector
    get() {
        if (`_splitscreen-left-outline` != null) {
            return `_splitscreen-left-outline`!!
        }
        `_splitscreen-left-outline` =
            Builder(
                name = "Splitscreen-left-outline",
                defaultWidth =
                    24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight =
                24.0f,
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
                    moveTo(5.0f, 21.0f)
                    curveTo(4.45f, 21.0f, 3.979f, 20.804f, 3.587f, 20.412f)
                    curveTo(3.195f, 20.02f, 2.9993f, 19.5493f, 3.0f, 19.0f)
                    verticalLineTo(5.0f)
                    curveTo(3.0f, 4.45f, 3.196f, 3.979f, 3.588f, 3.587f)
                    curveTo(3.98f, 3.195f, 4.4507f, 2.9993f, 5.0f, 3.0f)
                    horizontalLineTo(9.0f)
                    curveTo(9.55f, 3.0f, 10.021f, 3.196f, 10.413f, 3.588f)
                    curveTo(10.805f, 3.98f, 11.0007f, 4.4507f, 11.0f, 5.0f)
                    verticalLineTo(19.0f)
                    curveTo(11.0f, 19.55f, 10.804f, 20.021f, 10.412f, 20.413f)
                    curveTo(10.02f, 20.805f, 9.5493f, 21.0007f, 9.0f, 21.0f)
                    horizontalLineTo(5.0f)
                    close()
                    moveTo(15.0f, 21.0f)
                    curveTo(14.45f, 21.0f, 13.979f, 20.804f, 13.587f, 20.412f)
                    curveTo(13.195f, 20.02f, 12.9993f, 19.5493f, 13.0f, 19.0f)
                    verticalLineTo(5.0f)
                    curveTo(13.0f, 4.45f, 13.196f, 3.979f, 13.588f, 3.587f)
                    curveTo(13.98f, 3.195f, 14.4507f, 2.9993f, 15.0f, 3.0f)
                    horizontalLineTo(19.0f)
                    curveTo(19.55f, 3.0f, 20.021f, 3.196f, 20.413f, 3.588f)
                    curveTo(20.805f, 3.98f, 21.0007f, 4.4507f, 21.0f, 5.0f)
                    verticalLineTo(19.0f)
                    curveTo(21.0f, 19.55f, 20.804f, 20.021f, 20.412f, 20.413f)
                    curveTo(20.02f, 20.805f, 19.5493f, 21.0007f, 19.0f, 21.0f)
                    horizontalLineTo(15.0f)
                    close()
                    moveTo(19.0f, 5.0f)
                    horizontalLineTo(15.0f)
                    verticalLineTo(19.0f)
                    horizontalLineTo(19.0f)
                    verticalLineTo(5.0f)
                    close()
                }
            }
                .build()
        return `_splitscreen-left-outline`!!
    }

private var `_splitscreen-left-outline`: ImageVector? = null
