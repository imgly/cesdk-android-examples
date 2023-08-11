package ly.img.cesdk.core.library.components.asset

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import ly.img.cesdk.core.data.AssetSource
import ly.img.cesdk.core.data.WrappedAsset
import ly.img.cesdk.core.data.getDuration
import ly.img.cesdk.core.data.getThumbnailUri
import ly.img.cesdk.core.library.components.LibraryImageCard
import ly.img.engine.Asset

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun AudioAssetContent(
    wrappedAsset: WrappedAsset,
    onAssetClick: (AssetSource, Asset) -> Unit,
    onAssetLongClick: (AssetSource, Asset) -> Unit
) {
    ListItem(
        headlineContent = { Text(wrappedAsset.asset.label ?: "") },
        leadingContent = {
            LibraryImageCard(
                uri = wrappedAsset.asset.getThumbnailUri(),
                contentPadding = 0.dp,
                contentScale = ContentScale.Crop,
                tintImages = false,
                modifier = Modifier.size(56.dp)
            )
        },
        trailingContent = { Text(wrappedAsset.asset.getDuration()) },
        modifier = Modifier.combinedClickable(
            onClick = { onAssetClick(wrappedAsset.assetSource, wrappedAsset.asset) },
            onLongClick = { onAssetLongClick(wrappedAsset.assetSource, wrappedAsset.asset) }
        )
    )
}