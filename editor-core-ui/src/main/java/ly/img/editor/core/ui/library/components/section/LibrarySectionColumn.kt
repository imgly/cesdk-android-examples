package ly.img.editor.core.ui.library.components.section

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
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.library.state.AssetLibraryUiState
import ly.img.editor.core.ui.library.state.WrappedAsset
import ly.img.editor.core.ui.library.util.LibraryEvent

@Composable
internal fun LibrarySectionColumn(
    uiState: AssetLibraryUiState,
    onAssetClick: (WrappedAsset) -> Unit,
    onLibraryEvent: (LibraryEvent) -> Unit,
    launchGetContent: (String, UploadAssetSourceType) -> Unit,
    launchCamera: (Boolean) -> Unit,
) {
    val nestedScrollConnection =
        remember(uiState.libraryCategory) {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    onLibraryEvent(LibraryEvent.OnEnterSearchMode(false, uiState.libraryCategory))
                    return Offset.Zero
                }
            }
        }

    LazyColumn(
        modifier =
            Modifier
                .nestedScroll(nestedScrollConnection)
                .testTag(tag = "LibrarySectionColumn")
                .fillMaxSize(),
    ) {
        items(uiState.sectionItems, key = { it.id }, contentType = { it.javaClass }) { sectionItem ->
            when (sectionItem) {
                is LibrarySectionItem.Header ->
                    LibrarySectionHeader(
                        item = sectionItem,
                        onDrillDown = { content ->
                            LibraryEvent.OnDrillDown(
                                libraryCategory = uiState.libraryCategory,
                                expandContent = content,
                            ).let { onLibraryEvent(it) }
                        },
                        launchGetContent = launchGetContent,
                        launchCamera = launchCamera,
                    )

                is LibrarySectionItem.Content ->
                    LibrarySectionContent(
                        sectionItem = sectionItem,
                        onAssetClick = onAssetClick,
                        onAssetLongClick = { wrappedAsset ->
                            onLibraryEvent(LibraryEvent.OnAssetLongClick(wrappedAsset))
                        },
                        onSeeAllClick = { content ->
                            LibraryEvent.OnDrillDown(
                                libraryCategory = uiState.libraryCategory,
                                expandContent = content,
                            ).let { onLibraryEvent(it) }
                        },
                    )

                is LibrarySectionItem.ContentLoading ->
                    LibrarySectionContentLoadingContent(
                        assetType = sectionItem.section.assetType,
                    )

                is LibrarySectionItem.Error ->
                    LibrarySectionErrorContent(
                        assetType = sectionItem.assetType,
                    )

                is LibrarySectionItem.Loading -> {}
            }
        }
    }
}
