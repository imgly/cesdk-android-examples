package ly.img.cesdk.core.library

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.cesdk.core.data.AssetSource
import ly.img.cesdk.core.data.UploadAssetSource
import ly.img.cesdk.core.library.components.LibraryContent
import ly.img.cesdk.core.library.components.LibrarySearchHeader
import ly.img.cesdk.core.library.components.asset.AssetCreditsContent
import ly.img.cesdk.core.library.state.LibraryCategory
import ly.img.cesdk.core.library.util.LibraryEvent
import ly.img.cesdk.core.library.util.LibraryUiEvent
import ly.img.cesdk.core.ui.AnyComposable
import ly.img.engine.Asset

@Composable
internal fun AssetLibrary(
    libraryCategory: LibraryCategory,
    onSearchFocus: () -> Unit,
    onAssetClick: (AssetSource, Asset) -> Unit,
    onUriPick: (UploadAssetSource, Uri) -> Unit,
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

    DisposableEffect(libraryCategory) {
        onDispose {
            viewModel.onEvent(LibraryEvent.OnDispose(libraryCategory))
        }
    }
}