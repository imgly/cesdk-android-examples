package ly.img.editor.core.ui.library.state

import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.ui.library.data.font.FontData
import ly.img.engine.Asset

sealed interface WrappedAsset {
    val asset: Asset
    val assetSourceType: AssetSourceType
    val assetType: AssetType

    data class GenericAsset(
        override val asset: Asset,
        override val assetSourceType: AssetSourceType,
        override val assetType: AssetType,
    ) : WrappedAsset

    class TextAsset(
        override val asset: Asset,
        override val assetSourceType: AssetSourceType,
        override val assetType: AssetType,
        val fontData: FontData?,
    ) : WrappedAsset
}
