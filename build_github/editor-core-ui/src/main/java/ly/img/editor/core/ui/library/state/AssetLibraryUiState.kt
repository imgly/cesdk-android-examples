package ly.img.editor.core.ui.library.state

import androidx.annotation.StringRes
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.ui.library.components.section.LibrarySectionItem

data class AssetLibraryUiState(
    val libraryCategory: LibraryCategory,
    @StringRes val titleRes: Int,
    val searchText: String = "",
    val isInSearchMode: Boolean = false,
    val sectionItems: List<LibrarySectionItem> = listOf(),
    val loadState: CategoryLoadState = CategoryLoadState.Idle,
    val isRoot: Boolean = true,
    val assetsData: AssetsData = AssetsData(),
)

data class AssetsData(
    val page: Int = 0,
    val canPaginate: Boolean = false,
    val assets: List<WrappedAsset> = listOf(),
    val assetSourceType: AssetSourceType? = null,
    val assetType: AssetType? = null,
    val assetsLoadState: AssetsLoadState = AssetsLoadState.Idle,
)

enum class CategoryLoadState {
    Idle,
    Loading,
    LoadingAssets,
    LoadingSections,
    Success,
}

enum class AssetsLoadState {
    Idle,
    Loading,
    Paginating,
    Error,
    EmptyResult,
    PaginationError,
}
