package ly.img.editor.configuration.memories.iconPack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ly.img.editor.core.iconpack.IconPack

// Material Symbols Rounded "touch_triple" (filled) — used for the multi-select toggle.
val IconPack.TouchTriple: ImageVector
    get() {
        if (touchTriple != null) return touchTriple!!
        touchTriple = ImageVector.Builder(
            name = "touch_triple",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Bevel,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero,
            ) {
                moveTo(19.95f, 9f)
                quadTo(19.53f, 9f, 19.25f, 8.71f)
                quadTo(18.98f, 8.42f, 18.98f, 8f)
                quadToRelative(0f, -0.1f, 0.13f, -0.5f)
                quadTo(19.3f, 7.15f, 19.4f, 6.77f)
                reflectiveQuadTo(19.5f, 6f)
                quadToRelative(0f, -0.5f, -0.14f, -0.99f)
                reflectiveQuadTo(18.88f, 4.15f)
                quadTo(18.73f, 3.97f, 18.63f, 3.76f)
                quadTo(18.53f, 3.55f, 18.53f, 3.32f)
                quadToRelative(0f, -0.38f, 0.26f, -0.65f)
                reflectiveQuadTo(19.43f, 2.4f)
                quadToRelative(0.32f, 0f, 0.59f, 0.16f)
                quadToRelative(0.26f, 0.16f, 0.46f, 0.41f)
                quadToRelative(0.55f, 0.63f, 0.79f, 1.41f)
                reflectiveQuadTo(21.5f, 6f)
                quadToRelative(0f, 0.65f, -0.16f, 1.29f)
                quadTo(21.18f, 7.93f, 20.83f, 8.5f)
                quadTo(20.7f, 8.73f, 20.46f, 8.86f)
                reflectiveQuadTo(19.95f, 9f)
                close()
                moveToRelative(-4f, 0f)
                quadTo(15.53f, 9f, 15.25f, 8.71f)
                reflectiveQuadTo(14.98f, 8f)
                quadToRelative(0f, -0.1f, 0.13f, -0.5f)
                quadTo(15.3f, 7.15f, 15.4f, 6.77f)
                reflectiveQuadTo(15.5f, 6f)
                quadToRelative(0f, -0.5f, -0.14f, -0.99f)
                reflectiveQuadTo(14.88f, 4.15f)
                quadTo(14.73f, 3.97f, 14.63f, 3.76f)
                quadTo(14.53f, 3.55f, 14.53f, 3.32f)
                quadToRelative(0f, -0.38f, 0.26f, -0.65f)
                reflectiveQuadTo(15.43f, 2.4f)
                quadToRelative(0.32f, 0f, 0.59f, 0.16f)
                quadToRelative(0.26f, 0.16f, 0.46f, 0.41f)
                quadToRelative(0.55f, 0.63f, 0.79f, 1.41f)
                reflectiveQuadTo(17.5f, 6f)
                quadToRelative(0f, 0.65f, -0.16f, 1.29f)
                quadTo(17.18f, 7.93f, 16.83f, 8.5f)
                quadTo(16.7f, 8.73f, 16.46f, 8.86f)
                reflectiveQuadTo(15.95f, 9f)
                close()
                moveTo(10.48f, 22f)
                quadTo(9.78f, 22f, 9.16f, 21.7f)
                reflectiveQuadTo(8.13f, 20.85f)
                lineTo(3.1f, 14.48f)
                quadTo(2.9f, 14.25f, 2.93f, 13.94f)
                reflectiveQuadTo(3.15f, 13.43f)
                quadTo(3.65f, 12.9f, 4.35f, 12.8f)
                reflectiveQuadToRelative(1.3f, 0.28f)
                lineTo(7.5f, 14.2f)
                verticalLineTo(6f)
                quadTo(7.5f, 5.57f, 7.79f, 5.29f)
                reflectiveQuadTo(8.5f, 5f)
                quadTo(8.93f, 5f, 9.23f, 5.29f)
                quadTo(9.53f, 5.57f, 9.53f, 6f)
                verticalLineToRelative(5f)
                horizontalLineTo(17f)
                quadToRelative(1.25f, 0f, 2.13f, 0.88f)
                reflectiveQuadTo(20f, 14f)
                verticalLineToRelative(4f)
                quadToRelative(0f, 1.65f, -1.18f, 2.82f)
                reflectiveQuadTo(16f, 22f)
                horizontalLineTo(10.48f)
                close()
                moveTo(11.98f, 9f)
                quadTo(11.55f, 9f, 11.26f, 8.71f)
                reflectiveQuadTo(10.98f, 8f)
                quadToRelative(0f, -0.05f, 0.13f, -0.5f)
                quadTo(11.3f, 7.15f, 11.4f, 6.79f)
                quadTo(11.5f, 6.43f, 11.5f, 6f)
                quadToRelative(0f, -1.25f, -0.88f, -2.13f)
                reflectiveQuadTo(8.5f, 3f)
                reflectiveQuadTo(6.38f, 3.88f)
                reflectiveQuadTo(5.5f, 6f)
                quadToRelative(0f, 0.43f, 0.1f, 0.79f)
                reflectiveQuadTo(5.9f, 7.5f)
                quadTo(5.98f, 7.63f, 6f, 7.75f)
                reflectiveQuadTo(6.03f, 8f)
                quadToRelative(0f, 0.42f, -0.28f, 0.71f)
                reflectiveQuadTo(5.05f, 9f)
                quadTo(4.78f, 9f, 4.54f, 8.85f)
                reflectiveQuadTo(4.18f, 8.48f)
                quadTo(3.85f, 7.93f, 3.68f, 7.3f)
                reflectiveQuadTo(3.5f, 6f)
                quadTo(3.5f, 3.92f, 4.96f, 2.46f)
                reflectiveQuadTo(8.5f, 1f)
                reflectiveQuadToRelative(3.54f, 1.46f)
                reflectiveQuadTo(13.5f, 6f)
                quadToRelative(0f, 0.68f, -0.17f, 1.3f)
                reflectiveQuadToRelative(-0.5f, 1.17f)
                quadTo(12.7f, 8.7f, 12.48f, 8.85f)
                reflectiveQuadTo(11.98f, 9f)
                close()
            }
        }.build()
        return touchTriple!!
    }

private var touchTriple: ImageVector? = null
