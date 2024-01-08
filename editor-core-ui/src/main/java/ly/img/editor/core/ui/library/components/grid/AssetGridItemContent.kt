package ly.img.editor.core.ui.library.components.grid

import androidx.compose.runtime.Composable
import ly.img.editor.core.library.AssetSourceGroupType
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.ui.library.components.asset.AssetImage
import ly.img.editor.core.ui.library.components.asset.AudioAssetContent
import ly.img.editor.core.ui.library.components.asset.TextAssetContent
import ly.img.editor.core.ui.library.state.WrappedAsset
import ly.img.engine.Asset

@Composable
internal fun AssetGridItemContent(
    wrappedAsset: WrappedAsset,
    assetSourceGroupType: AssetSourceGroupType,
    onAssetClick: (AssetSourceType, Asset) -> Unit,
    onAssetLongClick: (AssetSourceType, Asset) -> Unit,
) {
    when (assetSourceGroupType) {
        AssetSourceGroupType.Audio -> {
            AudioAssetContent(
                wrappedAsset = wrappedAsset,
                onAssetClick = onAssetClick,
                onAssetLongClick = onAssetLongClick
            )
        }
        AssetSourceGroupType.Text -> {
            TextAssetContent(
                wrappedAsset = wrappedAsset as WrappedAsset.TextAsset,
                onAssetClick = onAssetClick,
                onAssetLongClick = onAssetLongClick
            )
        }
        else -> {
            AssetImage(
                asset = wrappedAsset.asset,
                assetSourceGroupType = assetSourceGroupType,
                assetSourceType = wrappedAsset.assetSourceType,
                onAssetClick = onAssetClick,
                onAssetLongClick = onAssetLongClick
            )
        }
    }
}