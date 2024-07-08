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

val IconPack.NavigateBefore: ImageVector
    get() {
        if (`_navigate-before` != null) {
            return `_navigate-before`!!
        }
        `_navigate-before` =
            Builder(
                name = "Navigate-before",
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
                    moveTo(15.7049f, 7.41f)
                    lineTo(14.2949f, 6.0f)
                    lineTo(8.2949f, 12.0f)
                    lineTo(14.2949f, 18.0f)
                    lineTo(15.7049f, 16.59f)
                    lineTo(11.1249f, 12.0f)
                    lineTo(15.7049f, 7.41f)
                    close()
                }
            }.build()
        return `_navigate-before`!!
    }

private var `_navigate-before`: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.NavigateBefore
