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

val IconPack.Filteradjustments: ImageVector
    get() {
        if (_filterAdjustments != null) {
            return _filterAdjustments!!
        }
        _filterAdjustments =
            Builder(
                name = "FilterAdjustments",
                defaultWidth = 32.0.dp,
                defaultHeight = 32.0.dp,
                viewportWidth = 32.0f,
                viewportHeight = 32.0f,
            ).apply {
                path(
                    fill = SolidColor(Color(0xFFffffff)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(10.666f, 17.3333f)
                    curveTo(8.186f, 17.3333f, 6.1193f, 19.04f, 5.5194f, 21.3333f)
                    horizontalLineTo(2.666f)
                    verticalLineTo(24.0f)
                    horizontalLineTo(5.5194f)
                    curveTo(6.1193f, 26.2933f, 8.186f, 28.0f, 10.666f, 28.0f)
                    curveTo(13.146f, 28.0f, 15.2127f, 26.2933f, 15.8127f, 24.0f)
                    horizontalLineTo(29.3327f)
                    verticalLineTo(21.3333f)
                    horizontalLineTo(15.8127f)
                    curveTo(15.2127f, 19.04f, 13.146f, 17.3333f, 10.666f, 17.3333f)
                    close()
                    moveTo(10.666f, 25.3333f)
                    curveTo(9.1994f, 25.3333f, 7.9994f, 24.1333f, 7.9994f, 22.6667f)
                    curveTo(7.9994f, 21.2f, 9.1994f, 20.0f, 10.666f, 20.0f)
                    curveTo(12.1327f, 20.0f, 13.3327f, 21.2f, 13.3327f, 22.6667f)
                    curveTo(13.3327f, 24.1333f, 12.1327f, 25.3333f, 10.666f, 25.3333f)
                    close()
                    moveTo(26.4794f, 8.0f)
                    curveTo(25.8794f, 5.7067f, 23.8127f, 4.0f, 21.3327f, 4.0f)
                    curveTo(18.8527f, 4.0f, 16.786f, 5.7067f, 16.186f, 8.0f)
                    horizontalLineTo(2.666f)
                    verticalLineTo(10.6667f)
                    horizontalLineTo(16.186f)
                    curveTo(16.786f, 12.96f, 18.8527f, 14.6667f, 21.3327f, 14.6667f)
                    curveTo(23.8127f, 14.6667f, 25.8794f, 12.96f, 26.4794f, 10.6667f)
                    horizontalLineTo(29.3327f)
                    verticalLineTo(8.0f)
                    horizontalLineTo(26.4794f)
                    close()
                    moveTo(21.3327f, 12.0f)
                    curveTo(19.866f, 12.0f, 18.666f, 10.8f, 18.666f, 9.3333f)
                    curveTo(18.666f, 7.8667f, 19.866f, 6.6667f, 21.3327f, 6.6667f)
                    curveTo(22.7993f, 6.6667f, 23.9993f, 7.8667f, 23.9993f, 9.3333f)
                    curveTo(23.9993f, 10.8f, 22.7993f, 12.0f, 21.3327f, 12.0f)
                    close()
                }
            }.build()
        return _filterAdjustments!!
    }

private var _filterAdjustments: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Filteradjustments.IconPreview()
