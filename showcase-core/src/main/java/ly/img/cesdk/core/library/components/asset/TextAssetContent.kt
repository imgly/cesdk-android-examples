package ly.img.cesdk.core.library.components.asset

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ly.img.cesdk.core.R
import ly.img.cesdk.core.data.AssetSource
import ly.img.cesdk.core.data.WrappedAsset
import ly.img.cesdk.core.data.getMeta
import ly.img.cesdk.core.iconpack.Add
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.theme.surface3
import ly.img.engine.Asset

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TextAssetContent(
    wrappedAsset: WrappedAsset.TextAsset,
    onAssetClick: (AssetSource, Asset) -> Unit,
    onAssetLongClick: (AssetSource, Asset) -> Unit
) {
    ListItem(
        modifier = Modifier
            .combinedClickable(
                onClick = { onAssetClick(wrappedAsset.assetSource, wrappedAsset.asset) },
                onLongClick = { onAssetLongClick(wrappedAsset.assetSource, wrappedAsset.asset) }
            ),
        headlineContent = {
            Text(
                text = wrappedAsset.asset.label ?: "",
                fontFamily = wrappedAsset.fontFamily.fontFamily,
                fontWeight = FontWeight(requireNotNull(wrappedAsset.asset.getMeta("fontWeight", "")).toInt()),
                fontSize = requireNotNull(wrappedAsset.asset.getMeta("fontSize", "")).toInt().sp,
                fontStyle = FontStyle.Normal,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        },
        trailingContent = {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.surface3)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = IconPack.Add,
                    contentDescription = stringResource(id = R.string.cesdk_add),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.Center)
                )
            }
        }
    )
}