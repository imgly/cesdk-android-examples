package ly.img.cesdk.core.library.components.section

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import ly.img.cesdk.core.data.AssetSource
import ly.img.cesdk.core.data.UploadAssetSource
import ly.img.cesdk.core.library.state.AssetLibraryUiState
import ly.img.cesdk.core.library.util.LibraryEvent
import ly.img.engine.Asset

@Composable
internal fun LibrarySectionColumn(
    uiState: AssetLibraryUiState,
    onAssetClick: (AssetSource, Asset) -> Unit,
    onUriPick: (UploadAssetSource, Uri) -> Unit,
    onLibraryEvent: (LibraryEvent) -> Unit
) {

    val nestedScrollConnection = remember(uiState.libraryCategory) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                onLibraryEvent(LibraryEvent.OnEnterSearchMode(false, uiState.libraryCategory))
                return Offset.Zero
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .nestedScroll(nestedScrollConnection)
            .testTag(tag = "LibrarySectionColumn")
            .fillMaxSize()
    ) {
        items(uiState.sectionItems, key = { it.id }, contentType = { it.javaClass }) { sectionItem ->
            when (sectionItem) {
                is LibrarySectionItem.Header -> LibrarySectionHeader(
                    item = sectionItem,
                    onDrillDown = {
                        onLibraryEvent(LibraryEvent.OnDrillDown(uiState.libraryCategory, sectionItem.stackData))
                    },
                    onUriPick = onUriPick
                )

                is LibrarySectionItem.Content -> LibrarySectionContent(
                    sectionItem = sectionItem,
                    onAssetClick = onAssetClick,
                    onAssetLongClick = { assetSource, asset ->
                        onLibraryEvent(LibraryEvent.OnAssetLongClick(assetSource, asset))
                    },
                    onSeeAllClick = {
                        onLibraryEvent(LibraryEvent.OnDrillDown(uiState.libraryCategory, sectionItem.stackData))
                    }
                )

                is LibrarySectionItem.ContentLoading -> LibrarySectionContentLoadingContent(
                    assetSourceGroupType = sectionItem.assetSourceGroupType
                )

                is LibrarySectionItem.Error -> LibrarySectionErrorContent(
                    assetSourceGroupType = sectionItem.assetSourceGroupType
                )

                LibrarySectionItem.Loading -> {}
            }
        }
    }
}