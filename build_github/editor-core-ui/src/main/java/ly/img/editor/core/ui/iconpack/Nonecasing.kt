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
import androidx.compose.ui.unit.dp

public val IconPack.Nonecasing: ImageVector
    get() {
        if (_nonecasing != null) {
            return _nonecasing!!
        }
        _nonecasing =
            Builder(
                name = "Nonecasing",
                defaultWidth = 24.0.dp,
                defaultHeight = 25.0.dp,
                viewportWidth = 24.0f,
                viewportHeight = 25.0f,
            ).apply {
                path(
                    fill = SolidColor(Color(0xFFffffff)),
                    stroke = null,
                    strokeLineWidth = 0.0f,
                    strokeLineCap = Butt,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(7.0f, 11.5f)
                    horizontalLineToRelative(10.0f)
                    verticalLineToRelative(2.0f)
                    horizontalLineToRelative(-10.0f)
                    close()
                }
            }.build()
        return _nonecasing!!
    }

private var _nonecasing: ImageVector? = null

@Composable
private fun Preview() = IconPack.Nonecasing.IconPreview()
