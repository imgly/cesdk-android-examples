package ly.img.editor.core.ui.library.components

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.library.components.grid.AssetGrid
import ly.img.editor.core.ui.library.components.section.LibrarySectionColumn
import ly.img.editor.core.ui.library.state.AssetLibraryUiState
import ly.img.editor.core.ui.library.state.CategoryLoadState
import ly.img.editor.core.ui.library.state.WrappedAsset
import ly.img.editor.core.ui.library.util.LibraryEvent

@Composable
internal fun LibraryContent(
    uiState: AssetLibraryUiState,
    onAssetClick: (WrappedAsset) -> Unit,
    onUriPick: (UploadAssetSourceType, Uri) -> Unit,
    onLibraryEvent: (LibraryEvent) -> Unit,
    launchGetContent: (String, UploadAssetSourceType) -> Unit,
    launchCamera: (Boolean) -> Unit,
) {
    val libraryCategory = uiState.libraryCategory

    LaunchedEffect(libraryCategory) {
        onLibraryEvent(LibraryEvent.OnFetch(libraryCategory))
    }

    if (uiState.loadState == CategoryLoadState.LoadingAssets) {
        AssetGrid(
            uiState = uiState,
            onAssetClick = onAssetClick,
            onUriPick = onUriPick,
            onLibraryEvent = onLibraryEvent,
        )
    } else {
        LibrarySectionColumn(
            uiState = uiState,
            onAssetClick = onAssetClick,
            onLibraryEvent = onLibraryEvent,
            launchGetContent = launchGetContent,
            launchCamera = launchCamera,
        )
    }
}
