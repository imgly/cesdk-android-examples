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

val IconPack.Arrowdropdown: ImageVector
    get() {
        if (_arrowdropdown != null) {
            return _arrowdropdown!!
        }
        _arrowdropdown =
            Builder(
                name = "Arrowdropdown",
                defaultWidth = 24.0.dp,
                defaultHeight =
                    24.0.dp,
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
                    moveTo(7.0f, 10.0f)
                    lineTo(12.0f, 15.0f)
                    lineTo(17.0f, 10.0f)
                    horizontalLineTo(7.0f)
                    close()
                }
            }.build()
        return _arrowdropdown!!
    }

private var _arrowdropdown: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Arrowdropdown.IconPreview()
