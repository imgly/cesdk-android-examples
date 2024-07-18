package ly.img.editor.core.ui.iconpack

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val IconPack.Folder: ImageVector
    get() {
        if (_folder != null) {
            return _folder!!
        }
        _folder =
            Builder(
                name = "Folder",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f,
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF1C1B1F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(9.17f, 6.0f)
                    lineTo(11.17f, 8.0f)
                    horizontalLineTo(20.0f)
                    verticalLineTo(18.0f)
                    horizontalLineTo(4.0f)
                    verticalLineTo(6.0f)
                    horizontalLineTo(9.17f)
                    close()
                    moveTo(10.0f, 4.0f)
                    horizontalLineTo(4.0f)
                    curveTo(2.9f, 4.0f, 2.01f, 4.9f, 2.01f, 6.0f)
                    lineTo(2.0f, 18.0f)
                    curveTo(2.0f, 19.1f, 2.9f, 20.0f, 4.0f, 20.0f)
                    horizontalLineTo(20.0f)
                    curveTo(21.1f, 20.0f, 22.0f, 19.1f, 22.0f, 18.0f)
                    verticalLineTo(8.0f)
                    curveTo(22.0f, 6.9f, 21.1f, 6.0f, 20.0f, 6.0f)
                    horizontalLineTo(12.0f)
                    lineTo(10.0f, 4.0f)
                    close()
                }
            }.build()
        return _folder!!
    }

private var _folder: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Folder.IconPreview()
