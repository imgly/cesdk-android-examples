package ly.img.cesdk.core.library.util

import ly.img.engine.AssetCredits
import ly.img.engine.AssetLicense

internal sealed interface LibraryUiEvent {
    data class ShowAssetCredits(
        val assetLabel: String,
        val assetCredits: AssetCredits?,
        val assetLicense: AssetLicense?,
        val assetSourceCredits: AssetCredits?,
        val assetSourceLicense: AssetLicense?,
    ) : LibraryUiEvent
}