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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import ly.img.editor.core.R
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.iconpack.Folder
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Search
import ly.img.editor.core.ui.library.components.asset.AssetsLoadingContent
import ly.img.editor.core.ui.library.state.AssetLibraryUiState
import ly.img.editor.core.ui.library.state.AssetsLoadState
import ly.img.editor.core.ui.library.state.WrappedAsset
import ly.img.editor.core.ui.library.util.AssetLibraryUiConfig
import ly.img.editor.core.ui.library.util.LibraryEvent

@Composable
internal fun AssetGrid(
    uiState: AssetLibraryUiState,
    onAssetClick: (WrappedAsset) -> Unit,
    onUriPick: (UploadAssetSourceType, Uri) -> Unit,
    onLibraryEvent: (LibraryEvent) -> Unit,
) {
    val lazyGridState = rememberLazyGridState()
    val libraryCategory = uiState.libraryCategory

    val shouldStartPaginate by remember(uiState.assetsData.canPaginate) {
        derivedStateOf {
            uiState.assetsData.canPaginate &&
                (
                    lazyGridState.layoutInfo.visibleItemsInfo
                        .lastOrNull()
                        ?.index ?: -9
                ) >=
                lazyGridState.layoutInfo.totalItemsCount - 6
        }
    }

    val nestedScrollConnection =
        remember(libraryCategory) {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    onLibraryEvent(LibraryEvent.OnEnterSearchMode(false, libraryCategory))
                    return Offset.Zero
                }
            }
        }

    LaunchedEffect(shouldStartPaginate, uiState.assetsData.assetsLoadState) {
        if (shouldStartPaginate && uiState.assetsData.assetsLoadState == AssetsLoadState.Idle) {
            onLibraryEvent(LibraryEvent.OnFetch(libraryCategory))
        }
    }

    when (uiState.assetsData.assetsLoadState) {
        AssetsLoadState.Loading -> {
            AssetsLoadingContent(checkNotNull(uiState.assetsData.assetType))
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
                    text = stringResource(R.string.ly_img_editor_no_elements),
                )
            } else {
                val assetSource = uiState.assetsData.assetSourceType
                EmptyResultContent(
                    icon = IconPack.Folder,
                    text = stringResource(R.string.ly_img_editor_no_elements),
                    button =
                        if (assetSource is UploadAssetSourceType) {
                            {
                                val launcher =
                                    rememberLauncherForActivityResult(
                                        contract = ActivityResultContracts.GetContent(),
                                    ) { uri: Uri? ->
                                        uri?.let { onUriPick(assetSource, it) }
                                    }
                                Button(onClick = {
                                    launcher.launch(assetSource.mimeTypeFilter)
                                }) {
                                    Text(text = stringResource(R.string.ly_img_editor_add))
                                }
                            }
                        } else {
                            null
                        },
                )
            }
        }

        else -> {
            val assetType = uiState.assetsData.assetType
            val assetSource = uiState.assetsData.assetSourceType
            if (assetType != null && assetSource != null) {
                val activatedPreviewItemId = remember { mutableStateOf<String?>(null) }
                val context = LocalContext.current
                val exoPlayerInstance by
                    remember {
                        lazy {
                            ExoPlayer.Builder(context.applicationContext).build()
                        }
                    }

                DisposableEffect(exoPlayerInstance) {
                    onDispose {
                        exoPlayerInstance.release()
                    }
                }
                LazyVerticalGrid(
                    state = lazyGridState,
                    verticalArrangement = AssetLibraryUiConfig.assetGridVerticalArrangement(assetType),
                    horizontalArrangement = AssetLibraryUiConfig.assetGridHorizontalArrangement(assetType),
                    contentPadding = PaddingValues(4.dp),
                    columns = GridCells.Fixed(AssetLibraryUiConfig.assetGridColumns(assetType)),
                    modifier =
                        Modifier
                            .nestedScroll(nestedScrollConnection)
                            .fillMaxSize(),
                ) {
                    val assets = uiState.assetsData.assets
                    if (assetSource is UploadAssetSourceType && assets.isNotEmpty()) {
                        item {
                            AssetGridUploadItemContent(
                                uploadAssetSource = assetSource,
                                assetType = assetType,
                                onUriPick = onUriPick,
                            )
                        }
                    }
                    items(assets) { asset ->
                        AssetGridItemContent(
                            wrappedAsset = asset,
                            assetType = assetType,
                            onAssetClick = onAssetClick,
                            activatedPreviewItem = activatedPreviewItemId,
                            exoPlayer = { exoPlayerInstance },
                            onAssetLongClick = {
                                onLibraryEvent(LibraryEvent.OnAssetLongClick(it))
                            },
                        )
                    }
                }
            }
        }
    }
}
