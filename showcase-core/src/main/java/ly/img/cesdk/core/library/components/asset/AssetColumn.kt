package ly.img.cesdk.core.library.components.asset

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ly.img.cesdk.core.data.AssetSource
import ly.img.cesdk.core.data.WrappedAsset
import ly.img.cesdk.core.library.state.AssetSourceGroupType
import ly.img.engine.Asset

@Composable
internal fun AssetColumn(
    wrappedAssets: List<WrappedAsset>,
    assetSourceGroupType: AssetSourceGroupType,
    onAssetLongClick: (AssetSource, Asset) -> Unit,
    onAssetClick: (AssetSource, Asset) -> Unit,
) {
    Column(Modifier.animateContentSize()) {
        if (wrappedAssets.isEmpty()) {
            EmptyAssetsContent(assetSourceGroupType)
        } else {
            Column {
                wrappedAssets.forEach { wrappedAsset ->
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
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}