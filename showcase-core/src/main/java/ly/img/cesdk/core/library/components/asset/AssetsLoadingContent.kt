package ly.img.cesdk.core.library.components.asset

import androidx.compose.runtime.Composable
import ly.img.cesdk.core.library.state.AssetSourceGroupType

@Composable
internal fun AssetsLoadingContent(assetSourceGroupType: AssetSourceGroupType) {
    AssetsIntermediateStateContent(
        state = IntermediateState.Loading,
        assetSourceGroupType = assetSourceGroupType,
        inGrid = true
    )
}