package ly.img.editor.core.ui.library.components.asset

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
import ly.img.editor.core.R
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.ui.iconpack.Arrowrightbig
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.library.state.WrappedAsset
import ly.img.editor.core.ui.library.util.AssetLibraryUiConfig

@Composable
internal fun AssetRow(
    wrappedAssets: List<WrappedAsset>,
    expandContent: LibraryContent?,
    onAssetClick: (WrappedAsset) -> Unit,
    onAssetLongClick: (WrappedAsset) -> Unit,
    onSeeAllClick: (LibraryContent) -> Unit,
    assetType: AssetType,
) {
    Column {
        if (wrappedAssets.isEmpty()) {
            EmptyAssetsContent(assetType = assetType)
        } else {
            val lazyRowState = rememberLazyListState()
            LaunchedEffect(wrappedAssets) {
                lazyRowState.scrollToItem(0)
            }
            LazyRow(
                modifier = Modifier.height(AssetLibraryUiConfig.contentRowHeight(assetType)),
                state = lazyRowState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(wrappedAssets) { wrappedAsset ->
                    AssetImage(
                        wrappedAsset = wrappedAsset,
                        assetType = assetType,
                        onAssetClick = onAssetClick,
                        onAssetLongClick = onAssetLongClick,
                    )
                }
                if (expandContent != null) {
                    item {
                        SeeAllButton(
                            assetType = assetType,
                            onClick = { onSeeAllClick(expandContent) },
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
    assetType: AssetType,
    onClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .size(AssetLibraryUiConfig.contentRowHeight(assetType))
                .clip(shape = MaterialTheme.shapes.medium)
                .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = IconPack.Arrowrightbig,
            modifier =
                Modifier
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), shape = CircleShape)
                    .padding(12.dp),
            contentDescription = null,
        )
        Text(
            text = stringResource(R.string.ly_img_editor_see_all),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
