package ly.img.editor.showcases.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val IconPack.GenOutputVector: ImageVector
    get() {
        if (genOutputVector != null) {
            return genOutputVector!!
        }
        genOutputVector = ImageVector.Builder(
            name = "GenOutputVector",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color(0xFF444655))) {
                moveTo(8f, 17.95f)
                curveTo(8.167f, 17.967f, 8.329f, 17.979f, 8.488f, 17.988f)
                curveTo(8.646f, 17.996f, 8.817f, 18f, 9f, 18f)
                curveTo(9.183f, 18f, 9.354f, 17.996f, 9.512f, 17.988f)
                curveTo(9.671f, 17.979f, 9.833f, 17.967f, 10f, 17.95f)
                verticalLineTo(20f)
                horizontalLineTo(20f)
                verticalLineTo(10f)
                horizontalLineTo(17.95f)
                curveTo(17.967f, 9.833f, 17.979f, 9.671f, 17.988f, 9.512f)
                curveTo(17.996f, 9.354f, 18f, 9.183f, 18f, 9f)
                curveTo(18f, 8.817f, 17.996f, 8.646f, 17.988f, 8.488f)
                curveTo(17.979f, 8.329f, 17.967f, 8.167f, 17.95f, 8f)
                horizontalLineTo(20f)
                curveTo(20.55f, 8f, 21.021f, 8.196f, 21.413f, 8.587f)
                curveTo(21.804f, 8.979f, 22f, 9.45f, 22f, 10f)
                verticalLineTo(20f)
                curveTo(22f, 20.55f, 21.804f, 21.021f, 21.413f, 21.413f)
                curveTo(21.021f, 21.804f, 20.55f, 22f, 20f, 22f)
                horizontalLineTo(10f)
                curveTo(9.45f, 22f, 8.979f, 21.804f, 8.588f, 21.413f)
                curveTo(8.196f, 21.021f, 8f, 20.55f, 8f, 20f)
                verticalLineTo(17.95f)
                close()
                moveTo(9f, 16f)
                curveTo(7.05f, 16f, 5.396f, 15.321f, 4.037f, 13.962f)
                curveTo(2.679f, 12.604f, 2f, 10.95f, 2f, 9f)
                curveTo(2f, 7.05f, 2.679f, 5.396f, 4.037f, 4.037f)
                curveTo(5.396f, 2.679f, 7.05f, 2f, 9f, 2f)
                curveTo(10.95f, 2f, 12.604f, 2.679f, 13.963f, 4.037f)
                curveTo(15.321f, 5.396f, 16f, 7.05f, 16f, 9f)
                curveTo(16f, 10.95f, 15.321f, 12.604f, 13.963f, 13.962f)
                curveTo(12.604f, 15.321f, 10.95f, 16f, 9f, 16f)
                close()
                moveTo(9f, 14f)
                curveTo(10.383f, 14f, 11.563f, 13.512f, 12.538f, 12.537f)
                curveTo(13.513f, 11.562f, 14f, 10.383f, 14f, 9f)
                curveTo(14f, 7.617f, 13.513f, 6.438f, 12.538f, 5.463f)
                curveTo(11.563f, 4.488f, 10.383f, 4f, 9f, 4f)
                curveTo(7.617f, 4f, 6.438f, 4.488f, 5.463f, 5.463f)
                curveTo(4.488f, 6.438f, 4f, 7.617f, 4f, 9f)
                curveTo(4f, 10.383f, 4.488f, 11.562f, 5.463f, 12.537f)
                curveTo(6.438f, 13.512f, 7.617f, 14f, 9f, 14f)
                close()
            }
        }.build()

        return genOutputVector!!
    }

private var genOutputVector: ImageVector? = null
