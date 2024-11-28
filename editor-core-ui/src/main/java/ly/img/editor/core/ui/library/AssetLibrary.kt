package ly.img.editor.core.ui.library

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.AnyComposable
import ly.img.editor.core.ui.library.components.LibraryContent
import ly.img.editor.core.ui.library.components.LibrarySearchHeader
import ly.img.editor.core.ui.library.components.asset.AssetCreditsContent
import ly.img.editor.core.ui.library.state.AssetLibraryUiState
import ly.img.editor.core.ui.library.state.WrappedAsset
import ly.img.editor.core.ui.library.util.LibraryEvent
import ly.img.editor.core.ui.library.util.LibraryUiEvent

@Composable
internal fun AssetLibrary(
    uiState: AssetLibraryUiState,
    onSearchFocus: () -> Unit,
    onAssetClick: (WrappedAsset) -> Unit,
    onUriPick: (UploadAssetSourceType, Uri) -> Unit,
    showAnyComposable: (AnyComposable) -> Unit,
    onClose: () -> Unit,
    onCloseAssetDetails: () -> Unit,
    launchGetContent: (String, UploadAssetSourceType) -> Unit,
    launchCamera: (Boolean) -> Unit,
    viewModel: LibraryViewModel = viewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect {
            when (it) {
                is LibraryUiEvent.ShowAssetCredits -> {
                    showAnyComposable(
                        object : AnyComposable() {
                            @Composable
                            override fun ComposableContent() {
                                AssetCreditsContent(
                                    it,
                                    onCloseAssetDetails,
                                )
                            }
                        },
                    )
                }
            }
        }
    }

    Column {
        LibrarySearchHeader(
            uiState = uiState,
            onLibraryEvent = viewModel::onEvent,
            onBack = onClose,
            onSearchFocus = onSearchFocus,
        )

        LibraryContent(
            uiState = uiState,
            onAssetClick = onAssetClick,
            onUriPick = onUriPick,
            onLibraryEvent = viewModel::onEvent,
            launchGetContent = launchGetContent,
            launchCamera = launchCamera,
        )

        BackHandler(!uiState.isRoot) {
            viewModel.onEvent(LibraryEvent.OnPopStack(uiState.libraryCategory))
        }

        BackHandler(uiState.isInSearchMode) {
            viewModel.onEvent(LibraryEvent.OnEnterSearchMode(false, uiState.libraryCategory))
        }
    }
}
