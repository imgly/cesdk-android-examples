package ly.img.editor.base.sheet

import ly.img.editor.core.component.data.Height
import ly.img.editor.core.sheet.SheetStyle
import ly.img.editor.core.sheet.SheetType

class LibraryTabsSheetType(
    override val style: SheetStyle =
        SheetStyle(
            isFloating = true,
            maxHeight = Height.Fraction(1F),
            isHalfExpandingEnabled = true,
        ),
) : SheetType
