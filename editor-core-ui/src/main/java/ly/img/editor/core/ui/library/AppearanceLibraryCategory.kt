package ly.img.editor.core.ui.library

import ly.img.editor.core.R
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.LibraryElements
import ly.img.editor.core.iconpack.LibraryElementsOutline
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.LibraryContent

object AppearanceLibraryCategory {
    val Filters by lazy {
        LibraryCategory(
            tabTitleRes = R.string.ly_img_editor_filters,
            tabSelectedIcon = IconPack.LibraryElements,
            tabUnselectedIcon = IconPack.LibraryElementsOutline,
            content =
                LibraryContent.Sections(
                    titleRes = R.string.ly_img_editor_filters,
                    sections =
                        listOf(
                            LibraryContent.Section(
                                sourceTypes = listOf(AppearanceAssetSourceType.DuoToneFilter),
                                count = Int.MAX_VALUE,
                                assetType = AssetType.Filter,
                                expandContent = null,
                            ),
                            LibraryContent.Section(
                                sourceTypes = listOf(AppearanceAssetSourceType.LutFilter),
                                addGroupedSubSections = true,
                                count = Int.MAX_VALUE,
                                assetType = AssetType.Filter,
                                expandContent = null,
                            ),
                        ),
                ),
        )
    }

    val FxEffects by lazy {
        LibraryCategory(
            tabTitleRes = R.string.ly_img_editor_effects,
            tabSelectedIcon = IconPack.LibraryElements,
            tabUnselectedIcon = IconPack.LibraryElementsOutline,
            content =
                LibraryContent.Sections(
                    titleRes = R.string.ly_img_editor_effects,
                    sections =
                        listOf(
                            LibraryContent.Section(
                                sourceTypes = listOf(AppearanceAssetSourceType.FxEffect),
                                count = Int.MAX_VALUE,
                                assetType = AssetType.Effect,
                                expandContent = null,
                            ),
                        ),
                ),
        )
    }

    val Blur by lazy {
        LibraryCategory(
            tabTitleRes = R.string.ly_img_editor_blur,
            tabSelectedIcon = IconPack.LibraryElements,
            tabUnselectedIcon = IconPack.LibraryElementsOutline,
            content =
                LibraryContent.Sections(
                    titleRes = R.string.ly_img_editor_blur,
                    sections =
                        listOf(
                            LibraryContent.Section(
                                sourceTypes = listOf(AppearanceAssetSourceType.Blur),
                                count = Int.MAX_VALUE,
                                assetType = AssetType.Blur,
                                expandContent = null,
                            ),
                        ),
                ),
        )
    }
}
