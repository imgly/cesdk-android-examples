package ly.img.cesdk.library

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.cesdk.core.components.bottomsheet.ModalBottomSheetValue
import ly.img.cesdk.core.components.bottomsheet.SwipeableV2State
import ly.img.cesdk.core.iconpack.Cloudupload
import ly.img.cesdk.core.iconpack.Clouduploadoutline
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.iconpack.Image
import ly.img.cesdk.core.iconpack.Imageoutline
import ly.img.cesdk.core.iconpack.Shapes
import ly.img.cesdk.core.iconpack.Shapesoutline
import ly.img.cesdk.core.iconpack.Stickeremoji
import ly.img.cesdk.core.iconpack.Stickeremojioutline
import ly.img.cesdk.core.iconpack.Textfields
import ly.img.cesdk.editorui.Event
import ly.img.cesdk.editorui.R
import ly.img.cesdk.library.components.AssetsLibrary
import ly.img.cesdk.library.components.ImagesLibrary
import ly.img.cesdk.library.components.TextLibrary
import ly.img.cesdk.library.components.assets.AssetSource
import ly.img.cesdk.library.components.uploads.UploadsLibrary
import kotlin.math.roundToInt

internal class OffsetWrapper(var offset: Float = 0f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLibrarySheet(
    swipeableState: SwipeableV2State<ModalBottomSheetValue>,
    onEvent: (Event) -> Unit,
    viewModel: LibraryViewModel = viewModel()
) {
    var selectedItem: NavBarItem by remember { mutableStateOf(NavBarItem.Images) }
    val onClose = remember { { onEvent(Event.HideSheet) } }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 80.dp padding to accommodate for bottom nav bar
        Surface(modifier = Modifier.padding(bottom = 80.dp)) {
            when (selectedItem) {
                NavBarItem.Images -> ImagesLibrary(
                    onClose = onClose,
                    onSearchFocus = { onEvent(Event.ExpandSheet) },
                    onImagePicked = viewModel::addAsset,
                    onImageUploaded = viewModel::addUploadedImage
                )

                NavBarItem.Shapes -> AssetsLibrary(
                    assetSource = AssetSource.Shapes,
                    onSearchFocus = { onEvent(Event.ExpandSheet) },
                    onClick = viewModel::addAsset,
                    onClose = onClose,
                    tintImages = true
                )

                NavBarItem.Stickers -> AssetsLibrary(
                    assetSource = AssetSource.Stickers,
                    onSearchFocus = { onEvent(Event.ExpandSheet) },
                    onClick = viewModel::addAsset,
                    onClose = onClose
                )

                NavBarItem.Text -> TextLibrary(
                    title = stringResource(R.string.cesdk_text),
                    onClose = onClose,
                    onTextPicked = viewModel::addText
                )

                NavBarItem.Uploads -> UploadsLibrary(
                    title = stringResource(R.string.cesdk_uploads),
                    onClose = onClose,
                    onImagePicked = viewModel::addAsset,
                    onImageUploaded = viewModel::addUploadedImage
                )
            }
        }

        var bottomNavBarHeight by remember { mutableStateOf(0) }
        val offsetWrapper = remember(swipeableState.maxOffset) { OffsetWrapper() }

        if (offsetWrapper.offset == 0f) {
            swipeableState.let { offsetWrapper.offset = swipeableState.maxOffset }
        }

        NavigationBar(modifier = Modifier
            .onGloballyPositioned {
                bottomNavBarHeight = it.size.height
            }
            .absoluteOffset {
                IntOffset(
                    x = 0,
                    // 8.dp is for the top padding that is added to the bottom sheet
                    y = (offsetWrapper.offset - (swipeableState.offset ?: 0f) - bottomNavBarHeight - 8.dp.toPx()).roundToInt()
                )
            }) {
            navBarItems.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(if (selectedItem == item) item.selectedIcon else item.unselectedIcon, contentDescription = null)
                    },
                    label = { Text(stringResource(item.titleRes)) },
                    selected = selectedItem == item,
                    onClick = { selectedItem = item }
                )
            }
        }
    }
}

private val navBarItems = listOf(NavBarItem.Images, NavBarItem.Text, NavBarItem.Shapes, NavBarItem.Stickers, NavBarItem.Uploads)

private sealed class NavBarItem(@StringRes val titleRes: Int, val unselectedIcon: ImageVector, val selectedIcon: ImageVector) {
    object Text : NavBarItem(R.string.cesdk_text, IconPack.Textfields, IconPack.Textfields)
    object Images : NavBarItem(R.string.cesdk_images, IconPack.Imageoutline, IconPack.Image)
    object Shapes : NavBarItem(R.string.cesdk_shapes, IconPack.Shapesoutline, IconPack.Shapes)
    object Stickers : NavBarItem(R.string.cesdk_stickers, IconPack.Stickeremojioutline, IconPack.Stickeremoji)
    object Uploads : NavBarItem(R.string.cesdk_uploads, IconPack.Clouduploadoutline, IconPack.Cloudupload)
}