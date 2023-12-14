package ly.img.cesdk.core.library.state

import androidx.annotation.StringRes
import ly.img.cesdk.core.data.AssetSource

internal data class AssetSourceGroup(
    @StringRes val titleRes: Int,
    val sources: List<AssetSource>,
    val type: AssetSourceGroupType
) {
    fun previewCount() = if (type == AssetSourceGroupType.Audio || type == AssetSourceGroupType.Text) 3 else 10
}

enum class AssetSourceGroupType {
    Image,
    Audio,
    Video,
    Gallery,
    Shape,
    Sticker,
    Text,
    Filter,
    Effect,
    Blur
}
