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

val IconPack.Expandmore: ImageVector
    get() {
        if (_expandmore != null) {
            return _expandmore!!
        }
        _expandmore =
            Builder(
                name = "Expandmore",
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
                    pathFillType = NonZero,
                ) {
                    moveTo(12.0f, 15.3751f)
                    lineTo(6.0f, 9.3751f)
                    lineTo(7.4f, 7.9751f)
                    lineTo(12.0f, 12.5751f)
                    lineTo(16.6f, 7.9751f)
                    lineTo(18.0f, 9.3751f)
                    lineTo(12.0f, 15.3751f)
                    close()
                }
            }.build()
        return _expandmore!!
    }

private var _expandmore: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Expandmore.IconPreview()
