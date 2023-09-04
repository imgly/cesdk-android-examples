package ly.img.cesdk.core.data

import ly.img.engine.FindAssetsResult

internal data class WrappedFindAssetsResult(
    val assetSource: AssetSource,
    val findAssetsResult: FindAssetsResult
)
