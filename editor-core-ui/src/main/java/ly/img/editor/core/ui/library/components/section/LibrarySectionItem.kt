package ly.img.editor.core.ui.library.components.section

import androidx.annotation.StringRes
import ly.img.editor.core.library.AssetSourceGroupType
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.library.state.LibraryStackData
import ly.img.editor.core.ui.library.state.WrappedAsset
import kotlin.random.Random

internal sealed class LibrarySectionItem(val id: String) {
    data class Header(
        val stackData: LibraryStackData,
        @StringRes val titleRes: Int,
        val uploadAssetSource: UploadAssetSourceType?,
        val count: Int? = null,
    ) : LibrarySectionItem("Header ${stackData.hashCode()}")

    data class Content(
        val wrappedAssets: List<WrappedAsset>,
        val assetSourceGroupType: AssetSourceGroupType,
        val stackData: LibraryStackData,
        val moreAssetsAvailable: Boolean
    ) : LibrarySectionItem("Content ${stackData.hashCode()}")

    data class ContentLoading(
        val assetSourceGroupType: AssetSourceGroupType
    ) : LibrarySectionItem("Content Loading ${Random.nextInt()}")

    object Loading : LibrarySectionItem("Loading ${Random.nextInt()}")

    data class Error(
        val assetSourceGroupType: AssetSourceGroupType
    ) : LibrarySectionItem("Error ${Random.nextInt()}")
}