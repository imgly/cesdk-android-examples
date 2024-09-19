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

val IconPack.Volumemedium: ImageVector
    get() {
        if (_volumemedium != null) {
            return _volumemedium!!
        }
        _volumemedium =
            Builder(
                name = "Volumemedium",
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
                    moveTo(5.0f, 9.0f)
                    verticalLineTo(15.0f)
                    horizontalLineTo(9.0f)
                    lineTo(14.0f, 20.0f)
                    verticalLineTo(4.0f)
                    lineTo(9.0f, 9.0f)
                    moveTo(18.5f, 12.0f)
                    curveTo(18.5f, 10.23f, 17.5f, 8.71f, 16.0f, 7.97f)
                    verticalLineTo(16.0f)
                    curveTo(17.5f, 15.29f, 18.5f, 13.76f, 18.5f, 12.0f)
                    close()
                }
            }
                .build()
        return _volumemedium!!
    }

private var _volumemedium: ImageVector? = null
