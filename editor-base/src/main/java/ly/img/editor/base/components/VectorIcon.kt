package ly.img.editor.base.components

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import ly.img.editor.core.ui.ColorSchemeKeyToken
import ly.img.editor.core.ui.fromToken
import ly.img.editor.core.ui.tab_item.TabIcon
import ly.img.editor.core.ui.tab_item.TabIconComposable

object VectorIconComposable : TabIconComposable<VectorIcon>() {
    @Composable
    override fun IconContent(icon: VectorIcon) {
        Icon(
            imageVector = icon.imageVector,
            contentDescription = null,
            tint =
                if (icon.tint != null) {
                    MaterialTheme.colorScheme.fromToken(icon.tint)
                } else {
                    LocalContentColor.current
                },
        )
    }
}

class VectorIcon(
    val imageVector: ImageVector,
    val tint: ColorSchemeKeyToken? = null,
) : TabIcon
