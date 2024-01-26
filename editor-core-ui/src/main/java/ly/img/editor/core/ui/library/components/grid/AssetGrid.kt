package ly.img.editor.core.ui.library.components.grid

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.core.R
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.iconpack.Folder
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Search
import ly.img.editor.core.ui.library.components.asset.AssetsLoadingContent
import ly.img.editor.core.ui.library.state.AssetLibraryUiState
import ly.img.editor.core.ui.library.state.AssetsLoadState
import ly.img.editor.core.ui.library.util.AssetLibraryUiConfig
import ly.img.editor.core.ui.library.util.LibraryEvent
import ly.img.engine.Asset

@Composable
internal fun AssetGrid(
    uiState: AssetLibraryUiState,
    onAssetClick: (AssetSourceType, Asset) -> Unit,
    onUriPick: (UploadAssetSourceType, Uri) -> Unit,
    onLibraryEvent: (LibraryEvent) -> Unit
) {
    val lazyGridState = rememberLazyGridState()
    val libraryCategory = uiState.libraryCategory

    val shouldStartPaginate by remember(uiState.assetsData.canPaginate) {
        derivedStateOf {
            uiState.assetsData.canPaginate && (lazyGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -9) >=
                lazyGridState.layoutInfo.totalItemsCount - 6
        }
    }

    val nestedScrollConnection = remember(libraryCategory) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                onLibraryEvent(LibraryEvent.OnEnterSearchMode(false, libraryCategory))
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(shouldStartPaginate, uiState.assetsData.assetsLoadState) {
        if (shouldStartPaginate && uiState.assetsData.assetsLoadState == AssetsLoadState.Idle)
            onLibraryEvent(LibraryEvent.OnFetch(libraryCategory))
    }

    when (uiState.assetsData.assetsLoadState) {
        AssetsLoadState.Loading -> {
            AssetsLoadingContent(checkNotNull(uiState.assetsData.assetSourceGroupType))
        }

        AssetsLoadState.Error -> {
            ErrorContent {
                onLibraryEvent(LibraryEvent.OnFetch(libraryCategory))
            }
        }

        AssetsLoadState.EmptyResult -> {
            if (uiState.searchText.isNotEmpty()) {
                EmptyResultContent(
                    icon = IconPack.Search,
                    text = stringResource(R.string.cesdk_no_elements)
                )
            } else {
                val assetSource = uiState.assetsData.assetSourceType
                EmptyResultContent(
                    icon = IconPack.Folder,
                    text = stringResource(R.string.cesdk_no_elements),
                    button = if (assetSource is UploadAssetSourceType) {
                        {
                            val launcher =
                                rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
                                    uri?.let { onUriPick(assetSource, it) }
                                }
                            Button(onClick = {
                                launcher.launch(assetSource.mimeTypeFilter)
                            }) {
                                Text(text = stringResource(R.string.cesdk_add))
                            }
                        }
                    } else null
                )
            }
        }

        else -> {
            val assetSourceGroupType = uiState.assetsData.assetSourceGroupType
            val assetSource = uiState.assetsData.assetSourceType
            if (assetSourceGroupType != null && assetSource != null) {
                LazyVerticalGrid(
                    state = lazyGridState,
                    verticalArrangement = AssetLibraryUiConfig.assetGridVerticalArrangement(assetSourceGroupType),
                    horizontalArrangement = AssetLibraryUiConfig.assetGridHorizontalArrangement(assetSourceGroupType),
                    contentPadding = PaddingValues(4.dp),
                    columns = GridCells.Fixed(AssetLibraryUiConfig.assetGridColumns(assetSourceGroupType)),
                    modifier = Modifier
                        .nestedScroll(nestedScrollConnection)
                        .fillMaxSize()
                ) {
                    val assets = uiState.assetsData.assets
                    if (assetSource is UploadAssetSourceType && assets.isNotEmpty()) {
                        item {
                            AssetGridUploadItemContent(
                                uploadAssetSource = assetSource,
                                assetSourceGroupType = assetSourceGroupType,
                                onUriPick = onUriPick
                            )
                        }
                    }
                    items(assets) { asset ->
                        AssetGridItemContent(
                            wrappedAsset = asset,
                            assetSourceGroupType = assetSourceGroupType,
                            onAssetClick = onAssetClick,
                            onAssetLongClick = { source, clickedAsset ->
                                onLibraryEvent(LibraryEvent.OnAssetLongClick(source, clickedAsset))
                            }
                        )
                    }
                }
            }
        }
    }
}