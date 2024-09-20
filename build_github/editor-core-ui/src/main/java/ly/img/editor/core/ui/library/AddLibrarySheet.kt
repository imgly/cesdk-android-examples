package ly.img.editor.core.ui.library

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.ui.AnyComposable
import ly.img.editor.core.ui.library.util.LibraryEvent

@Composable
fun AddLibrarySheet(
    libraryCategory: LibraryCategory,
    onClose: () -> Unit,
    onCloseAssetDetails: () -> Unit,
    onSearchFocus: () -> Unit,
    showAnyComposable: (AnyComposable) -> Unit,
) {
    val viewModel = viewModel<LibraryViewModel>()

    LibraryCategorySheet(
        libraryCategory = libraryCategory,
        onAssetClick = { wrappedAsset ->
            viewModel.onEvent(LibraryEvent.OnAddAsset(wrappedAsset))
            onClose()
        },
        onUriPick = { assetSource, uri ->
            viewModel.onEvent(LibraryEvent.OnAddUri(assetSource, uri))
            onClose()
        },
        onClose = onClose,
        onCloseAssetDetails = onCloseAssetDetails,
        onSearchFocus = onSearchFocus,
        showAnyComposable = showAnyComposable,
    )
}
