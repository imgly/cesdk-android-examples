package ly.img.editor.core.ui.library.components.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.ui.library.components.asset.AssetsIntermediateStateContent
import ly.img.editor.core.ui.library.components.asset.IntermediateState

@Composable
internal fun LibrarySectionContentLoadingContent(assetType: AssetType) {
    Column {
        AssetsIntermediateStateContent(
            state = IntermediateState.Loading,
            assetType = assetType,
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}
