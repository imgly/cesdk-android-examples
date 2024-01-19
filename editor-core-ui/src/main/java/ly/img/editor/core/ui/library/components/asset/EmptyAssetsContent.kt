package ly.img.editor.core.ui.library.components.asset

import androidx.compose.runtime.Composable
import ly.img.editor.core.library.AssetSourceGroupType

@Composable
internal fun EmptyAssetsContent(
    assetSourceGroupType: AssetSourceGroupType
) {
    AssetsIntermediateStateContent(
        state = IntermediateState.Empty,
        assetSourceGroupType = assetSourceGroupType
    )
}
