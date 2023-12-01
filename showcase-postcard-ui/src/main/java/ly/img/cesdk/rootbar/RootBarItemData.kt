package ly.img.cesdk.rootbar

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import ly.img.cesdk.bottomsheet.message_size.MessageSize
import ly.img.cesdk.components.VectorIcon
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.iconpack.Typeface
import ly.img.cesdk.core.ui.tab_item.TabIcon
import ly.img.cesdk.engine.getFillColor
import ly.img.cesdk.engine.toComposeColor
import ly.img.cesdk.postcardui.R
import ly.img.cesdk.rootbar.tab_icons.MessageColorIcon
import ly.img.cesdk.rootbar.tab_icons.TemplateColorsIcon
import ly.img.cesdk.util.SelectionColors
import ly.img.cesdk.util.getPinnedBlock
import ly.img.engine.Engine

data class RootBarItemData(
    val type: RootBarItemType,
    @StringRes val labelStringRes: Int,
    val icon: TabIcon
)

enum class RootBarItemType {
    TemplateColors,
    Font,
    Size,
    Color
}

internal fun rootBarItems(engine: Engine, pageIndex: Int, pageSelectionColors: SelectionColors?): List<RootBarItemData> {
    // Initially, when the scene is not loaded, we are unable to find the block.
    val block = when (engine.isEngineRunning()) {
        true -> engine.getPinnedBlock() ?: return listOf()
        false -> return listOf()
    }
    return if (pageIndex == 0) {
        if (pageSelectionColors == null) listOf() else {
            listOf(
                RootBarItemData(
                    RootBarItemType.TemplateColors,
                    R.string.cesdk_colors,
                    TemplateColorsIcon(pageSelectionColors.getColors().map { it.color.toComposeColor() })
                )
            )
        }
    } else {
        listOf(
            RootBarItemData(
                RootBarItemType.Font,
                ly.img.cesdk.editorui.R.string.cesdk_font,
                VectorIcon(IconPack.Typeface)
            ),
            RootBarItemData(
                RootBarItemType.Size,
                R.string.cesdk_size,
                VectorIcon(MessageSize.get(engine, block).circledIcon)
            ),
            RootBarItemData(
                RootBarItemType.Color,
                R.string.cesdk_color,
                MessageColorIcon(engine.getFillColor(block) ?: Color.Black)
            ),
        )
    }
}
