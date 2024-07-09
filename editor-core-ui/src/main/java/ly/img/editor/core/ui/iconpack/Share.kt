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

val IconPack.Share: ImageVector
    get() {
        if (_share != null) {
            return _share!!
        }
        _share =
            Builder(
                name = "Share",
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
                    moveTo(15.97f, 16.81f)
                    curveTo(16.5f, 16.31f, 17.21f, 16.0f, 18.0f, 16.0f)
                    curveTo(19.66f, 16.0f, 21.0f, 17.34f, 21.0f, 19.0f)
                    curveTo(21.0f, 20.66f, 19.66f, 22.0f, 18.0f, 22.0f)
                    curveTo(16.34f, 22.0f, 15.0f, 20.66f, 15.0f, 19.0f)
                    curveTo(15.0f, 18.76f, 15.04f, 18.53f, 15.09f, 18.31f)
                    lineTo(8.04f, 14.19f)
                    curveTo(7.5f, 14.69f, 6.79f, 15.0f, 6.0f, 15.0f)
                    curveTo(4.34f, 15.0f, 3.0f, 13.66f, 3.0f, 12.0f)
                    curveTo(3.0f, 10.34f, 4.34f, 9.0f, 6.0f, 9.0f)
                    curveTo(6.79f, 9.0f, 7.5f, 9.31f, 8.04f, 9.81f)
                    lineTo(15.09f, 5.7f)
                    curveTo(15.04f, 5.48f, 15.0f, 5.24f, 15.0f, 5.0f)
                    curveTo(15.0f, 3.34f, 16.34f, 2.0f, 18.0f, 2.0f)
                    curveTo(19.66f, 2.0f, 21.0f, 3.34f, 21.0f, 5.0f)
                    curveTo(21.0f, 6.66f, 19.66f, 8.0f, 18.0f, 8.0f)
                    curveTo(17.21f, 8.0f, 16.49f, 7.69f, 15.96f, 7.19f)
                    lineTo(8.91f, 11.3f)
                    curveTo(8.96f, 11.53f, 9.0f, 11.76f, 9.0f, 12.0f)
                    curveTo(9.0f, 12.24f, 8.96f, 12.47f, 8.91f, 12.7f)
                    lineTo(15.97f, 16.81f)
                    close()
                    moveTo(19.0f, 5.0f)
                    curveTo(19.0f, 4.45f, 18.55f, 4.0f, 18.0f, 4.0f)
                    curveTo(17.45f, 4.0f, 17.0f, 4.45f, 17.0f, 5.0f)
                    curveTo(17.0f, 5.55f, 17.45f, 6.0f, 18.0f, 6.0f)
                    curveTo(18.55f, 6.0f, 19.0f, 5.55f, 19.0f, 5.0f)
                    close()
                    moveTo(6.0f, 13.0f)
                    curveTo(5.45f, 13.0f, 5.0f, 12.55f, 5.0f, 12.0f)
                    curveTo(5.0f, 11.45f, 5.45f, 11.0f, 6.0f, 11.0f)
                    curveTo(6.55f, 11.0f, 7.0f, 11.45f, 7.0f, 12.0f)
                    curveTo(7.0f, 12.55f, 6.55f, 13.0f, 6.0f, 13.0f)
                    close()
                    moveTo(17.0f, 19.0f)
                    curveTo(17.0f, 19.55f, 17.45f, 20.0f, 18.0f, 20.0f)
                    curveTo(18.55f, 20.0f, 19.0f, 19.55f, 19.0f, 19.0f)
                    curveTo(19.0f, 18.45f, 18.55f, 18.0f, 18.0f, 18.0f)
                    curveTo(17.45f, 18.0f, 17.0f, 18.45f, 17.0f, 19.0f)
                    close()
                }
            }.build()
        return _share!!
    }

private var _share: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Share.IconPreview()
