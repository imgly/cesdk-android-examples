package ly.img.editor.core.ui.library.components.asset

import androidx.compose.runtime.Composable
import ly.img.editor.core.library.AssetSourceGroupType
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.ui.library.components.LibraryImageCard
import ly.img.editor.core.ui.library.getThumbnailUri
import ly.img.editor.core.ui.library.util.AssetLibraryUiConfig
import ly.img.engine.Asset

@Composable
internal fun AssetImage(
    asset: Asset,
    assetSourceGroupType: AssetSourceGroupType,
    assetSourceType: AssetSourceType,
    onAssetClick: (AssetSourceType, Asset) -> Unit,
    onAssetLongClick: (AssetSourceType, Asset) -> Unit
) {
    LibraryImageCard(
        uri = asset.getThumbnailUri(),
        onClick = { onAssetClick(assetSourceType, asset) },
        onLongClick = { onAssetLongClick(assetSourceType, asset) },
        contentPadding = AssetLibraryUiConfig.contentPadding(assetSourceGroupType),
        contentScale = AssetLibraryUiConfig.contentScale(assetSourceGroupType),
        tintImages = AssetLibraryUiConfig.shouldTintImages(assetSourceGroupType)
    )
}