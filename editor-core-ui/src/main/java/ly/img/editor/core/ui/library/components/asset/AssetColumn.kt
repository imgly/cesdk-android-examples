package ly.img.editor.core.ui.library.components.asset

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ly.img.editor.core.library.AssetSourceGroupType
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.ui.library.state.WrappedAsset
import ly.img.engine.Asset

@Composable
internal fun AssetColumn(
    wrappedAssets: List<WrappedAsset>,
    assetSourceGroupType: AssetSourceGroupType,
    onAssetLongClick: (AssetSourceType, Asset) -> Unit,
    onAssetClick: (AssetSourceType, Asset) -> Unit,
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