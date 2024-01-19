package ly.img.editor.core.ui.library.components.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ly.img.editor.core.library.AssetSourceGroupType
import ly.img.editor.core.ui.library.components.asset.AssetsIntermediateStateContent
import ly.img.editor.core.ui.library.components.asset.IntermediateState

@Composable
internal fun LibrarySectionContentLoadingContent(assetSourceGroupType: AssetSourceGroupType) {
    Column {
        AssetsIntermediateStateContent(
            state = IntermediateState.Loading,
            assetSourceGroupType = assetSourceGroupType
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}