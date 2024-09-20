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

val IconPack.NavigateNext: ImageVector
    get() {
        if (`_navigate-next` != null) {
            return `_navigate-next`!!
        }
        `_navigate-next` =
            Builder(
                name = "Navigate-next",
                defaultWidth = 24.0.dp,
                defaultHeight =
                    24.0.dp,
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
                    pathFillType = NonZero,
                ) {
                    moveTo(9.7049f, 6.0f)
                    lineTo(8.2949f, 7.41f)
                    lineTo(12.8749f, 12.0f)
                    lineTo(8.2949f, 16.59f)
                    lineTo(9.7049f, 18.0f)
                    lineTo(15.7049f, 12.0f)
                    lineTo(9.7049f, 6.0f)
                    close()
                }
            }.build()
        return `_navigate-next`!!
    }

private var `_navigate-next`: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.NavigateNext.IconPreview()
