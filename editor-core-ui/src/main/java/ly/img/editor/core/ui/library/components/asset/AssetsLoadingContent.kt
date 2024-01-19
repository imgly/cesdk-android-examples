package ly.img.editor.core.ui.library.components.asset

import androidx.compose.runtime.Composable
import ly.img.editor.core.library.AssetSourceGroupType

@Composable
internal fun AssetsLoadingContent(assetSourceGroupType: AssetSourceGroupType) {
    AssetsIntermediateStateContent(
        state = IntermediateState.Loading,
        assetSourceGroupType = assetSourceGroupType,
        inGrid = true
    )
}
