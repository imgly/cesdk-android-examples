package ly.img.editor.base.dock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ly.img.editor.base.components.ColorButton
import ly.img.editor.base.components.FillButton
import ly.img.editor.base.engine.Fill
import ly.img.editor.base.engine.getFillInfo
import ly.img.editor.base.engine.getStrokeColor
import ly.img.editor.base.engine.isFillStrokeSupported
import ly.img.editor.core.ui.tab_item.TabIcon
import ly.img.editor.core.ui.tab_item.TabIconComposable
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

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
                    modifier = Modifier.padding(start = 12.dp),
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
    val strokeColor: Color?,
) : TabIcon {
    companion object {
        fun create(
            engine: Engine,
            designBlock: DesignBlock,
        ): FillStrokeIcon {
            val (isFillSupported, isStrokeSupported) = engine.block.isFillStrokeSupported(designBlock)
            return FillStrokeIcon(
                fill = engine.block.getFillInfo(designBlock)?.takeIf { engine.block.isFillEnabled(designBlock) },
                hasFill = isFillSupported,
                hasStroke = isStrokeSupported,
                strokeColor = engine.getStrokeColor(designBlock)?.takeIf { engine.block.isStrokeEnabled(designBlock) },
            )
        }
    }
}
