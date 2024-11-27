package ly.img.editor.core.ui.library

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.core.library.data.UploadAssetSourceType
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
    showAnyComposable: (AnyComposable) -> Unit,
    launchGetContent: (String, UploadAssetSourceType, DesignBlock?) -> Unit,
    launchCamera: (Boolean, DesignBlock?) -> Unit,
) {
    val viewModel = viewModel<LibraryViewModel>()
    val libraryCategory =
        when (type) {
            BlockType.Sticker -> viewModel.replaceStickerCategory
            BlockType.Image -> viewModel.replaceImageCategory
            BlockType.Audio -> viewModel.replaceAudioCategory
            BlockType.Video -> viewModel.replaceVideoCategory
            else -> throw IllegalArgumentException(
                "Replace is not supported for ${type.name}.",
            )
        }
    LibraryCategorySheet(
        libraryCategory = libraryCategory,
        onAssetClick = { wrappedAsset ->
            viewModel.onEvent(LibraryEvent.OnReplaceAsset(wrappedAsset, designBlock))
            onClose()
        },
        onUriPick = { assetSource, uri ->
            viewModel.onEvent(LibraryEvent.OnReplaceUri(assetSource, uri, designBlock))
            onClose()
        },
        onClose = onClose,
        onCloseAssetDetails = onCloseAssetDetails,
        onSearchFocus = onSearchFocus,
        showAnyComposable = showAnyComposable,
        launchGetContent = { mimeType, uploadAssetSourceType ->
            launchGetContent(mimeType, uploadAssetSourceType, designBlock)
        },
        launchCamera = { launchCamera(it, designBlock) },
    )
}
