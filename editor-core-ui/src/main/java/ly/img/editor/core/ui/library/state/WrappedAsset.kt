package ly.img.editor.core.ui.library.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.ui.library.data.font.FontFamilyData
import ly.img.engine.Asset

sealed class WrappedAsset(
    val asset: Asset,
    val assetSourceType: AssetSourceType
) {

    var isSelected: MutableState<Boolean> = mutableStateOf(false)

    fun setSelected(selection: Boolean) {
        isSelected.value = selection
    }

    class GenericAsset(asset: Asset, assetSourceType: AssetSourceType) : WrappedAsset(asset, assetSourceType)

    class TextAsset(
        asset: Asset, assetSourceType: AssetSourceType, val fontFamily: FontFamilyData
    ) : WrappedAsset(asset, assetSourceType)
}