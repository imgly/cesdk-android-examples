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

val IconPack.Joinmiter: ImageVector
    get() {
        if (_joinmiter != null) {
            return _joinmiter!!
        }
        _joinmiter =
            Builder(
                name = "Joinmiter",
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
                    moveTo(3.0f, 3.0f)
                    verticalLineTo(18.5f)
                    curveTo(3.0f, 19.8807f, 4.1193f, 21.0f, 5.5f, 21.0f)
                    horizontalLineTo(21.0f)
                    verticalLineTo(18.0f)
                    horizontalLineTo(6.0f)
                    verticalLineTo(3.0f)
                    horizontalLineTo(3.0f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF49454F)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(9.0f, 3.0f)
                    horizontalLineTo(8.0f)
                    verticalLineTo(16.0f)
                    horizontalLineTo(21.0f)
                    verticalLineTo(15.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(3.0f)
                    close()
                }
            }.build()
        return _joinmiter!!
    }

private var _joinmiter: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Joinmiter.IconPreview()
