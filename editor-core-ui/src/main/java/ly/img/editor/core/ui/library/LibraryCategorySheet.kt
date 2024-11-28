package ly.img.editor.core.ui.library

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.AnyComposable
import ly.img.editor.core.ui.library.state.WrappedAsset
import ly.img.editor.core.ui.library.util.LibraryEvent

@Composable
internal fun LibraryCategorySheet(
    libraryCategory: LibraryCategory,
    onAssetClick: (WrappedAsset) -> Unit,
    onUriPick: (UploadAssetSourceType, Uri) -> Unit,
    onClose: () -> Unit,
    onCloseAssetDetails: () -> Unit,
    onSearchFocus: () -> Unit,
    showAnyComposable: (AnyComposable) -> Unit,
    launchGetContent: (String, UploadAssetSourceType) -> Unit,
    launchCamera: (Boolean) -> Unit,
) {
    val viewModel = viewModel<LibraryViewModel>()

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        val uiState by viewModel.getAssetLibraryUiState(libraryCategory).collectAsState()
        AssetLibrary(
            uiState = uiState,
            onSearchFocus = onSearchFocus,
            onAssetClick = { wrappedAsset ->
                onAssetClick(wrappedAsset)
                onClose()
            },
            onUriPick = { assetSource, uri ->
                onUriPick(assetSource, uri)
                onClose()
            },
            showAnyComposable = showAnyComposable,
            onCloseAssetDetails = onCloseAssetDetails,
            launchGetContent = launchGetContent,
            launchCamera = launchCamera,
            onClose = onClose,
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(LibraryEvent.OnDispose)
        }
    }
}
