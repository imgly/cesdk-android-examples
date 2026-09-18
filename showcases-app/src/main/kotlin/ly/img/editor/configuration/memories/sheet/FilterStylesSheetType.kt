package ly.img.editor.configuration.memories.sheet

import ly.img.editor.configuration.memories.style.VideoStyle
import ly.img.editor.configuration.memories.style.VideoStyles
import ly.img.editor.core.sheet.SheetStyle
import ly.img.editor.core.sheet.SheetType

fun createFilterStylesSheetType(
    thumbnails: Map<String, String>,
    initialSelectedId: String = VideoStyles.DEFAULT.id,
    onFilterSelected: (VideoStyle) -> Unit,
): SheetType.Custom = SheetType.Custom(
    style = SheetStyle(),
    content = {
        FilterStylesSheet(
            onFilterSelected = onFilterSelected,
            thumbnails = thumbnails,
            initialSelectedId = initialSelectedId,
        )
    },
)
