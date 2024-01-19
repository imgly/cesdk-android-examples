package ly.img.editor.core.ui.library.components.section

import androidx.compose.runtime.Composable
import ly.img.editor.core.library.AssetSourceGroupType
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.ui.library.components.asset.AssetColumn
import ly.img.editor.core.ui.library.components.asset.AssetRow
import ly.img.engine.Asset

@Composable
internal fun LibrarySectionContent(
    sectionItem: LibrarySectionItem.Content,
    onAssetClick: (AssetSourceType, Asset) -> Unit,
    onAssetLongClick: (AssetSourceType, Asset) -> Unit,
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