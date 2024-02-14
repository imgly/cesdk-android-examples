package ly.img.editor.core.ui.library.components.asset

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import ly.img.editor.core.ui.library.components.LibraryImageCard
import ly.img.editor.core.ui.library.getDuration
import ly.img.editor.core.ui.library.getThumbnailUri
import ly.img.editor.core.ui.library.state.WrappedAsset

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun AudioAssetContent(
    wrappedAsset: WrappedAsset,
    onAssetClick: (WrappedAsset) -> Unit,
    onAssetLongClick: (WrappedAsset) -> Unit,
) {
    ListItem(
        headlineContent = { Text(wrappedAsset.asset.label ?: "") },
        leadingContent = {
            LibraryImageCard(
                uri = wrappedAsset.asset.getThumbnailUri(),
                contentPadding = 0.dp,
                contentScale = ContentScale.Crop,
                tintImages = false,
                modifier = Modifier.size(56.dp),
            )
        },
        trailingContent = { Text(wrappedAsset.asset.getDuration()) },
        modifier =
            Modifier.combinedClickable(
                onClick = { onAssetClick(wrappedAsset) },
                onLongClick = { onAssetLongClick(wrappedAsset) },
            ),
    )
}
