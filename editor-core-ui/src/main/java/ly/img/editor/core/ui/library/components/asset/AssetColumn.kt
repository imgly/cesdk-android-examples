package ly.img.editor.core.ui.library.components.asset

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.ui.library.state.WrappedAsset

@Composable
internal fun AssetColumn(
    wrappedAssets: List<WrappedAsset>,
    assetType: AssetType,
    onAssetLongClick: (WrappedAsset) -> Unit,
    onAssetClick: (WrappedAsset) -> Unit,
) {
    val activatedPreviewItemId = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val exoPlayerInstance by
        remember {
            lazy {
                ExoPlayer.Builder(context.applicationContext).build()
            }
        }

    DisposableEffect(exoPlayerInstance) {
        onDispose {
            exoPlayerInstance.release()
        }
    }

    Column(Modifier.animateContentSize()) {
        if (wrappedAssets.isEmpty()) {
            EmptyAssetsContent(assetType)
        } else {
            Column {
                wrappedAssets.forEach { wrappedAsset ->
                    if (assetType == AssetType.Audio) {
                        AudioAssetContent(
                            wrappedAsset = wrappedAsset,
                            onAssetClick = onAssetClick,
                            onAssetLongClick = onAssetLongClick,
                            activatedPreviewItem = activatedPreviewItemId,
                            exoPlayer = { exoPlayerInstance },
                        )
                    } else if (assetType == AssetType.Text) {
                        TextAssetContent(
                            wrappedAsset = wrappedAsset as WrappedAsset.TextAsset,
                            onAssetClick = onAssetClick,
                            onAssetLongClick = onAssetLongClick,
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
