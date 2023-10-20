package ly.img.cesdk.library.components.assets

import ly.img.engine.Asset

data class FindAssetsResultUiState(
    val page: Int = 0,
    val searchQuery: String = "",
    val canPaginate: Boolean = false,
    val listState: ListState = ListState.IDLE,
    val assets: List<Asset> = listOf(),
    val assetIds: Set<String> = setOf()
)

enum class ListState {
    IDLE,
    LOADING,
    PAGINATING,
    ERROR,
    EMPTY_RESULT,
    UPLOADS_EMPTY_MAIN_RESULT,
    PAGINATION_ERROR,
}