package ly.img.editor.core.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Deprecated("Use IconPack.StickerEmojiOutline instead.", ReplaceWith("IconPack.StickerEmojiOutline"))
val IconPack.Stickeremojioutline: ImageVector
    get() = StickerEmojiOutline

val IconPack.StickerEmojiOutline: ImageVector
    get() {
        if (_stickeremojioutline != null) {
            return _stickeremojioutline!!
        }
        _stickeremojioutline =
            Builder(
                name = "Stickeremojioutline",
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
                    moveTo(5.5f, 2.0f)
                    curveTo(3.56f, 2.0f, 2.0f, 3.56f, 2.0f, 5.5f)
                    verticalLineTo(18.5f)
                    curveTo(2.0f, 20.44f, 3.56f, 22.0f, 5.5f, 22.0f)
                    horizontalLineTo(16.0f)
                    lineTo(22.0f, 16.0f)
                    verticalLineTo(5.5f)
                    curveTo(22.0f, 3.56f, 20.44f, 2.0f, 18.5f, 2.0f)
                    horizontalLineTo(5.5f)
                    close()
                    moveTo(5.75f, 4.0f)
                    horizontalLineTo(18.25f)
                    curveTo(18.7141f, 4.0f, 19.1592f, 4.1844f, 19.4874f, 4.5126f)
                    curveTo(19.8156f, 4.8408f, 20.0f, 5.2859f, 20.0f, 5.75f)
                    verticalLineTo(15.0f)
                    horizontalLineTo(18.5f)
                    curveTo(16.56f, 15.0f, 15.0f, 16.56f, 15.0f, 18.5f)
                    verticalLineTo(20.0f)
                    horizontalLineTo(5.75f)
                    curveTo(5.2859f, 20.0f, 4.8408f, 19.8156f, 4.5126f, 19.4874f)
                    curveTo(4.1844f, 19.1592f, 4.0f, 18.7141f, 4.0f, 18.25f)
                    verticalLineTo(5.75f)
                    curveTo(4.0f, 5.2859f, 4.1844f, 4.8408f, 4.5126f, 4.5126f)
                    curveTo(4.8408f, 4.1844f, 5.2859f, 4.0f, 5.75f, 4.0f)
                    close()
                    moveTo(14.44f, 6.77f)
                    curveTo(14.28f, 6.77f, 14.12f, 6.79f, 13.97f, 6.83f)
                    curveTo(13.03f, 7.09f, 12.5f, 8.05f, 12.74f, 9.0f)
                    curveTo(12.79f, 9.15f, 12.86f, 9.3f, 12.95f, 9.44f)
                    lineTo(16.18f, 8.56f)
                    curveTo(16.18f, 8.39f, 16.16f, 8.22f, 16.12f, 8.05f)
                    curveTo(15.91f, 7.3f, 15.22f, 6.77f, 14.44f, 6.77f)
                    close()
                    moveTo(8.17f, 8.5f)
                    curveTo(8.0f, 8.5f, 7.85f, 8.5f, 7.7f, 8.55f)
                    curveTo(6.77f, 8.81f, 6.22f, 9.77f, 6.47f, 10.7f)
                    curveTo(6.5f, 10.86f, 6.59f, 11.0f, 6.68f, 11.16f)
                    lineTo(9.91f, 10.28f)
                    curveTo(9.91f, 10.11f, 9.89f, 9.94f, 9.85f, 9.78f)
                    curveTo(9.7522f, 9.4102f, 9.5339f, 9.0836f, 9.2296f, 8.8518f)
                    curveTo(8.9253f, 8.62f, 8.5525f, 8.4962f, 8.17f, 8.5f)
                    close()
                    moveTo(16.72f, 11.26f)
                    lineTo(7.59f, 13.77f)
                    curveTo(8.2384f, 14.5203f, 9.0854f, 15.0723f, 10.0337f, 15.3624f)
                    curveTo(10.9819f, 15.6525f, 11.9928f, 15.669f, 12.95f, 15.41f)
                    curveTo(13.9051f, 15.1428f, 14.7654f, 14.6117f, 15.4323f, 13.8776f)
                    curveTo(16.0991f, 13.1435f, 16.5455f, 12.2363f, 16.72f, 11.26f)
                    close()
                }
            }
                .build()
        return _stickeremojioutline!!
    }

private var _stickeremojioutline: ImageVector? = null
