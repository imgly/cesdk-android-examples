package ly.img.cesdk.core.library.components.asset

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.cesdk.core.R
import ly.img.cesdk.core.data.AssetSource
import ly.img.cesdk.core.data.WrappedAsset
import ly.img.cesdk.core.iconpack.Arrowrightbig
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.library.state.AssetSourceGroupType
import ly.img.cesdk.core.library.util.AssetLibraryUiConfig
import ly.img.engine.Asset

@Composable
internal fun AssetRow(
    wrappedAssets: List<WrappedAsset>,
    onAssetClick: (AssetSource, Asset) -> Unit,
    onAssetLongClick: (AssetSource, Asset) -> Unit,
    onSeeAllClick: () -> Unit,
    assetSourceGroupType: AssetSourceGroupType,
    moreAssetsAvailable: Boolean
) {
    Column {
        if (wrappedAssets.isEmpty()) {
            EmptyAssetsContent(assetSourceGroupType = assetSourceGroupType)
        } else {
            val lazyRowState = rememberLazyListState()
            LaunchedEffect(wrappedAssets) {
                lazyRowState.scrollToItem(0)
            }
            LazyRow(
                modifier = Modifier.height(AssetLibraryUiConfig.contentRowHeight(assetSourceGroupType)),
                state = lazyRowState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(wrappedAssets) { wrappedAsset ->
                    AssetImage(
                        asset = wrappedAsset.asset,
                        assetSourceGroupType = assetSourceGroupType,
                        assetSource = wrappedAsset.assetSource,
                        onAssetClick = onAssetClick,
                        onAssetLongClick = onAssetLongClick
                    )
                }
                if (moreAssetsAvailable) {
                    item {
                        SeeAllButton(
                            assetSourceGroupType = assetSourceGroupType,
                            onClick = onSeeAllClick
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SeeAllButton(
    assetSourceGroupType: AssetSourceGroupType,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .size(AssetLibraryUiConfig.contentRowHeight(assetSourceGroupType))
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = IconPack.Arrowrightbig,
            modifier = Modifier
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), shape = CircleShape)
                .padding(12.dp),
            contentDescription = null
        )
        Text(
            text = stringResource(R.string.cesdk_see_all),
            style = MaterialTheme.typography.labelLarge
        )
    }
}