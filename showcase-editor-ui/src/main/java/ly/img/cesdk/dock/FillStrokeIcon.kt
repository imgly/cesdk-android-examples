package ly.img.cesdk.dock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ly.img.cesdk.core.components.ColorButton
import ly.img.cesdk.core.components.FillButton
import ly.img.cesdk.core.components.tab_item.TabIcon
import ly.img.cesdk.core.components.tab_item.TabIconComposable
import ly.img.cesdk.engine.Fill

object FillStrokeIconComposable : TabIconComposable<FillStrokeIcon>() {

    @Composable
    override fun IconContent(icon: FillStrokeIcon) {
        if (icon.hasFill && icon.hasStroke) {
            Box {
                FillButton(fill = icon.fill, buttonSize = 24.dp, selectionStrokeWidth = 0.dp)
                ColorButton(
                    color = icon.strokeColor,
                    buttonSize = 24.dp,
                    selectionStrokeWidth = 0.dp,
                    punchHole = true,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        } else if (icon.hasFill) {
            FillButton(fill = icon.fill, buttonSize = 24.dp, selectionStrokeWidth = 0.dp)
        } else if (icon.hasStroke) {
            ColorButton(color = icon.strokeColor, buttonSize = 24.dp, selectionStrokeWidth = 0.dp, punchHole = true)
        } else {
            throw IllegalStateException("FillStrokeIcon has neither stroke nor fill.")
        }
    }
}

class FillStrokeIcon(
    val hasFill: Boolean,
    val fill: Fill?,
    val hasStroke: Boolean,
    val strokeColor: Color?
) : TabIcon