package ly.img.editor.core.iconpack

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

val IconPack.LayersOutline: ImageVector
    get() {
        if (_layersoutline != null) {
            return _layersoutline!!
        }
        _layersoutline =
            Builder(
                name = "Layersoutline",
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
                    moveTo(12.0f, 21.05f)
                    lineTo(3.0f, 14.05f)
                    lineTo(4.65f, 12.8f)
                    lineTo(12.0f, 18.5f)
                    lineTo(19.35f, 12.8f)
                    lineTo(21.0f, 14.05f)
                    lineTo(12.0f, 21.05f)
                    close()
                    moveTo(12.0f, 16.0f)
                    lineTo(3.0f, 9.0f)
                    lineTo(12.0f, 2.0f)
                    lineTo(21.0f, 9.0f)
                    lineTo(12.0f, 16.0f)
                    close()
                    moveTo(12.0f, 13.45f)
                    lineTo(17.75f, 9.0f)
                    lineTo(12.0f, 4.55f)
                    lineTo(6.25f, 9.0f)
                    lineTo(12.0f, 13.45f)
                    close()
                }
            }.build()
        return _layersoutline!!
    }

private var _layersoutline: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.LayersOutline.IconPreview()
