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

val IconPack.Sizel: ImageVector
    get() {
        if (_sizel != null) {
            return _sizel!!
        }
        _sizel =
            Builder(
                name = "Sizel",
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
                    moveTo(9.0f, 7.0f)
                    verticalLineTo(17.0f)
                    horizontalLineTo(15.0f)
                    verticalLineTo(15.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(7.0f)
                    horizontalLineTo(9.0f)
                    close()
                }
            }.build()
        return _sizel!!
    }

private var _sizel: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Sizel.IconPreview()
