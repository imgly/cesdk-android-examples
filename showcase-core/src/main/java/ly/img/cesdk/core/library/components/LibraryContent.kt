package ly.img.cesdk.core.library.components

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import ly.img.cesdk.core.data.AssetSource
import ly.img.cesdk.core.data.UploadAssetSource
import ly.img.cesdk.core.library.components.grid.AssetGrid
import ly.img.cesdk.core.library.components.section.LibrarySectionColumn
import ly.img.cesdk.core.library.state.AssetLibraryUiState
import ly.img.cesdk.core.library.state.CategoryLoadState
import ly.img.cesdk.core.library.util.LibraryEvent
import ly.img.engine.Asset

@Composable
internal fun LibraryContent(
    uiState: State<AssetLibraryUiState>,
    onAssetClick: (AssetSource, Asset) -> Unit,
    onUriPick: (UploadAssetSource, Uri) -> Unit,
    onLibraryEvent: (LibraryEvent) -> Unit
) {
    val uiStateValue = uiState.value
    val libraryCategory = uiStateValue.libraryCategory

    LaunchedEffect(libraryCategory) {
        onLibraryEvent(LibraryEvent.OnFetch(libraryCategory))
    }

    if (uiStateValue.loadState == CategoryLoadState.LoadingAssets) {
        AssetGrid(
            uiState = uiStateValue,
            onAssetClick = onAssetClick,
            onUriPick = onUriPick,
            onLibraryEvent = onLibraryEvent
        )
    } else {
        LibrarySectionColumn(
            uiState = uiStateValue,
            onAssetClick = onAssetClick,
            onUriPick = onUriPick,
            onLibraryEvent = onLibraryEvent
        )
    }
}