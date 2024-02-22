package ly.img.editor.core.ui.library

import ly.img.editor.core.library.data.AssetSourceType

object AppearanceAssetSourceType {
    val DuoToneFilter by lazy {
        AssetSourceType(sourceId = "ly.img.filter.duotone")
    }
    val LutFilter by lazy {
        AssetSourceType(sourceId = "ly.img.filter.lut")
    }
    val FxEffect by lazy {
        AssetSourceType(sourceId = "ly.img.effect")
    }
    val Blur by lazy {
        AssetSourceType(sourceId = "ly.img.blur")
    }
}
