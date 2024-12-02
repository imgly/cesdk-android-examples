package ly.img.editor.core.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Deprecated("Use IconPack.StickerEmoji instead.", ReplaceWith("IconPack.StickerEmoji"))
val IconPack.Stickeremoji: ImageVector
    get() = StickerEmoji

val IconPack.StickerEmoji: ImageVector
    get() {
        if (_stickeremoji != null) {
            return _stickeremoji!!
        }
        _stickeremoji =
            Builder(
                name = "Stickeremoji",
                defaultWidth = 24.0.dp,
                defaultHeight =
                    24.0.dp,
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
                    pathFillType = EvenOdd,
                ) {
                    moveTo(5.5f, 2.0f)
                    horizontalLineTo(18.5f)
                    curveTo(20.4f, 2.0f, 22.0f, 3.6f, 22.0f, 5.5f)
                    verticalLineTo(16.0f)
                    lineTo(16.0f, 22.0f)
                    horizontalLineTo(5.5f)
                    curveTo(3.6f, 22.0f, 2.0f, 20.4f, 2.0f, 18.5f)
                    verticalLineTo(5.5f)
                    curveTo(2.0f, 3.6f, 3.6f, 2.0f, 5.5f, 2.0f)
                    close()
                    moveTo(15.0f, 18.5f)
                    verticalLineTo(20.0f)
                    lineTo(20.0f, 15.0f)
                    horizontalLineTo(18.5f)
                    curveTo(16.6f, 15.0f, 15.0f, 16.6f, 15.0f, 18.5f)
                    close()
                    moveTo(13.97f, 6.83f)
                    curveTo(14.12f, 6.79f, 14.28f, 6.77f, 14.44f, 6.77f)
                    curveTo(15.22f, 6.77f, 15.91f, 7.3f, 16.12f, 8.05f)
                    curveTo(16.16f, 8.22f, 16.18f, 8.39f, 16.18f, 8.56f)
                    lineTo(12.95f, 9.44f)
                    curveTo(12.86f, 9.3f, 12.79f, 9.15f, 12.74f, 9.0f)
                    curveTo(12.5f, 8.05f, 13.03f, 7.09f, 13.97f, 6.83f)
                    close()
                    moveTo(7.7f, 8.55f)
                    curveTo(7.85f, 8.5f, 8.0f, 8.5f, 8.17f, 8.5f)
                    curveTo(8.5525f, 8.4962f, 8.9253f, 8.62f, 9.2296f, 8.8518f)
                    curveTo(9.5339f, 9.0836f, 9.7522f, 9.4102f, 9.85f, 9.78f)
                    curveTo(9.89f, 9.94f, 9.91f, 10.11f, 9.91f, 10.28f)
                    lineTo(6.68f, 11.16f)
                    curveTo(6.6676f, 11.138f, 6.6552f, 11.1164f, 6.643f, 11.095f)
                    lineTo(6.643f, 11.0949f)
                    lineTo(6.6429f, 11.0948f)
                    curveTo(6.5663f, 10.961f, 6.4959f, 10.8379f, 6.47f, 10.7f)
                    curveTo(6.22f, 9.77f, 6.77f, 8.81f, 7.7f, 8.55f)
                    close()
                    moveTo(16.72f, 11.26f)
                    lineTo(7.59f, 13.77f)
                    curveTo(8.2384f, 14.5203f, 9.0854f, 15.0723f, 10.0337f, 15.3624f)
                    curveTo(10.9819f, 15.6526f, 11.9928f, 15.6691f, 12.95f, 15.41f)
                    curveTo(13.9051f, 15.1428f, 14.7654f, 14.6117f, 15.4323f, 13.8776f)
                    curveTo(16.0991f, 13.1435f, 16.5455f, 12.2363f, 16.72f, 11.26f)
                    close()
                }
            }
                .build()
        return _stickeremoji!!
    }

private var _stickeremoji: ImageVector? = null
