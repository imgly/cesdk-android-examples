package ly.img.editor.core.ui.library.state

import androidx.annotation.StringRes
import ly.img.editor.core.library.AssetSourceGroupType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.ui.library.components.section.LibrarySectionItem

internal data class AssetLibraryUiState(
    val libraryCategory: LibraryCategory,
    @StringRes val titleRes: Int,
    val searchText: String = "",
    val isInSearchMode: Boolean = false,
    val sectionItems: List<LibrarySectionItem> = listOf(),
    val loadState: CategoryLoadState = CategoryLoadState.Idle,
    val isRoot: Boolean = true,
    val assetsData: AssetsData = AssetsData()
)

internal data class AssetsData(
    val page: Int = 0,
    val canPaginate: Boolean = false,
    val assets: List<WrappedAsset> = listOf(),
    val assetSourceType: AssetSourceType? = null,
    val assetSourceGroupType: AssetSourceGroupType? = null,
    val assetsLoadState: AssetsLoadState = AssetsLoadState.Idle
)

internal enum class CategoryLoadState {
    Idle,
    Loading,
    LoadingAssets,
    LoadingSections,
    Success
}

internal enum class AssetsLoadState {
    Idle,
    Loading,
    Paginating,
    Error,
    EmptyResult,
    PaginationError
}