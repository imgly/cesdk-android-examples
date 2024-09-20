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

val IconPack.Joinround: ImageVector
    get() {
        if (_joinround != null) {
            return _joinround!!
        }
        _joinround =
            Builder(
                name = "Joinround",
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
                    pathFillType = EvenOdd,
                ) {
                    moveTo(3.0f, 6.5f)
                    verticalLineTo(3.0f)
                    horizontalLineTo(6.0f)
                    verticalLineTo(6.5f)
                    curveTo(6.0f, 12.8513f, 11.1487f, 18.0f, 17.5f, 18.0f)
                    horizontalLineTo(21.0f)
                    verticalLineTo(21.0f)
                    horizontalLineTo(17.5f)
                    curveTo(9.4919f, 21.0f, 3.0f, 14.5081f, 3.0f, 6.5f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF49454F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd,
                ) {
                    moveTo(8.0f, 5.5f)
                    verticalLineTo(3.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(5.5f)
                    curveTo(9.0f, 10.7467f, 13.2533f, 15.0f, 18.5f, 15.0f)
                    horizontalLineTo(21.0f)
                    verticalLineTo(16.0f)
                    horizontalLineTo(18.5f)
                    curveTo(12.701f, 16.0f, 8.0f, 11.299f, 8.0f, 5.5f)
                    close()
                }
            }.build()
        return _joinround!!
    }

private var _joinround: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Joinround.IconPreview()
