package ly.img.editor.base.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import ly.img.editor.core.ui.tab_item.TabIcon
import ly.img.editor.core.ui.tab_item.TabIconComposable

object VectorIconComposable : TabIconComposable<VectorIcon>() {

    @Composable
    override fun IconContent(icon: VectorIcon) {
        Icon(imageVector = icon.imageVector, contentDescription = null)
    }
}

class VectorIcon(val imageVector: ImageVector) : TabIcon
