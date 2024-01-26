package ly.img.editor.core.library

import androidx.annotation.StringRes
import ly.img.editor.core.library.data.AssetSourceType

data class AssetSourceGroup(
    @StringRes val titleRes: Int,
    val sources: List<AssetSourceType>,
    val type: AssetSourceGroupType
)

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
