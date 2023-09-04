package ly.img.cesdk.core.library.components.asset

import androidx.compose.runtime.Composable
import ly.img.cesdk.core.data.AssetSource
import ly.img.cesdk.core.data.getThumbnailUri
import ly.img.cesdk.core.library.components.LibraryImageCard
import ly.img.cesdk.core.library.state.AssetSourceGroupType
import ly.img.cesdk.core.library.util.AssetLibraryUiConfig
import ly.img.engine.Asset

@Composable
internal fun AssetImage(
    asset: Asset,
    assetSourceGroupType: AssetSourceGroupType,
    assetSource: AssetSource,
    onAssetClick: (AssetSource, Asset) -> Unit,
    onAssetLongClick: (AssetSource, Asset) -> Unit
) {
    LibraryImageCard(
        uri = asset.getThumbnailUri(),
        onClick = { onAssetClick(assetSource, asset) },
        onLongClick = { onAssetLongClick(assetSource, asset) },
        contentPadding = AssetLibraryUiConfig.contentPadding(assetSourceGroupType),
        contentScale = AssetLibraryUiConfig.contentScale(assetSourceGroupType),
        tintImages = AssetLibraryUiConfig.shouldTintImages(assetSourceGroupType)
    )
}