package ly.img.editor.core.ui.iconpack

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val IconPack.Cancel: ImageVector
    get() {
        if (_cancel != null) {
            return _cancel!!
        }
        _cancel =
            Builder(
                name = "Cancel",
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
                    pathFillType = EvenOdd,
                ) {
                    moveTo(12.0f, 2.0f)
                    curveTo(6.47f, 2.0f, 2.0f, 6.47f, 2.0f, 12.0f)
                    curveTo(2.0f, 17.53f, 6.47f, 22.0f, 12.0f, 22.0f)
                    curveTo(17.53f, 22.0f, 22.0f, 17.53f, 22.0f, 12.0f)
                    curveTo(22.0f, 6.47f, 17.53f, 2.0f, 12.0f, 2.0f)
                    close()
                    moveTo(12.0f, 20.0f)
                    curveTo(7.59f, 20.0f, 4.0f, 16.41f, 4.0f, 12.0f)
                    curveTo(4.0f, 7.59f, 7.59f, 4.0f, 12.0f, 4.0f)
                    curveTo(16.41f, 4.0f, 20.0f, 7.59f, 20.0f, 12.0f)
                    curveTo(20.0f, 16.41f, 16.41f, 20.0f, 12.0f, 20.0f)
                    close()
                    moveTo(12.0f, 10.59f)
                    lineTo(15.59f, 7.0f)
                    lineTo(17.0f, 8.41f)
                    lineTo(13.41f, 12.0f)
                    lineTo(17.0f, 15.59f)
                    lineTo(15.59f, 17.0f)
                    lineTo(12.0f, 13.41f)
                    lineTo(8.41f, 17.0f)
                    lineTo(7.0f, 15.59f)
                    lineTo(10.59f, 12.0f)
                    lineTo(7.0f, 8.41f)
                    lineTo(8.41f, 7.0f)
                    lineTo(12.0f, 10.59f)
                    close()
                }
            }.build()
        return _cancel!!
    }

private var _cancel: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Cancel.IconPreview()
