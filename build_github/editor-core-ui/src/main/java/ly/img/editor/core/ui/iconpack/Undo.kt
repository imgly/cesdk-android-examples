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

val IconPack.Undo: ImageVector
    get() {
        if (_undo != null) {
            return _undo!!
        }
        _undo =
            Builder(
                name = "Undo",
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
                    moveTo(14.1f, 19.0f)
                    horizontalLineTo(7.0f)
                    verticalLineTo(17.0f)
                    horizontalLineTo(14.1f)
                    curveTo(15.15f, 17.0f, 16.0623f, 16.6667f, 16.837f, 16.0f)
                    curveTo(17.6123f, 15.3333f, 18.0f, 14.5f, 18.0f, 13.5f)
                    curveTo(18.0f, 12.5f, 17.6123f, 11.6667f, 16.837f, 11.0f)
                    curveTo(16.0623f, 10.3333f, 15.15f, 10.0f, 14.1f, 10.0f)
                    horizontalLineTo(7.8f)
                    lineTo(10.4f, 12.6f)
                    lineTo(9.0f, 14.0f)
                    lineTo(4.0f, 9.0f)
                    lineTo(9.0f, 4.0f)
                    lineTo(10.4f, 5.4f)
                    lineTo(7.8f, 8.0f)
                    horizontalLineTo(14.1f)
                    curveTo(15.7167f, 8.0f, 17.1043f, 8.525f, 18.263f, 9.575f)
                    curveTo(19.421f, 10.625f, 20.0f, 11.9333f, 20.0f, 13.5f)
                    curveTo(20.0f, 15.0667f, 19.421f, 16.375f, 18.263f, 17.425f)
                    curveTo(17.1043f, 18.475f, 15.7167f, 19.0f, 14.1f, 19.0f)
                    close()
                }
            }.build()
        return _undo!!
    }

private var _undo: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Undo.IconPreview()
