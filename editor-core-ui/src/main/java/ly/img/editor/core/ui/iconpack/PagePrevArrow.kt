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

val IconPack.PagePrevArrow: ImageVector
    get() {
        if (_pagePrevArrow != null) {
            return _pagePrevArrow!!
        }
        _pagePrevArrow =
            Builder(
                name = "PagePrevArrow",
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
                    moveTo(10.0f, 20.9102f)
                    curveTo(4.5606f, 20.9102f, 0.0391f, 16.3887f, 0.0391f, 10.9492f)
                    curveTo(0.0391f, 5.5f, 4.5508f, 0.9883f, 9.9902f, 0.9883f)
                    curveTo(15.4395f, 0.9883f, 19.9609f, 5.5f, 19.9609f, 10.9492f)
                    curveTo(19.9609f, 16.3887f, 15.4492f, 20.9102f, 10.0f, 20.9102f)
                    close()
                    moveTo(10.0f, 19.25f)
                    curveTo(14.6094f, 19.25f, 18.3008f, 15.5586f, 18.3008f, 10.9492f)
                    curveTo(18.3008f, 6.3398f, 14.5996f, 2.6484f, 9.9902f, 2.6484f)
                    curveTo(5.3809f, 2.6484f, 1.709f, 6.3398f, 1.709f, 10.9492f)
                    curveTo(1.709f, 15.5586f, 5.3906f, 19.25f, 10.0f, 19.25f)
                    close()
                    moveTo(11.9238f, 15.7832f)
                    curveTo(11.6699f, 16.0273f, 11.1621f, 16.0078f, 10.8887f, 15.7539f)
                    lineTo(6.8262f, 11.916f)
                    curveTo(6.2695f, 11.4082f, 6.2695f, 10.5098f, 6.8262f, 10.002f)
                    lineTo(10.8887f, 6.1641f)
                    curveTo(11.1914f, 5.8809f, 11.6406f, 5.8711f, 11.9141f, 6.1348f)
                    curveTo(12.2168f, 6.418f, 12.2266f, 6.9063f, 11.9336f, 7.1797f)
                    lineTo(7.9199f, 10.9492f)
                    lineTo(11.9336f, 14.7383f)
                    curveTo(12.2168f, 15.0117f, 12.2266f, 15.4805f, 11.9238f, 15.7832f)
                    close()
                }
            }.build()
        return _pagePrevArrow!!
    }

private var _pagePrevArrow: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.PagePrevArrow.IconPreview()
