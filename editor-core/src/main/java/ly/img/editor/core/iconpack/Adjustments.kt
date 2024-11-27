package ly.img.editor.core.iconpack

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

val IconPack.Adjustments: ImageVector
    get() {
        if (_adjustments != null) {
            return _adjustments!!
        }
        _adjustments =
            Builder(
                name = "Adjustments",
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
                    moveTo(11.0f, 21.0f)
                    verticalLineTo(15.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(17.0f)
                    horizontalLineTo(21.0f)
                    verticalLineTo(19.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(21.0f)
                    horizontalLineTo(11.0f)
                    close()
                    moveTo(3.0f, 19.0f)
                    verticalLineTo(17.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(19.0f)
                    horizontalLineTo(3.0f)
                    close()
                    moveTo(7.0f, 15.0f)
                    verticalLineTo(13.0f)
                    horizontalLineTo(3.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(7.0f)
                    verticalLineTo(9.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(15.0f)
                    horizontalLineTo(7.0f)
                    close()
                    moveTo(11.0f, 13.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(21.0f)
                    verticalLineTo(13.0f)
                    horizontalLineTo(11.0f)
                    close()
                    moveTo(15.0f, 9.0f)
                    verticalLineTo(3.0f)
                    horizontalLineTo(17.0f)
                    verticalLineTo(5.0f)
                    horizontalLineTo(21.0f)
                    verticalLineTo(7.0f)
                    horizontalLineTo(17.0f)
                    verticalLineTo(9.0f)
                    horizontalLineTo(15.0f)
                    close()
                    moveTo(3.0f, 7.0f)
                    verticalLineTo(5.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(7.0f)
                    horizontalLineTo(3.0f)
                    close()
                }
            }.build()
        return _adjustments!!
    }

private var _adjustments: ImageVector? = null

@Preview
@Composable
private fun Preview() = IconPack.Adjustments.IconPreview()
