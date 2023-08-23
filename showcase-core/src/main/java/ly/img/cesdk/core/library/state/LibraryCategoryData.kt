package ly.img.cesdk.core.library.state

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow

internal data class LibraryCategoryData(
    var dataStack: ArrayList<LibraryStackData>,
    val uiStateFlow: MutableStateFlow<AssetLibraryUiState>,
    var dirty: Boolean = false,
    var fetchJob: Job? = null,
    var searchJob: Job? = null
)

internal data class LibraryStackData(
    val assetSourceGroups: List<AssetSourceGroup>,
    val group: String? = null,
    val fullQuery: Boolean = false
) {
    fun isSingleAssetSource() = assetSourceGroups.singleOrNull()?.sources?.singleOrNull() != null

    fun getSingleAssetSourceGroupType() = assetSourceGroups.single().type

    fun getSingleAssetSource() = assetSourceGroups.single().sources.single()
}