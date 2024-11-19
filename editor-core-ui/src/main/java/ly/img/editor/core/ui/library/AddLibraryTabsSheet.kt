package ly.img.editor.core.ui.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.compose.bottomsheet.ModalBottomSheetValue
import ly.img.editor.compose.bottomsheet.SwipeableV2State
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.AnyComposable
import ly.img.editor.core.ui.library.components.LibraryNavigationBar
import ly.img.editor.core.ui.library.util.LibraryEvent
import ly.img.engine.DesignBlock
import kotlin.math.roundToInt

internal class OffsetWrapper(var offset: Float = 0f)

@Composable
fun AddLibraryTabsSheet(
    swipeableState: SwipeableV2State<ModalBottomSheetValue>,
    onClose: () -> Unit,
    onCloseAssetDetails: () -> Unit,
    onSearchFocus: () -> Unit,
    showAnyComposable: (AnyComposable) -> Unit,
    launchGetContent: (String, UploadAssetSourceType, DesignBlock?) -> Unit,
    launchCamera: (Boolean, DesignBlock?) -> Unit,
) {
    val viewModel = viewModel<LibraryViewModel>()

    val tabItems = viewModel.navBarItems
    var selectedItemIndex by remember { mutableStateOf(0) }
    val libraryCategory = tabItems[selectedItemIndex]
    val uiState by viewModel.getAssetLibraryUiState(libraryCategory).collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        // 80.dp padding to accommodate for bottom nav bar
        Surface(modifier = Modifier.padding(bottom = 80.dp)) {
            AssetLibrary(
                uiState = uiState,
                onSearchFocus = onSearchFocus,
                onAssetClick = {
                    viewModel.onEvent(LibraryEvent.OnAddAsset(it))
                    onClose()
                },
                onUriPick = { assetSource, uri ->
                    viewModel.onEvent(LibraryEvent.OnAddUri(assetSource, uri))
                    onClose()
                },
                launchGetContent = { mimeType, uploadAssetSourceType ->
                    launchGetContent(mimeType, uploadAssetSourceType, null)
                },
                launchCamera = { launchCamera(it, null) },
                showAnyComposable = showAnyComposable,
                onCloseAssetDetails = onCloseAssetDetails,
                onClose = onClose,
            )
        }

        var bottomNavBarHeight by remember { mutableStateOf(0) }
        val offsetWrapper = remember(swipeableState.maxOffset) { OffsetWrapper() }

        if (offsetWrapper.offset == 0f) {
            swipeableState.let { offsetWrapper.offset = swipeableState.maxOffset }
        }

        LibraryNavigationBar(
            items = tabItems,
            modifier =
                Modifier
                    .testTag(tag = "LibraryNavigationBar")
                    .onGloballyPositioned {
                        bottomNavBarHeight = it.size.height
                    }
                    .absoluteOffset {
                        IntOffset(
                            x = 0,
                            // 8.dp is for the top padding that is added to the bottom sheet
                            y =
                                (
                                    offsetWrapper.offset - (
                                        swipeableState.offset
                                            ?: 0f
                                    ) - bottomNavBarHeight - 8.dp.toPx()
                                ).roundToInt(),
                        )
                    },
            selectedItemIndex = selectedItemIndex,
            onSelectionChange = { index ->
                selectedItemIndex = index
            },
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(LibraryEvent.OnDispose)
        }
    }
}
