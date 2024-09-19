package ly.img.editor.core.ui.library.util

import ly.img.engine.AssetCredits
import ly.img.engine.AssetLicense

sealed interface LibraryUiEvent {
    data class ShowAssetCredits(
        val assetLabel: String,
        val assetCredits: AssetCredits?,
        val assetLicense: AssetLicense?,
        val assetSourceCredits: AssetCredits?,
        val assetSourceLicense: AssetLicense?,
    ) : LibraryUiEvent
}
