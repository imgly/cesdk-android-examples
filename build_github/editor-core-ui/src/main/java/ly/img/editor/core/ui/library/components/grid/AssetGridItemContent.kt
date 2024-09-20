package ly.img.editor.core.ui.library.components.grid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.media3.exoplayer.ExoPlayer
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.ui.library.components.asset.AssetImage
import ly.img.editor.core.ui.library.components.asset.AudioAssetContent
import ly.img.editor.core.ui.library.components.asset.TextAssetContent
import ly.img.editor.core.ui.library.state.WrappedAsset

@Composable
internal fun AssetGridItemContent(
    wrappedAsset: WrappedAsset,
    assetType: AssetType,
    onAssetClick: (WrappedAsset) -> Unit,
    onAssetLongClick: (WrappedAsset) -> Unit,
    activatedPreviewItem: MutableState<String?> = mutableStateOf(""),
    exoPlayer: () -> ExoPlayer?,
) {
    when (assetType) {
        AssetType.Audio -> {
            AudioAssetContent(
                wrappedAsset = wrappedAsset,
                onAssetClick = onAssetClick,
                onAssetLongClick = onAssetLongClick,
                activatedPreviewItem = activatedPreviewItem,
                exoPlayer = exoPlayer,
            )
        }
        AssetType.Text -> {
            TextAssetContent(
                wrappedAsset = wrappedAsset as WrappedAsset.TextAsset,
                onAssetClick = onAssetClick,
                onAssetLongClick = onAssetLongClick,
            )
        }
        else -> {
            AssetImage(
                wrappedAsset = wrappedAsset,
                assetType = assetType,
                onAssetClick = onAssetClick,
                onAssetLongClick = onAssetLongClick,
            )
        }
    }
}
