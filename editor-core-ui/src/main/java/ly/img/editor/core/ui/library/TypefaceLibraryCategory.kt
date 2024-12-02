package ly.img.editor.core.ui.library

import ly.img.editor.core.R
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.LibraryElements
import ly.img.editor.core.iconpack.LibraryElementsOutline
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.data.AssetSourceType

val TypefaceLibraryCategory by lazy {
    LibraryCategory(
        tabTitleRes = R.string.ly_img_editor_typeface,
        tabSelectedIcon = IconPack.LibraryElements,
        tabUnselectedIcon = IconPack.LibraryElementsOutline,
        content =
            LibraryContent.Sections(
                titleRes = R.string.ly_img_editor_typeface,
                sections =
                    listOf(
                        LibraryContent.Section(
                            sourceTypes = listOf(AssetSourceType(sourceId = "ly.img.typeface")),
                            count = Int.MAX_VALUE,
                            assetType = AssetType.Typeface,
                            expandContent = null,
                        ),
                    ),
            ),
    )
}
