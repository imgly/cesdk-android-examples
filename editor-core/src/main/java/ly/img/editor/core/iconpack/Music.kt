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

val IconPack.Music: ImageVector
    get() {
        if (_music != null) {
            return _music!!
        }
        _music =
            Builder(
                name = "Music",
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
                    moveTo(21.0f, 3.0f)
                    verticalLineTo(15.5f)
                    curveTo(21.0f, 16.4283f, 20.6313f, 17.3185f, 19.9749f, 17.9749f)
                    curveTo(19.3185f, 18.6313f, 18.4283f, 19.0f, 17.5f, 19.0f)
                    curveTo(16.5717f, 19.0f, 15.6815f, 18.6313f, 15.0251f, 17.9749f)
                    curveTo(14.3687f, 17.3185f, 14.0f, 16.4283f, 14.0f, 15.5f)
                    curveTo(14.0f, 14.5717f, 14.3687f, 13.6815f, 15.0251f, 13.0251f)
                    curveTo(15.6815f, 12.3687f, 16.5717f, 12.0f, 17.5f, 12.0f)
                    curveTo(18.04f, 12.0f, 18.55f, 12.12f, 19.0f, 12.34f)
                    verticalLineTo(6.47f)
                    lineTo(9.0f, 8.6f)
                    verticalLineTo(17.5f)
                    curveTo(9.0f, 18.4283f, 8.6313f, 19.3185f, 7.9749f, 19.9749f)
                    curveTo(7.3185f, 20.6313f, 6.4283f, 21.0f, 5.5f, 21.0f)
                    curveTo(4.5717f, 21.0f, 3.6815f, 20.6313f, 3.0251f, 19.9749f)
                    curveTo(2.3688f, 19.3185f, 2.0f, 18.4283f, 2.0f, 17.5f)
                    curveTo(2.0f, 16.5717f, 2.3688f, 15.6815f, 3.0251f, 15.0251f)
                    curveTo(3.6815f, 14.3687f, 4.5717f, 14.0f, 5.5f, 14.0f)
                    curveTo(6.04f, 14.0f, 6.55f, 14.12f, 7.0f, 14.34f)
                    verticalLineTo(6.0f)
                    lineTo(21.0f, 3.0f)
                    close()
                }
            }
                .build()
        return _music!!
    }

private var _music: ImageVector? = null
