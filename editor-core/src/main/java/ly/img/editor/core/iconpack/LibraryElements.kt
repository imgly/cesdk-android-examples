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

@Deprecated("Use IconPack.LibraryElements instead.", ReplaceWith("IconPack.LibraryElements"))
val IconPack.Libraryelements: ImageVector
    get() = LibraryElements

val IconPack.LibraryElements: ImageVector
    get() {
        if (`_library-elements` != null) {
            return `_library-elements`!!
        }
        `_library-elements` =
            Builder(
                name = "Library-elements",
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
                    pathFillType = NonZero,
                ) {
                    moveTo(13.0f, 3.0f)
                    lineTo(21.0f, 5.9998f)
                    verticalLineTo(17.9998f)
                    lineTo(13.0f, 21.0f)
                    verticalLineTo(3.0f)
                    close()
                }
            }
                .build()
        return `_library-elements`!!
    }

private var `_library-elements`: ImageVector? = null
