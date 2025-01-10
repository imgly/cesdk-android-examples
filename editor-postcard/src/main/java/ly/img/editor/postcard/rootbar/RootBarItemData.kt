package ly.img.editor.postcard.rootbar

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import ly.img.editor.base.engine.getFillColor
import ly.img.editor.base.engine.toComposeColor
import ly.img.editor.core.component.data.EditorIcon
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Typeface
import ly.img.editor.core.sheet.SheetType
import ly.img.editor.postcard.R
import ly.img.editor.postcard.bottomsheet.PostcardSheetType
import ly.img.editor.postcard.bottomsheet.message_size.MessageSize
import ly.img.editor.postcard.util.SelectionColors
import ly.img.editor.postcard.util.getPinnedBlock
import ly.img.engine.Engine

data class RootBarItemData(
    val sheetType: SheetType,
    @StringRes val labelStringRes: Int,
    val icon: EditorIcon,
)

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
                    sheetType = PostcardSheetType.TemplateColors(),
                    labelStringRes = R.string.ly_img_editor_colors,
                    icon =
                        EditorIcon.Colors(
                            pageSelectionColors.getColors().map { it.color.toComposeColor() },
                        ),
                ),
            )
        }
    } else {
        listOf(
            RootBarItemData(
                sheetType = PostcardSheetType.Font(),
                labelStringRes = ly.img.editor.base.R.string.ly_img_editor_font,
                icon = EditorIcon.Vector(IconPack.Typeface),
            ),
            RootBarItemData(
                sheetType = PostcardSheetType.Size(),
                labelStringRes = R.string.ly_img_editor_size,
                icon = EditorIcon.Vector(MessageSize.get(engine, block).circledIcon),
            ),
            RootBarItemData(
                sheetType = PostcardSheetType.Color(),
                labelStringRes = R.string.ly_img_editor_color,
                icon = EditorIcon.Colors(engine.getFillColor(block) ?: Color.Black),
            ),
        )
    }
}
