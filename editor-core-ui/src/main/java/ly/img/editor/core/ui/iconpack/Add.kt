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

val IconPack.Add: ImageVector
    get() {
        if (_add != null) {
            return _add!!
        }
        _add =
            Builder(
                name = "Add",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth =
                24.0f,
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
                    moveTo(20.0f, 13.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(13.0f)
                    horizontalLineTo(4.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(4.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(20.0f)
                    verticalLineTo(13.0f)
                    close()
                }
            }.build()
        return _add!!
    }

private var _add: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Add.IconPreview()
