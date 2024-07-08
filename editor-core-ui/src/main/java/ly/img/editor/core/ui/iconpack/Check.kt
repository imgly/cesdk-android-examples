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

val IconPack.Check: ImageVector
    get() {
        if (_check != null) {
            return _check!!
        }
        _check =
            Builder(
                name = "Check",
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
                    moveTo(9.0002f, 16.17f)
                    lineTo(4.8302f, 12.0f)
                    lineTo(3.4102f, 13.41f)
                    lineTo(9.0002f, 19.0f)
                    lineTo(21.0002f, 7.0f)
                    lineTo(19.5902f, 5.59f)
                    lineTo(9.0002f, 16.17f)
                    close()
                }
            }.build()
        return _check!!
    }

private var _check: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Check.IconPreview()
