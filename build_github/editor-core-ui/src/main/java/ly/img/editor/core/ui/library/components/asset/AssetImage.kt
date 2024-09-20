package ly.img.editor.core.ui.library.components.asset

import androidx.compose.runtime.Composable
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.ui.library.components.LibraryImageCard
import ly.img.editor.core.ui.library.getThumbnailUri
import ly.img.editor.core.ui.library.state.WrappedAsset
import ly.img.editor.core.ui.library.util.AssetLibraryUiConfig

@Composable
internal fun AssetImage(
    wrappedAsset: WrappedAsset,
    assetType: AssetType,
    onAssetClick: (WrappedAsset) -> Unit,
    onAssetLongClick: (WrappedAsset) -> Unit,
) {
    LibraryImageCard(
        uri = wrappedAsset.asset.getThumbnailUri(),
        onClick = { onAssetClick(wrappedAsset) },
        onLongClick = { onAssetLongClick(wrappedAsset) },
        contentPadding = AssetLibraryUiConfig.contentPadding(assetType),
        contentScale = AssetLibraryUiConfig.contentScale(assetType),
        tintImages = AssetLibraryUiConfig.shouldTintImages(assetType),
    )
}
