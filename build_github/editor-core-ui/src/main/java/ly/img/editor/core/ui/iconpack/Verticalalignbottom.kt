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

val IconPack.Verticalalignbottom: ImageVector
    get() {
        if (_verticalalignbottom != null) {
            return _verticalalignbottom!!
        }
        _verticalalignbottom =
            Builder(
                name = "Verticalalignbottom",
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
                    moveTo(4.0f, 21.0f)
                    verticalLineTo(19.0f)
                    horizontalLineTo(20.0f)
                    verticalLineTo(21.0f)
                    horizontalLineTo(4.0f)
                    close()
                    moveTo(12.0f, 17.0f)
                    lineTo(7.0f, 12.0f)
                    lineTo(8.4f, 10.6f)
                    lineTo(11.0f, 13.2f)
                    verticalLineTo(3.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(13.2f)
                    lineTo(15.6f, 10.6f)
                    lineTo(17.0f, 12.0f)
                    lineTo(12.0f, 17.0f)
                    close()
                }
            }.build()
        return _verticalalignbottom!!
    }

private var _verticalalignbottom: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Verticalalignbottom.IconPreview()
