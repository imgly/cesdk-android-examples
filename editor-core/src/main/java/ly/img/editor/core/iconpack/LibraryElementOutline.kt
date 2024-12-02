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

@Deprecated("Use IconPack.LibraryElementsOutline instead.", ReplaceWith("IconPack.LibraryElementsOutline"))
val IconPack.Libraryelementsoutline: ImageVector
    get() = LibraryElementsOutline

val IconPack.LibraryElementsOutline: ImageVector
    get() {
        if (`_library-elements-outline` != null) {
            return `_library-elements-outline`!!
        }
        `_library-elements-outline` =
            Builder(
                name = "Library-elements-outline",
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
                    moveTo(5.0f, 3.0f)
                    horizontalLineTo(3.0f)
                    verticalLineTo(21.0f)
                    horizontalLineTo(5.0f)
                    verticalLineTo(3.0f)
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
                    moveTo(8.0f, 3.0f)
                    horizontalLineTo(10.0f)
                    verticalLineTo(21.0f)
                    horizontalLineTo(8.0f)
                    verticalLineTo(3.0f)
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
                    moveTo(13.0f, 3.0f)
                    verticalLineTo(21.0f)
                    lineTo(21.0f, 17.9998f)
                    verticalLineTo(5.9998f)
                    lineTo(13.0f, 3.0f)
                    close()
                    moveTo(15.0f, 5.8859f)
                    verticalLineTo(18.1139f)
                    lineTo(19.0f, 16.6138f)
                    verticalLineTo(7.3858f)
                    lineTo(15.0f, 5.8859f)
                    close()
                }
            }
                .build()
        return `_library-elements-outline`!!
    }

private var `_library-elements-outline`: ImageVector? = null
