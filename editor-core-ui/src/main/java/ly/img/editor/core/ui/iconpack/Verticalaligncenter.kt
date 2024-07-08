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

val IconPack.Verticalaligncenter: ImageVector
    get() {
        if (_verticalaligncenter != null) {
            return _verticalaligncenter!!
        }
        _verticalaligncenter =
            Builder(
                name = "Verticalaligncenter",
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
                    moveTo(11.0f, 22.0f)
                    verticalLineTo(17.8f)
                    lineTo(9.4f, 19.4f)
                    lineTo(8.0f, 18.0f)
                    lineTo(12.0f, 14.0f)
                    lineTo(16.0f, 18.0f)
                    lineTo(14.6f, 19.4f)
                    lineTo(13.0f, 17.8f)
                    verticalLineTo(22.0f)
                    horizontalLineTo(11.0f)
                    close()
                    moveTo(4.0f, 13.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(20.0f)
                    verticalLineTo(13.0f)
                    horizontalLineTo(4.0f)
                    close()
                    moveTo(12.0f, 10.0f)
                    lineTo(8.0f, 6.0f)
                    lineTo(9.4f, 4.6f)
                    lineTo(11.0f, 6.2f)
                    verticalLineTo(2.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(6.2f)
                    lineTo(14.6f, 4.6f)
                    lineTo(16.0f, 6.0f)
                    lineTo(12.0f, 10.0f)
                    close()
                }
            }.build()
        return _verticalaligncenter!!
    }

private var _verticalaligncenter: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Verticalaligncenter.IconPreview()
