package ly.img.cesdk.library.components

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.cesdk.core.components.WrappedIndicatorTabRow
import ly.img.cesdk.editorui.R
import ly.img.cesdk.library.LibraryViewModel
import ly.img.cesdk.library.components.assets.AssetsGrid
import ly.img.cesdk.library.components.assets.ImageSource
import ly.img.cesdk.library.components.assets.getSearchTextRes
import ly.img.cesdk.library.components.uploads.AddImageButton
import ly.img.engine.Asset

@Composable
fun ImagesLibrary(
    onClose: () -> Unit,
    onSearchFocus: () -> Unit,
    onImagePicked: (ImageSource, Asset) -> Unit,
    onImageUploaded: (String) -> Unit,
    viewModel: LibraryViewModel = viewModel()
) {
    var selectedItem by remember { mutableStateOf(1) }
    val selectedTab = tabItems[selectedItem]
    val assetSource = selectedTab.imageSource

    val onImagePickedFromGallery = remember {
        { uri: Uri ->
            onClose()
            onImageUploaded(uri.toString())
        }
    }

    Column {
        val searchText by viewModel.getSearchTextFlow(assetSource).collectAsState()
        SearchSheetHeader(
            searchValue = searchText,
            placeholderTextRes = assetSource.getSearchTextRes(),
            onSearchValueChange = {
                viewModel.onSearchTextChange(it, assetSource)
            },
            onSearchFocus = onSearchFocus,
            onClose = onClose,
            leadingAction = {
                AddImageButton(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    onImagePicked = onImagePickedFromGallery
                )
            }
        )

        WrappedIndicatorTabRow(
            tabs = tabItems.map { stringResource(it.titleRes) },
            selectedTabIndex = selectedItem,
            onTabClick = {
                selectedItem = it
            }
        )

        AssetsGrid(
            assetSource = assetSource,
            onClick = {
                onClose()
                onImagePicked(assetSource, it)
            },
            onImagePicked = onImagePickedFromGallery
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSearch(assetSource)
        }
    }
}

private val tabItems = listOf(TabItem.Uploads, TabItem.Images, TabItem.Unsplash)

private sealed class TabItem(@StringRes val titleRes: Int, val imageSource: ImageSource) {
    object Images : TabItem(R.string.cesdk_example_images, ImageSource.Local)
    object Uploads : TabItem(R.string.cesdk_uploads, ImageSource.Uploads)
    object Unsplash : TabItem(R.string.cesdk_unsplash, ImageSource.Unsplash)
}