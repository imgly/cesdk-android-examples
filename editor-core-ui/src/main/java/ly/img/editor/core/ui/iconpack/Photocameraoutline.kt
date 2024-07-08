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

val IconPack.Photocameraoutline: ImageVector
    get() {
        if (_photocameraoutline != null) {
            return _photocameraoutline!!
        }
        _photocameraoutline =
            Builder(
                name = "Photocameraoutline",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f,
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF46464F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(12.0f, 17.5f)
                    curveTo(13.25f, 17.5f, 14.3127f, 17.0627f, 15.188f, 16.188f)
                    curveTo(16.0627f, 15.3127f, 16.5f, 14.25f, 16.5f, 13.0f)
                    curveTo(16.5f, 11.75f, 16.0627f, 10.6873f, 15.188f, 9.812f)
                    curveTo(14.3127f, 8.9373f, 13.25f, 8.5f, 12.0f, 8.5f)
                    curveTo(10.75f, 8.5f, 9.6873f, 8.9373f, 8.812f, 9.812f)
                    curveTo(7.9373f, 10.6873f, 7.5f, 11.75f, 7.5f, 13.0f)
                    curveTo(7.5f, 14.25f, 7.9373f, 15.3127f, 8.812f, 16.188f)
                    curveTo(9.6873f, 17.0627f, 10.75f, 17.5f, 12.0f, 17.5f)
                    close()
                    moveTo(12.0f, 15.5f)
                    curveTo(11.3f, 15.5f, 10.7083f, 15.2583f, 10.225f, 14.775f)
                    curveTo(9.7417f, 14.2917f, 9.5f, 13.7f, 9.5f, 13.0f)
                    curveTo(9.5f, 12.3f, 9.7417f, 11.7083f, 10.225f, 11.225f)
                    curveTo(10.7083f, 10.7417f, 11.3f, 10.5f, 12.0f, 10.5f)
                    curveTo(12.7f, 10.5f, 13.2917f, 10.7417f, 13.775f, 11.225f)
                    curveTo(14.2583f, 11.7083f, 14.5f, 12.3f, 14.5f, 13.0f)
                    curveTo(14.5f, 13.7f, 14.2583f, 14.2917f, 13.775f, 14.775f)
                    curveTo(13.2917f, 15.2583f, 12.7f, 15.5f, 12.0f, 15.5f)
                    close()
                    moveTo(4.0f, 21.0f)
                    curveTo(3.45f, 21.0f, 2.9793f, 20.8043f, 2.588f, 20.413f)
                    curveTo(2.196f, 20.021f, 2.0f, 19.55f, 2.0f, 19.0f)
                    verticalLineTo(7.0f)
                    curveTo(2.0f, 6.45f, 2.196f, 5.9793f, 2.588f, 5.588f)
                    curveTo(2.9793f, 5.196f, 3.45f, 5.0f, 4.0f, 5.0f)
                    horizontalLineTo(7.15f)
                    lineTo(9.0f, 3.0f)
                    horizontalLineTo(15.0f)
                    lineTo(16.85f, 5.0f)
                    horizontalLineTo(20.0f)
                    curveTo(20.55f, 5.0f, 21.021f, 5.196f, 21.413f, 5.588f)
                    curveTo(21.8043f, 5.9793f, 22.0f, 6.45f, 22.0f, 7.0f)
                    verticalLineTo(19.0f)
                    curveTo(22.0f, 19.55f, 21.8043f, 20.021f, 21.413f, 20.413f)
                    curveTo(21.021f, 20.8043f, 20.55f, 21.0f, 20.0f, 21.0f)
                    horizontalLineTo(4.0f)
                    close()
                    moveTo(20.0f, 19.0f)
                    verticalLineTo(7.0f)
                    horizontalLineTo(15.95f)
                    lineTo(14.125f, 5.0f)
                    horizontalLineTo(9.875f)
                    lineTo(8.05f, 7.0f)
                    horizontalLineTo(4.0f)
                    verticalLineTo(19.0f)
                    horizontalLineTo(20.0f)
                    close()
                }
            }.build()
        return _photocameraoutline!!
    }

private var _photocameraoutline: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Photocameraoutline.IconPreview()
