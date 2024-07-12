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

val IconPack.None: ImageVector
    get() {
        if (_none != null) {
            return _none!!
        }
        _none =
            Builder(
                name = "None",
                defaultWidth = 56.0.dp,
                defaultHeight = 56.0.dp,
                viewportWidth = 56.0f,
                viewportHeight = 56.0f,
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
                    moveTo(27.9993f, 4.667f)
                    curveTo(40.8327f, 4.667f, 51.3327f, 15.167f, 51.3327f, 28.0003f)
                    curveTo(51.3327f, 40.8337f, 40.8327f, 51.3337f, 27.9993f, 51.3337f)
                    curveTo(15.166f, 51.3337f, 4.666f, 40.8337f, 4.666f, 28.0003f)
                    curveTo(4.666f, 15.167f, 15.166f, 4.667f, 27.9993f, 4.667f)
                    close()
                    moveTo(27.9993f, 9.3337f)
                    curveTo(23.566f, 9.3337f, 19.5993f, 10.7337f, 16.566f, 13.3003f)
                    lineTo(42.6993f, 39.4337f)
                    curveTo(45.0327f, 36.167f, 46.666f, 32.2003f, 46.666f, 28.0003f)
                    curveTo(46.666f, 17.7337f, 38.266f, 9.3337f, 27.9993f, 9.3337f)
                    close()
                    moveTo(39.4327f, 42.7003f)
                    lineTo(13.2993f, 16.567f)
                    curveTo(10.7327f, 19.6003f, 9.3327f, 23.567f, 9.3327f, 28.0003f)
                    curveTo(9.3327f, 38.267f, 17.7327f, 46.667f, 27.9993f, 46.667f)
                    curveTo(32.4327f, 46.667f, 36.3993f, 45.267f, 39.4327f, 42.7003f)
                    close()
                }
            }.build()
        return _none!!
    }

private var _none: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.None.IconPreview()
