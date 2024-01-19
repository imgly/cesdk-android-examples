package ly.img.editor.core.ui.library

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.ui.AnyComposable
import ly.img.editor.core.ui.engine.BlockType
import ly.img.editor.core.ui.library.util.LibraryEvent
import ly.img.engine.DesignBlock

@Composable
fun ReplaceLibrarySheet(
    designBlock: DesignBlock,
    type: BlockType,
    onClose: () -> Unit,
    onCloseAssetDetails: () -> Unit,
    onSearchFocus: () -> Unit,
    showAnyComposable: (AnyComposable) -> Unit
) {
    val viewModel = viewModel<LibraryViewModel>()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        val libraryCategory =
            when (type) {
                BlockType.Sticker -> LibraryCategory.Stickers
                BlockType.Image -> LibraryCategory.Images
                else -> throw IllegalArgumentException(
                    "Replace is supported only for images and stickers."
                )
            }
        AssetLibrary(
            libraryCategory = libraryCategory,
            onSearchFocus = onSearchFocus,
            onAssetClick = { assetSource, asset ->
                viewModel.onEvent(LibraryEvent.OnReplaceAsset(assetSource, asset, designBlock))
                onClose()
            },
            onUriPick = { assetSource, uri ->
                viewModel.onEvent(LibraryEvent.OnReplaceUri(assetSource, uri, designBlock))
                onClose()
            },
            showAnyComposable = showAnyComposable,
            onCloseAssetDetails = onCloseAssetDetails,
            onClose = onClose
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(LibraryEvent.OnDispose)
        }
    }
}
