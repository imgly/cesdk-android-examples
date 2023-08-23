package ly.img.cesdk.core.library.components.asset

import androidx.compose.runtime.Composable
import ly.img.cesdk.core.library.state.AssetSourceGroupType

@Composable
internal fun EmptyAssetsContent(
    assetSourceGroupType: AssetSourceGroupType
) {
    AssetsIntermediateStateContent(
        state = IntermediateState.Empty,
        assetSourceGroupType = assetSourceGroupType
    )
}