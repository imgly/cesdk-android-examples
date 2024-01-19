package ly.img.editor.core.ui.library

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.AnyComposable
import ly.img.editor.core.ui.library.components.LibraryContent
import ly.img.editor.core.ui.library.components.LibrarySearchHeader
import ly.img.editor.core.ui.library.components.asset.AssetCreditsContent
import ly.img.editor.core.ui.library.util.LibraryEvent
import ly.img.editor.core.ui.library.util.LibraryUiEvent
import ly.img.engine.Asset

@Composable
internal fun AssetLibrary(
    libraryCategory: LibraryCategory,
    onSearchFocus: () -> Unit,
    onAssetClick: (AssetSourceType, Asset) -> Unit,
    onUriPick: (UploadAssetSourceType, Uri) -> Unit,
    showAnyComposable: (AnyComposable) -> Unit,
    onClose: () -> Unit,
    onCloseAssetDetails: () -> Unit,
    viewModel: LibraryViewModel = viewModel()
) {
    val uiState = viewModel.getAssetLibraryUiState(libraryCategory).collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect {
            when (it) {
                is LibraryUiEvent.ShowAssetCredits -> {
                    showAnyComposable(object : AnyComposable() {
                        @Composable
                        override fun ComposableContent() {
                            AssetCreditsContent(
                                it,
                                onCloseAssetDetails
                            )
                        }
                    })
                }
            }
        }
    }

    Column {
        LibrarySearchHeader(
            uiState = uiState,
            onLibraryEvent = viewModel::onEvent,
            onBack = onClose,
            onSearchFocus = onSearchFocus
        )

        LibraryContent(
            uiState = uiState,
            onAssetClick = onAssetClick,
            onUriPick = onUriPick,
            onLibraryEvent = viewModel::onEvent
        )

        BackHandler(!uiState.value.isRoot) {
            viewModel.onEvent(LibraryEvent.OnPopStack(libraryCategory))
        }

        BackHandler(uiState.value.isInSearchMode) {
            viewModel.onEvent(LibraryEvent.OnEnterSearchMode(false, libraryCategory))
        }
    }
}