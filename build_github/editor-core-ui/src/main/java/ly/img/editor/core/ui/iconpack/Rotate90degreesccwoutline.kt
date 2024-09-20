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

val IconPack.Rotate90degreesccwoutline: ImageVector
    get() {
        if (_rotate90degreesccwoutline != null) {
            return _rotate90degreesccwoutline!!
        }
        _rotate90degreesccwoutline =
            Builder(
                name = "Rotate90degreesccwoutline",
                defaultWidth =
                    24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight =
                24.0f,
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
                    moveTo(13.0f, 22.0f)
                    curveTo(12.15f, 22.0f, 11.3167f, 21.8833f, 10.5f, 21.65f)
                    curveTo(9.6833f, 21.4167f, 8.9167f, 21.0667f, 8.2f, 20.6f)
                    lineTo(9.65f, 19.15f)
                    curveTo(10.1667f, 19.4333f, 10.7083f, 19.646f, 11.275f, 19.788f)
                    curveTo(11.8417f, 19.9293f, 12.4167f, 20.0f, 13.0f, 20.0f)
                    curveTo(14.95f, 20.0f, 16.604f, 19.3207f, 17.962f, 17.962f)
                    curveTo(19.3207f, 16.604f, 20.0f, 14.95f, 20.0f, 13.0f)
                    curveTo(20.0f, 11.05f, 19.3207f, 9.3957f, 17.962f, 8.037f)
                    curveTo(16.604f, 6.679f, 14.95f, 6.0f, 13.0f, 6.0f)
                    horizontalLineTo(12.85f)
                    lineTo(14.4f, 7.55f)
                    lineTo(13.0f, 9.0f)
                    lineTo(9.0f, 5.0f)
                    lineTo(13.0f, 1.0f)
                    lineTo(14.4f, 2.45f)
                    lineTo(12.85f, 4.0f)
                    horizontalLineTo(13.0f)
                    curveTo(15.5f, 4.0f, 17.625f, 4.875f, 19.375f, 6.625f)
                    curveTo(21.125f, 8.375f, 22.0f, 10.5f, 22.0f, 13.0f)
                    curveTo(22.0f, 14.25f, 21.7627f, 15.4207f, 21.288f, 16.512f)
                    curveTo(20.8127f, 17.604f, 20.171f, 18.554f, 19.363f, 19.362f)
                    curveTo(18.5543f, 20.1707f, 17.6043f, 20.8127f, 16.513f, 21.288f)
                    curveTo(15.421f, 21.7627f, 14.25f, 22.0f, 13.0f, 22.0f)
                    close()
                    moveTo(7.0f, 19.0f)
                    lineTo(1.0f, 13.0f)
                    lineTo(7.0f, 7.0f)
                    lineTo(13.0f, 13.0f)
                    lineTo(7.0f, 19.0f)
                    close()
                    moveTo(7.0f, 16.15f)
                    lineTo(10.15f, 13.0f)
                    lineTo(7.0f, 9.85f)
                    lineTo(3.85f, 13.0f)
                    lineTo(7.0f, 16.15f)
                    close()
                }
            }.build()
        return _rotate90degreesccwoutline!!
    }

private var _rotate90degreesccwoutline: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Rotate90degreesccwoutline.IconPreview()
