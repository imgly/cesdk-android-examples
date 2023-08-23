package ly.img.cesdk.core.data

import ly.img.cesdk.core.data.font.FontFamilyData
import ly.img.engine.Asset

internal sealed class WrappedAsset(
    val asset: Asset,
    val assetSource: AssetSource
) {
    class GenericAsset(asset: Asset, assetSource: AssetSource) : WrappedAsset(asset, assetSource)

    class TextAsset(
        asset: Asset, assetSource: AssetSource, val fontFamily: FontFamilyData
    ) : WrappedAsset(asset, assetSource)
}