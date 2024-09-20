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

val IconPack.Search: ImageVector
    get() {
        if (_search != null) {
            return _search!!
        }
        _search =
            Builder(
                name = "Search",
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
                    moveTo(14.76f, 13.27f)
                    lineTo(20.49f, 19.0f)
                    lineTo(19.0f, 20.49f)
                    lineTo(13.27f, 14.76f)
                    curveTo(12.2f, 15.53f, 10.91f, 16.0f, 9.5f, 16.0f)
                    curveTo(5.91f, 16.0f, 3.0f, 13.09f, 3.0f, 9.5f)
                    curveTo(3.0f, 5.91f, 5.91f, 3.0f, 9.5f, 3.0f)
                    curveTo(13.09f, 3.0f, 16.0f, 5.91f, 16.0f, 9.5f)
                    curveTo(16.0f, 10.91f, 15.53f, 12.2f, 14.76f, 13.27f)
                    close()
                    moveTo(9.5f, 5.0f)
                    curveTo(7.01f, 5.0f, 5.0f, 7.01f, 5.0f, 9.5f)
                    curveTo(5.0f, 11.99f, 7.01f, 14.0f, 9.5f, 14.0f)
                    curveTo(11.99f, 14.0f, 14.0f, 11.99f, 14.0f, 9.5f)
                    curveTo(14.0f, 7.01f, 11.99f, 5.0f, 9.5f, 5.0f)
                    close()
                }
            }.build()
        return _search!!
    }

private var _search: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Search.IconPreview()
