package ly.img.editor.core.ui.library.state

import ly.img.editor.core.library.data.AssetSourceType
import ly.img.engine.FindAssetsResult

internal data class WrappedFindAssetsResult(
    val assetSourceType: AssetSourceType,
    val findAssetsResult: FindAssetsResult,
)
