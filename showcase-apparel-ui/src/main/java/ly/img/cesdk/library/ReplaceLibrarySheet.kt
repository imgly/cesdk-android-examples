package ly.img.cesdk.library

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.cesdk.apparel.Event
import ly.img.cesdk.engine.BlockType
import ly.img.cesdk.library.components.AssetsLibrary
import ly.img.cesdk.library.components.ImagesLibrary
import ly.img.cesdk.library.components.assets.AssetSource
import ly.img.cesdk.library.components.assets.ImageSource
import ly.img.engine.Asset
import ly.img.engine.DesignBlock

@Composable
fun ReplaceLibrarySheet(
    designBlock: DesignBlock,
    type: BlockType,
    onEvent: (Event) -> Unit,
    viewModel: LibraryViewModel = viewModel()
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        val onClose = remember { { onEvent(Event.HideSheet) } }
        when (type) {
            BlockType.Image -> ImagesLibrary(
                onClose = onClose,
                onSearchFocus = { onEvent(Event.ExpandSheet) },
                onImagePicked = { imageSource: ImageSource, asset: Asset ->
                    viewModel.replaceAsset(imageSource, asset, designBlock)
                },
                onImageUploaded = {
                    viewModel.replaceUploadedImage(it, designBlock)
                }
            )
            BlockType.Sticker -> AssetsLibrary(
                assetSource = AssetSource.Stickers,
                onSearchFocus = { onEvent(Event.ExpandSheet) },
                onClick = { assetSource: AssetSource, asset: Asset ->
                    viewModel.replaceAsset(assetSource, asset, designBlock)
                },
                onClose = onClose
            )
            else -> {
                throw IllegalArgumentException("Replace is supported only for images and stickers.")
            }
        }
    }
}
