package ly.img.editor.guides.icons.brandicons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ly.img.editor.guides.icons.BrandIcons

public val BrandIcons.BrandSpark: ImageVector
    get() {
        if (brandSpark != null) {
            return brandSpark!!
        }
        brandSpark = Builder(
            name = "BrandSpark",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
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
                moveTo(12.0f, 2.0f)
                lineToRelative(2.6f, 6.4f)
                lineTo(21.0f, 11.0f)
                lineToRelative(-6.4f, 2.6f)
                lineTo(12.0f, 20.0f)
                lineToRelative(-2.6f, -6.4f)
                lineTo(3.0f, 11.0f)
                lineToRelative(6.4f, -2.6f)
                lineTo(12.0f, 2.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF46464F)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero,
            ) {
                moveTo(18.0f, 16.0f)
                lineToRelative(0.9f, 2.1f)
                lineTo(21.0f, 19.0f)
                lineToRelative(-2.1f, 0.9f)
                lineTo(18.0f, 22.0f)
                lineToRelative(-0.9f, -2.1f)
                lineTo(15.0f, 19.0f)
                lineToRelative(2.1f, -0.9f)
                lineTo(18.0f, 16.0f)
                close()
            }
        }
            .build()
        return brandSpark!!
    }

private var brandSpark: ImageVector? = null
