package ly.img.editor.core.iconpack

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val IconPack.Delete: ImageVector
    get() {
        if (_delete != null) {
            return _delete!!
        }
        _delete =
            Builder(
                name = "Delete",
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
                    pathFillType = EvenOdd,
                ) {
                    moveTo(15.0f, 3.0f)
                    verticalLineTo(4.0f)
                    horizontalLineTo(20.0f)
                    verticalLineTo(6.0f)
                    horizontalLineTo(19.0f)
                    verticalLineTo(19.0f)
                    curveTo(19.0f, 20.1f, 18.1f, 21.0f, 17.0f, 21.0f)
                    horizontalLineTo(7.0f)
                    curveTo(5.9f, 21.0f, 5.0f, 20.1f, 5.0f, 19.0f)
                    verticalLineTo(6.0f)
                    horizontalLineTo(4.0f)
                    verticalLineTo(4.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(3.0f)
                    horizontalLineTo(15.0f)
                    close()
                    moveTo(7.0f, 19.0f)
                    horizontalLineTo(17.0f)
                    verticalLineTo(6.0f)
                    horizontalLineTo(7.0f)
                    verticalLineTo(19.0f)
                    close()
                    moveTo(9.0f, 8.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(17.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(8.0f)
                    close()
                    moveTo(15.0f, 8.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(17.0f)
                    horizontalLineTo(15.0f)
                    verticalLineTo(8.0f)
                    close()
                }
            }.build()
        return _delete!!
    }

private var _delete: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Delete.IconPreview()
