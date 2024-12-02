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

@Deprecated("Use IconPack.TextFields instead.", ReplaceWith("IconPack.TextFields"))
val IconPack.Textfields: ImageVector
    get() = TextFields

val IconPack.TextFields: ImageVector
    get() {
        if (_textfields != null) {
            return _textfields!!
        }
        _textfields =
            Builder(
                name = "Textfields",
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
                    moveTo(7.0f, 20.0f)
                    verticalLineTo(7.0f)
                    horizontalLineTo(2.0f)
                    verticalLineTo(4.0f)
                    horizontalLineTo(15.0f)
                    verticalLineTo(7.0f)
                    horizontalLineTo(10.0f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(7.0f)
                    close()
                    moveTo(16.0f, 20.0f)
                    verticalLineTo(12.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(9.0f)
                    horizontalLineTo(22.0f)
                    verticalLineTo(12.0f)
                    horizontalLineTo(19.0f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(16.0f)
                    close()
                }
            }
                .build()
        return _textfields!!
    }

private var _textfields: ImageVector? = null
