package ly.img.cesdk.core.library.components.grid

import androidx.compose.runtime.Composable
import ly.img.cesdk.core.data.AssetSource
import ly.img.cesdk.core.data.WrappedAsset
import ly.img.cesdk.core.library.components.asset.AssetImage
import ly.img.cesdk.core.library.components.asset.AudioAssetContent
import ly.img.cesdk.core.library.components.asset.TextAssetContent
import ly.img.cesdk.core.library.state.AssetSourceGroupType
import ly.img.engine.Asset

@Composable
internal fun AssetGridItemContent(
    wrappedAsset: WrappedAsset,
    assetSourceGroupType: AssetSourceGroupType,
    onAssetClick: (AssetSource, Asset) -> Unit,
    onAssetLongClick: (AssetSource, Asset) -> Unit,
) {
    if (assetSourceGroupType == AssetSourceGroupType.Audio) {
        AudioAssetContent(
            wrappedAsset = wrappedAsset,
            onAssetClick = onAssetClick,
            onAssetLongClick = onAssetLongClick
        )
    } else if (assetSourceGroupType == AssetSourceGroupType.Text) {
        TextAssetContent(
            wrappedAsset = wrappedAsset as WrappedAsset.TextAsset,
            onAssetClick = onAssetClick,
            onAssetLongClick = onAssetLongClick
        )
    } else {
        AssetImage(
            asset = wrappedAsset.asset,
            assetSourceGroupType = assetSourceGroupType,
            assetSource = wrappedAsset.assetSource,
            onAssetClick = onAssetClick,
            onAssetLongClick = onAssetLongClick
        )
    }
}