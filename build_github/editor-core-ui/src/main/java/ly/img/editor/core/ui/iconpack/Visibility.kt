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

val IconPack.Visibility: ImageVector
    get() {
        if (_visibility != null) {
            return _visibility!!
        }
        _visibility =
            Builder(
                name = "Visibility",
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
                    moveTo(12.0f, 16.0f)
                    curveTo(13.25f, 16.0f, 14.3127f, 15.5627f, 15.188f, 14.688f)
                    curveTo(16.0627f, 13.8127f, 16.5f, 12.75f, 16.5f, 11.5f)
                    curveTo(16.5f, 10.25f, 16.0627f, 9.1873f, 15.188f, 8.312f)
                    curveTo(14.3127f, 7.4373f, 13.25f, 7.0f, 12.0f, 7.0f)
                    curveTo(10.75f, 7.0f, 9.6873f, 7.4373f, 8.812f, 8.312f)
                    curveTo(7.9373f, 9.1873f, 7.5f, 10.25f, 7.5f, 11.5f)
                    curveTo(7.5f, 12.75f, 7.9373f, 13.8127f, 8.812f, 14.688f)
                    curveTo(9.6873f, 15.5627f, 10.75f, 16.0f, 12.0f, 16.0f)
                    close()
                    moveTo(12.0f, 14.2f)
                    curveTo(11.25f, 14.2f, 10.6127f, 13.9373f, 10.088f, 13.412f)
                    curveTo(9.5627f, 12.8873f, 9.3f, 12.25f, 9.3f, 11.5f)
                    curveTo(9.3f, 10.75f, 9.5627f, 10.1123f, 10.088f, 9.587f)
                    curveTo(10.6127f, 9.0623f, 11.25f, 8.8f, 12.0f, 8.8f)
                    curveTo(12.75f, 8.8f, 13.3877f, 9.0623f, 13.913f, 9.587f)
                    curveTo(14.4377f, 10.1123f, 14.7f, 10.75f, 14.7f, 11.5f)
                    curveTo(14.7f, 12.25f, 14.4377f, 12.8873f, 13.913f, 13.412f)
                    curveTo(13.3877f, 13.9373f, 12.75f, 14.2f, 12.0f, 14.2f)
                    close()
                    moveTo(12.0f, 19.0f)
                    curveTo(9.5667f, 19.0f, 7.35f, 18.3207f, 5.35f, 16.962f)
                    curveTo(3.35f, 15.604f, 1.9f, 13.7833f, 1.0f, 11.5f)
                    curveTo(1.9f, 9.2167f, 3.35f, 7.3957f, 5.35f, 6.037f)
                    curveTo(7.35f, 4.679f, 9.5667f, 4.0f, 12.0f, 4.0f)
                    curveTo(14.4333f, 4.0f, 16.65f, 4.679f, 18.65f, 6.037f)
                    curveTo(20.65f, 7.3957f, 22.1f, 9.2167f, 23.0f, 11.5f)
                    curveTo(22.1f, 13.7833f, 20.65f, 15.604f, 18.65f, 16.962f)
                    curveTo(16.65f, 18.3207f, 14.4333f, 19.0f, 12.0f, 19.0f)
                    close()
                }
            }.build()
        return _visibility!!
    }

private var _visibility: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Visibility.IconPreview()
