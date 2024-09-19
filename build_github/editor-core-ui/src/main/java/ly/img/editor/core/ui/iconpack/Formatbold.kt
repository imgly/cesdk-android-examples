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

val IconPack.Formatbold: ImageVector
    get() {
        if (_formatbold != null) {
            return _formatbold!!
        }
        _formatbold =
            Builder(
                name = "Formatbold",
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
                    moveTo(7.0f, 18.0f)
                    verticalLineTo(4.0f)
                    horizontalLineTo(12.5f)
                    curveTo(13.6333f, 4.0f, 14.625f, 4.3417f, 15.475f, 5.025f)
                    curveTo(16.325f, 5.7083f, 16.75f, 6.5833f, 16.75f, 7.65f)
                    curveTo(16.75f, 8.2833f, 16.5833f, 8.8707f, 16.25f, 9.412f)
                    curveTo(15.9167f, 9.954f, 15.4667f, 10.3667f, 14.9f, 10.65f)
                    verticalLineTo(10.85f)
                    curveTo(15.6f, 11.0833f, 16.1667f, 11.4957f, 16.6f, 12.087f)
                    curveTo(17.0333f, 12.679f, 17.25f, 13.35f, 17.25f, 14.1f)
                    curveTo(17.25f, 15.25f, 16.7877f, 16.1877f, 15.863f, 16.913f)
                    curveTo(14.9377f, 17.6377f, 13.8667f, 18.0f, 12.65f, 18.0f)
                    horizontalLineTo(7.0f)
                    close()
                    moveTo(9.65f, 9.7f)
                    horizontalLineTo(12.3f)
                    curveTo(12.8f, 9.7f, 13.2417f, 9.5417f, 13.625f, 9.225f)
                    curveTo(14.0083f, 8.9083f, 14.2f, 8.5083f, 14.2f, 8.025f)
                    curveTo(14.2f, 7.5417f, 14.0083f, 7.1417f, 13.625f, 6.825f)
                    curveTo(13.2417f, 6.5083f, 12.8f, 6.35f, 12.3f, 6.35f)
                    horizontalLineTo(9.65f)
                    verticalLineTo(9.7f)
                    close()
                    moveTo(9.65f, 15.6f)
                    horizontalLineTo(12.5f)
                    curveTo(13.0667f, 15.6f, 13.5583f, 15.429f, 13.975f, 15.087f)
                    curveTo(14.3917f, 14.7457f, 14.6f, 14.3083f, 14.6f, 13.775f)
                    curveTo(14.6f, 13.2417f, 14.3917f, 12.804f, 13.975f, 12.462f)
                    curveTo(13.5583f, 12.1207f, 13.0667f, 11.95f, 12.5f, 11.95f)
                    horizontalLineTo(9.65f)
                    verticalLineTo(15.6f)
                    close()
                }
            }.build()
        return _formatbold!!
    }

private var _formatbold: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Formatbold.IconPreview()
