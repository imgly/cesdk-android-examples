package ly.img.cesdk.library.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.cesdk.library.LibraryViewModel
import ly.img.cesdk.library.components.assets.AssetSource
import ly.img.cesdk.library.components.assets.AssetsGrid
import ly.img.cesdk.library.components.assets.getSearchTextRes
import ly.img.engine.Asset

@Composable
fun AssetsLibrary(
    assetSource: AssetSource,
    onSearchFocus: () -> Unit,
    onClick: (AssetSource, Asset) -> Unit,
    onClose: () -> Unit,
    tintImages: Boolean = false,
    viewModel: LibraryViewModel = viewModel()
) {
    Column {
        val searchText by viewModel.getSearchTextFlow(assetSource).collectAsState()
        SearchSheetHeader(
            searchValue = searchText,
            placeholderTextRes = assetSource.getSearchTextRes(),
            onSearchValueChange = {
                viewModel.onSearchTextChange(it, assetSource)
            },
            onSearchFocus = onSearchFocus,
            onClose = onClose
        )

        AssetsGrid(
            assetSource = assetSource,
            columns = 4,
            contentPadding = 4.dp,
            contentScale = ContentScale.Fit,
            tintImages = tintImages,
            onClick = {
                onClose()
                onClick(assetSource, it)
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSearch(assetSource)
        }
    }
}