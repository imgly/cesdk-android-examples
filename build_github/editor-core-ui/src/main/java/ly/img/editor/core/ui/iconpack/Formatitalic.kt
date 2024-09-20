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

val IconPack.Formatitalic: ImageVector
    get() {
        if (_formatitalic != null) {
            return _formatitalic!!
        }
        _formatitalic =
            Builder(
                name = "Formatitalic",
                defaultWidth = 24.0.dp,
                defaultHeight =
                    24.0.dp,
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
                    moveTo(10.0f, 4.0f)
                    verticalLineTo(7.0f)
                    horizontalLineTo(12.21f)
                    lineTo(8.79f, 15.0f)
                    horizontalLineTo(6.0f)
                    verticalLineTo(18.0f)
                    horizontalLineTo(14.0f)
                    verticalLineTo(15.0f)
                    horizontalLineTo(11.79f)
                    lineTo(15.21f, 7.0f)
                    horizontalLineTo(18.0f)
                    verticalLineTo(4.0f)
                    horizontalLineTo(10.0f)
                    close()
                }
            }.build()
        return _formatitalic!!
    }

private var _formatitalic: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Formatitalic.IconPreview()
