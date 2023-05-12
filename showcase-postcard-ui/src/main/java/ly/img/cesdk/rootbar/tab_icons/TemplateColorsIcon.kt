package ly.img.cesdk.rootbar.tab_icons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ly.img.cesdk.core.components.ColorButton
import ly.img.cesdk.core.components.tab_item.TabIcon
import ly.img.cesdk.core.components.tab_item.TabIconComposable
import ly.img.cesdk.core.utils.ifTrue

object TemplateColorsIconComposable : TabIconComposable<TemplateColorsIcon>() {

    @Composable
    override fun IconContent(icon: TemplateColorsIcon) {
        Box {
            icon.colors.forEachIndexed { index, color ->
                ColorButton(
                    color = color,
                    buttonSize = 24.dp,
                    selectionStrokeWidth = 0.dp,
                    modifier = Modifier.ifTrue(index > 0) { padding(start = 12.dp) }
                )
            }
        }
    }
}

class TemplateColorsIcon(val colors: List<Color>) : TabIcon