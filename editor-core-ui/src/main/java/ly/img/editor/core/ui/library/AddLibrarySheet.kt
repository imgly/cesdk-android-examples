package ly.img.editor.core.ui.library

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.ui.AnyComposable
import ly.img.editor.core.ui.library.util.LibraryEvent

@Composable
fun AddLibrarySheet(
    libraryCategory: LibraryCategory,
    addToBackgroundTrack: Boolean?,
    onClose: () -> Unit,
    onCloseAssetDetails: () -> Unit,
    onSearchFocus: () -> Unit,
    showAnyComposable: (AnyComposable) -> Unit,
    launchCamera: (Boolean, (Uri) -> Unit) -> Unit,
) {
    val viewModel = viewModel<LibraryViewModel>()

    LibraryCategorySheet(
        libraryCategory = libraryCategory,
        onAssetClick = { wrappedAsset ->
            viewModel.onEvent(
                LibraryEvent.OnAddAsset(
                    wrappedAsset = wrappedAsset,
                    addToBackgroundTrack = addToBackgroundTrack,
                ),
            )
            onClose()
        },
        onUriPick = { assetSource, uri ->
            viewModel.onEvent(
                LibraryEvent.OnAddUri(
                    assetSource = assetSource,
                    uri = uri,
                    addToBackgroundTrack = addToBackgroundTrack,
                ),
            )
            onClose()
        },
        onClose = onClose,
        onCloseAssetDetails = onCloseAssetDetails,
        onSearchFocus = onSearchFocus,
        showAnyComposable = showAnyComposable,
        launchCamera = launchCamera,
    )
}
