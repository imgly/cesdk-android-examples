package ly.img.editor.postcard.rootbar

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import ly.img.editor.base.engine.getFillColor
import ly.img.editor.base.engine.toComposeColor
import ly.img.editor.core.component.data.EditorIcon
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Typeface
import ly.img.editor.postcard.R
import ly.img.editor.postcard.bottomsheet.message_size.MessageSize
import ly.img.editor.postcard.util.SelectionColors
import ly.img.editor.postcard.util.getPinnedBlock
import ly.img.engine.Engine

data class RootBarItemData(
    val type: RootBarItemType,
    @StringRes val labelStringRes: Int,
    val icon: EditorIcon,
)

enum class RootBarItemType {
    TemplateColors,
    Font,
    Size,
    Color,
}

internal fun rootBarItems(
    engine: Engine,
    pageIndex: Int,
    pageSelectionColors: SelectionColors?,
): List<RootBarItemData> {
    // Initially, when the scene is not loaded, we are unable to find the block.
    val block =
        when (engine.isEngineRunning()) {
            true -> engine.getPinnedBlock() ?: return listOf()
            false -> return listOf()
        }
    return if (pageIndex == 0) {
        if (pageSelectionColors == null) {
            listOf()
        } else {
            listOf(
                RootBarItemData(
                    RootBarItemType.TemplateColors,
                    R.string.ly_img_editor_colors,
                    EditorIcon.Colors(
                        pageSelectionColors.getColors().map { it.color.toComposeColor() },
                    ),
                ),
            )
        }
    } else {
        listOf(
            RootBarItemData(
                RootBarItemType.Font,
                ly.img.editor.base.R.string.ly_img_editor_font,
                EditorIcon.Vector(IconPack.Typeface),
            ),
            RootBarItemData(
                RootBarItemType.Size,
                R.string.ly_img_editor_size,
                EditorIcon.Vector(MessageSize.get(engine, block).circledIcon),
            ),
            RootBarItemData(
                RootBarItemType.Color,
                R.string.ly_img_editor_color,
                EditorIcon.Colors(engine.getFillColor(block) ?: Color.Black),
            ),
        )
    }
}
