package ly.img.cesdk.core.library.components.section

import androidx.compose.runtime.Composable
import ly.img.cesdk.core.data.AssetSource
import ly.img.cesdk.core.library.components.asset.AssetColumn
import ly.img.cesdk.core.library.components.asset.AssetRow
import ly.img.cesdk.core.library.state.AssetSourceGroupType
import ly.img.engine.Asset

@Composable
internal fun LibrarySectionContent(
    sectionItem: LibrarySectionItem.Content,
    onAssetClick: (AssetSource, Asset) -> Unit,
    onAssetLongClick: (AssetSource, Asset) -> Unit,
    onSeeAllClick: () -> Unit
) {
    when (val assetSourceGroupType = sectionItem.assetSourceGroupType) {
        AssetSourceGroupType.Audio, AssetSourceGroupType.Text -> AssetColumn(
            wrappedAssets = sectionItem.wrappedAssets,
            assetSourceGroupType = assetSourceGroupType,
            onAssetClick = onAssetClick,
            onAssetLongClick = onAssetLongClick
        )

        else -> AssetRow(
            wrappedAssets = sectionItem.wrappedAssets,
            moreAssetsAvailable = sectionItem.moreAssetsAvailable,
            assetSourceGroupType = assetSourceGroupType,
            onAssetClick = onAssetClick,
            onAssetLongClick = onAssetLongClick,
            onSeeAllClick = onSeeAllClick
        )
    }
}