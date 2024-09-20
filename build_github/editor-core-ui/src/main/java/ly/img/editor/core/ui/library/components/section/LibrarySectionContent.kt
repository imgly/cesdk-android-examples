package ly.img.editor.core.ui.library.components.section

import androidx.compose.runtime.Composable
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.ui.library.components.asset.AssetColumn
import ly.img.editor.core.ui.library.components.asset.AssetRow
import ly.img.editor.core.ui.library.state.WrappedAsset

@Composable
internal fun LibrarySectionContent(
    sectionItem: LibrarySectionItem.Content,
    onAssetClick: (WrappedAsset) -> Unit,
    onAssetLongClick: (WrappedAsset) -> Unit,
    onSeeAllClick: (LibraryContent) -> Unit,
) {
    when (val assetType = sectionItem.assetType) {
        AssetType.Audio, AssetType.Text ->
            AssetColumn(
                wrappedAssets = sectionItem.wrappedAssets,
                assetType = assetType,
                onAssetClick = onAssetClick,
                onAssetLongClick = onAssetLongClick,
            )

        else ->
            AssetRow(
                wrappedAssets = sectionItem.wrappedAssets,
                expandContent = sectionItem.expandContent,
                assetType = assetType,
                onAssetClick = onAssetClick,
                onAssetLongClick = onAssetLongClick,
                onSeeAllClick = onSeeAllClick,
            )
    }
}
