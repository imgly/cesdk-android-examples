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

val IconPack.PageNextArrow: ImageVector
    get() {
        if (_pageNextArrow != null) {
            return _pageNextArrow!!
        }
        _pageNextArrow =
            Builder(
                name = "PageNextArrow",
                defaultWidth = 20.0.dp,
                defaultHeight =
                    21.0.dp,
                viewportWidth = 20.0f,
                viewportHeight = 21.0f,
            ).apply {
                path(
                    fill = SolidColor(Color(0xFF000000)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(10.0f, 20.4102f)
                    curveTo(4.5606f, 20.4102f, 0.0391f, 15.8887f, 0.0391f, 10.4492f)
                    curveTo(0.0391f, 5.0f, 4.5508f, 0.4883f, 9.9902f, 0.4883f)
                    curveTo(15.4395f, 0.4883f, 19.9609f, 5.0f, 19.9609f, 10.4492f)
                    curveTo(19.9609f, 15.8887f, 15.4492f, 20.4102f, 10.0f, 20.4102f)
                    close()
                    moveTo(10.0f, 18.75f)
                    curveTo(14.6094f, 18.75f, 18.3008f, 15.0586f, 18.3008f, 10.4492f)
                    curveTo(18.3008f, 5.8398f, 14.5996f, 2.1484f, 9.9902f, 2.1484f)
                    curveTo(5.3809f, 2.1484f, 1.709f, 5.8398f, 1.709f, 10.4492f)
                    curveTo(1.709f, 15.0586f, 5.3906f, 18.75f, 10.0f, 18.75f)
                    close()
                    moveTo(7.7734f, 15.2832f)
                    curveTo(7.4707f, 14.9805f, 7.4707f, 14.5117f, 7.7637f, 14.2383f)
                    lineTo(11.7773f, 10.4492f)
                    lineTo(7.7637f, 6.6797f)
                    curveTo(7.4609f, 6.4063f, 7.4707f, 5.918f, 7.7832f, 5.6348f)
                    curveTo(8.0566f, 5.3711f, 8.5059f, 5.3809f, 8.8086f, 5.6641f)
                    lineTo(12.8711f, 9.502f)
                    curveTo(13.4277f, 10.0098f, 13.4277f, 10.9082f, 12.8711f, 11.416f)
                    lineTo(8.8086f, 15.2539f)
                    curveTo(8.5352f, 15.5078f, 8.0273f, 15.5273f, 7.7734f, 15.2832f)
                    close()
                }
            }.build()
        return _pageNextArrow!!
    }

private var _pageNextArrow: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.PageNextArrow.IconPreview()
