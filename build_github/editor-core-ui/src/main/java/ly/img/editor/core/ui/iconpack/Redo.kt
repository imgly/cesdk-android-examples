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

val IconPack.Redo: ImageVector
    get() {
        if (_redo != null) {
            return _redo!!
        }
        _redo =
            Builder(
                name = "Redo",
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
                    moveTo(9.9f, 19.0f)
                    curveTo(8.2833f, 19.0f, 6.896f, 18.475f, 5.738f, 17.425f)
                    curveTo(4.5793f, 16.375f, 4.0f, 15.0667f, 4.0f, 13.5f)
                    curveTo(4.0f, 11.9333f, 4.5793f, 10.625f, 5.738f, 9.575f)
                    curveTo(6.896f, 8.525f, 8.2833f, 8.0f, 9.9f, 8.0f)
                    horizontalLineTo(16.2f)
                    lineTo(13.6f, 5.4f)
                    lineTo(15.0f, 4.0f)
                    lineTo(20.0f, 9.0f)
                    lineTo(15.0f, 14.0f)
                    lineTo(13.6f, 12.6f)
                    lineTo(16.2f, 10.0f)
                    horizontalLineTo(9.9f)
                    curveTo(8.85f, 10.0f, 7.9377f, 10.3333f, 7.163f, 11.0f)
                    curveTo(6.3877f, 11.6667f, 6.0f, 12.5f, 6.0f, 13.5f)
                    curveTo(6.0f, 14.5f, 6.3877f, 15.3333f, 7.163f, 16.0f)
                    curveTo(7.9377f, 16.6667f, 8.85f, 17.0f, 9.9f, 17.0f)
                    horizontalLineTo(17.0f)
                    verticalLineTo(19.0f)
                    horizontalLineTo(9.9f)
                    close()
                }
            }.build()
        return _redo!!
    }

private var _redo: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Redo.IconPreview()
