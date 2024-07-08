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

val IconPack.Arrowrightbig: ImageVector
    get() {
        if (_arrowrightbig != null) {
            return _arrowrightbig!!
        }
        _arrowrightbig =
            Builder(
                name = "Arrowrightbig",
                defaultWidth = 24.0.dp,
                defaultHeight =
                    24.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 24.0f,
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
                    moveTo(4.0f, 11.0001f)
                    verticalLineTo(13.0001f)
                    horizontalLineTo(16.0f)
                    lineTo(10.5f, 18.5001f)
                    lineTo(11.92f, 19.9201f)
                    lineTo(19.84f, 12.0001f)
                    lineTo(11.92f, 4.0801f)
                    lineTo(10.5f, 5.5001f)
                    lineTo(16.0f, 11.0001f)
                    horizontalLineTo(4.0f)
                    close()
                }
            }.build()
        return _arrowrightbig!!
    }

private var _arrowrightbig: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Arrowrightbig.IconPreview()
