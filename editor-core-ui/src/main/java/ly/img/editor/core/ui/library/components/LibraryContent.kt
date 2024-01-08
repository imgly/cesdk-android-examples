package ly.img.editor.core.ui.library.components

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.library.components.grid.AssetGrid
import ly.img.editor.core.ui.library.components.section.LibrarySectionColumn
import ly.img.editor.core.ui.library.state.AssetLibraryUiState
import ly.img.editor.core.ui.library.state.CategoryLoadState
import ly.img.editor.core.ui.library.util.LibraryEvent
import ly.img.engine.Asset

@Composable
internal fun LibraryContent(
    uiState: State<AssetLibraryUiState>,
    onAssetClick: (AssetSourceType, Asset) -> Unit,
    onUriPick: (UploadAssetSourceType, Uri) -> Unit,
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
