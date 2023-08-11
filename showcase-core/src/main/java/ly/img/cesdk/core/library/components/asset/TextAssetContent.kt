package ly.img.cesdk.core.library.components.asset

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ly.img.cesdk.core.data.AssetSource
import ly.img.cesdk.core.data.WrappedAsset
import ly.img.cesdk.core.data.getMeta
import ly.img.engine.Asset

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TextAssetContent(
    wrappedAsset: WrappedAsset.TextAsset,
    onAssetClick: (AssetSource, Asset) -> Unit,
    onAssetLongClick: (AssetSource, Asset) -> Unit
) {
    ListItem(
        modifier = Modifier.combinedClickable(
            onClick = { onAssetClick(wrappedAsset.assetSource, wrappedAsset.asset) },
            onLongClick = { onAssetLongClick(wrappedAsset.assetSource, wrappedAsset.asset) }
        ),
        headlineContent = {
            Text(
                text = wrappedAsset.asset.label ?: "",
                fontFamily = wrappedAsset.fontFamily.fontFamily,
                fontWeight = FontWeight(requireNotNull(wrappedAsset.asset.getMeta("fontWeight", "")).toInt()),
                fontSize = requireNotNull(wrappedAsset.asset.getMeta("fontSize", "")).toInt().sp,
                fontStyle = FontStyle.Normal
            )
        }
    )
}