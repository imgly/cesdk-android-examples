package ly.img.cesdk.core.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val IconPack.Cloudupload: ImageVector
    get() {
        if (_cloudupload != null) {
            return _cloudupload!!
        }
        _cloudupload = Builder(
            name = "Cloudupload", defaultWidth = 24.0.dp, defaultHeight =
            24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF49454F)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(11.0f, 20.0f)
                horizontalLineTo(6.5f)
                curveTo(4.98f, 20.0f, 3.6833f, 19.4767f, 2.61f, 18.43f)
                curveTo(1.5367f, 17.3767f, 1.0f, 16.0933f, 1.0f, 14.58f)
                curveTo(1.0f, 13.28f, 1.39f, 12.12f, 2.17f, 11.1f)
                curveTo(2.9567f, 10.08f, 3.9833f, 9.43f, 5.25f, 9.15f)
                curveTo(5.67f, 7.6167f, 6.5033f, 6.3767f, 7.75f, 5.43f)
                curveTo(9.0033f, 4.4767f, 10.42f, 4.0f, 12.0f, 4.0f)
                curveTo(13.9533f, 4.0f, 15.6067f, 4.68f, 16.96f, 6.04f)
                curveTo(18.32f, 7.3933f, 19.0f, 9.0467f, 19.0f, 11.0f)
                curveTo(20.1533f, 11.1333f, 21.1067f, 11.6333f, 21.86f, 12.5f)
                curveTo(22.62f, 13.3533f, 23.0f, 14.3533f, 23.0f, 15.5f)
                curveTo(23.0f, 16.7533f, 22.5633f, 17.8167f, 21.69f, 18.69f)
                curveTo(20.8167f, 19.5633f, 19.7533f, 20.0f, 18.5f, 20.0f)
                horizontalLineTo(13.0f)
                verticalLineTo(12.85f)
                lineTo(14.6f, 14.4f)
                lineTo(16.0f, 13.0f)
                lineTo(12.0f, 9.0f)
                lineTo(8.0f, 13.0f)
                lineTo(9.4f, 14.4f)
                lineTo(11.0f, 12.85f)
                verticalLineTo(20.0f)
                close()
            }
        }
            .build()
        return _cloudupload!!
    }

private var _cloudupload: ImageVector? = null
