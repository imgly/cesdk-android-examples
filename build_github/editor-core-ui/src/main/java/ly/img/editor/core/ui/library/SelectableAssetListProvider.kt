package ly.img.editor.core.ui.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.ui.library.components.section.LibrarySectionItem
import ly.img.editor.core.ui.library.state.WrappedAsset
import ly.img.editor.core.ui.library.util.LibraryEvent

@Composable
fun SelectableAssetListProvider(
    libraryCategory: LibraryCategory,
    filter: List<String> = emptyList(),
    onAssetsLoaded: (List<WrappedAsset>) -> Unit,
) {
    SelectableAssetListProvider(
        libraryCategory = libraryCategory,
        filter = filter,
        mapper = { it },
        onAssetsLoaded = onAssetsLoaded,
    )
}

@Composable
fun <T> SelectableAssetListProvider(
    libraryCategory: LibraryCategory,
    filter: List<String> = emptyList(),
    mapper: (WrappedAsset) -> T,
    onAssetsLoaded: (List<T>) -> Unit,
) {
    val viewModel = viewModel<LibraryViewModel>()
    val uiState = viewModel.getAssetLibraryUiState(libraryCategory).collectAsState()

    LaunchedEffect(libraryCategory) {
        viewModel.onEvent(LibraryEvent.OnFetch(libraryCategory))
    }

    LaunchedEffect(uiState.value.sectionItems) {
        uiState.value.sectionItems.flatMap {
            (it as? LibrarySectionItem.Content)?.wrappedAssets ?: emptyList()
        }.let { assets ->
            if (filter.isEmpty()) {
                assets
            } else {
                assets.filter { filter.contains(it.asset.id) }
            }.map(mapper).let {
                onAssetsLoaded(it)
            }
        }
    }
}
