package ly.img.cesdk.core.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import ly.img.cesdk.core.data.font.FontFamilyData
import ly.img.engine.Asset

sealed class WrappedAsset(
    val asset: Asset,
    val assetSource: AssetSource
) {

    var isSelected:MutableState<Boolean> = mutableStateOf(false)

    fun setSelected(selection: Boolean) {
        isSelected.value = selection
    }

    class GenericAsset(asset: Asset, assetSource: AssetSource) : WrappedAsset(asset, assetSource)

    class TextAsset(
        asset: Asset, assetSource: AssetSource, val fontFamily: FontFamilyData
    ) : WrappedAsset(asset, assetSource)
}