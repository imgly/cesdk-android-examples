package ly.img.editor.core.ui.library.components.section

import androidx.annotation.StringRes
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.library.state.WrappedAsset

sealed class LibrarySectionItem(val id: String) {
    data class Header(
        val stackIndex: Int,
        val sectionIndex: Int,
        @StringRes val titleRes: Int,
        val uploadAssetSourceType: UploadAssetSourceType?,
        val count: Int? = null,
        val expandContent: LibraryContent?,
    ) : LibrarySectionItem("Header $stackIndex $sectionIndex")

    data class Content(
        val stackIndex: Int,
        val sectionIndex: Int,
        val wrappedAssets: List<WrappedAsset>,
        val assetType: AssetType,
        val expandContent: LibraryContent?,
    ) : LibrarySectionItem("Content $stackIndex $sectionIndex")

    data class ContentLoading(
        val stackIndex: Int,
        val sectionIndex: Int,
        val subSectionIndex: Int,
        val section: LibraryContent.Section,
    ) : LibrarySectionItem("Content Loading $stackIndex $sectionIndex $subSectionIndex")

    data class Loading(
        val stackIndex: Int,
    ) : LibrarySectionItem("Loading $stackIndex")

    data class Error(
        val stackIndex: Int,
        val sectionIndex: Int,
        val assetType: AssetType,
    ) : LibrarySectionItem("Error $stackIndex $sectionIndex")
}
