package ly.img.editor.showcases.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

val IconPack.Memories: ImageVector
    get() {
        if (memories != null) {
            return memories!!
        }
        memories = ImageVector.Builder(
            name = "CustomFunctionalitiesMemories",
            defaultWidth = 20.dp,
            defaultHeight = 20.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            // Material Symbols "auto_awesome" (sparkles) — three four-point stars.
            addPath(
                pathData = PathParser()
                    .parsePathString(
                        "M19 9l1.25-2.75L23 5l-2.75-1.25L19 1l-1.25 2.75L15 5l2.75 1.25z" +
                            "M11.5 9.5L9 4 6.5 9.5 1 12l5.5 2.5L9 20l2.5-5.5L17 12z" +
                            "M19 15l-1.25 2.75L15 19l2.75 1.25L19 23l1.25-2.75L23 19z",
                    ).toNodes(),
                fill = SolidColor(Color(0xFF1B1B1F)),
            )
        }.build()

        return memories!!
    }

private var memories: ImageVector? = null
