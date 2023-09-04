package ly.img.cesdk.core.library.components.section

import androidx.annotation.StringRes
import ly.img.cesdk.core.data.UploadAssetSource
import ly.img.cesdk.core.data.WrappedAsset
import ly.img.cesdk.core.library.state.AssetSourceGroupType
import ly.img.cesdk.core.library.state.LibraryStackData
import kotlin.random.Random

internal sealed class LibrarySectionItem(val id: String) {
    data class Header(
        val stackData: LibraryStackData,
        @StringRes val titleRes: Int,
        val uploadAssetSource: UploadAssetSource?,
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