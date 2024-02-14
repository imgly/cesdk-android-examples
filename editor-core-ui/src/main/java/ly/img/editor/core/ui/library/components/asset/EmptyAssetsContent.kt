package ly.img.editor.core.ui.library.components.asset

import androidx.compose.runtime.Composable
import ly.img.editor.core.library.AssetType

@Composable
internal fun EmptyAssetsContent(assetType: AssetType) {
    AssetsIntermediateStateContent(
        state = IntermediateState.Empty,
        assetType = assetType,
    )
}
