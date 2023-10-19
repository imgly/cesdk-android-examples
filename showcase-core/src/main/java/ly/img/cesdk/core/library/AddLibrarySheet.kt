package ly.img.cesdk.core.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.cesdk.core.library.components.LibraryNavBarItem
import ly.img.cesdk.core.library.components.LibraryNavigationBar
import ly.img.cesdk.core.library.util.LibraryEvent
import ly.img.cesdk.core.ui.AnyComposable
import ly.img.cesdk.core.ui.bottomsheet.ModalBottomSheetValue
import ly.img.cesdk.core.ui.bottomsheet.SwipeableV2State
import kotlin.math.roundToInt

internal class OffsetWrapper(var offset: Float = 0f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLibrarySheet(
    swipeableState: SwipeableV2State<ModalBottomSheetValue>,
    onClose: () -> Unit,
    onCloseAssetDetails: () -> Unit,
    onSearchFocus: () -> Unit,
    showAnyComposable: (AnyComposable) -> Unit,
) {
    val viewModel = viewModel<LibraryViewModel>()

    val tabItems = remember(viewModel.libraryCategories) {
        viewModel.libraryCategories.map { LibraryNavBarItem.from(it) }
    }
    var selectedItemIndex by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 80.dp padding to accommodate for bottom nav bar
        Surface(modifier = Modifier.padding(bottom = 80.dp)) {
            AssetLibrary(
                libraryCategory = tabItems[selectedItemIndex].libraryCategory,
                onSearchFocus = onSearchFocus,
                onAssetClick = { assetSource, asset ->
                    viewModel.onEvent(LibraryEvent.OnAddAsset(assetSource, asset))
                    onClose()
                },
                onUriPick = { assetSource, uri ->
                    viewModel.onEvent(LibraryEvent.OnAddUri(assetSource, uri))
                    onClose()
                },
                showAnyComposable = showAnyComposable,
                onCloseAssetDetails = onCloseAssetDetails,
                onClose = onClose
            )
        }

        var bottomNavBarHeight by remember { mutableStateOf(0) }
        val offsetWrapper = remember(swipeableState.maxOffset) { OffsetWrapper() }

        if (offsetWrapper.offset == 0f) {
            swipeableState.let { offsetWrapper.offset = swipeableState.maxOffset }
        }

        LibraryNavigationBar(
            items = tabItems,
            modifier = Modifier
                .onGloballyPositioned {
                    bottomNavBarHeight = it.size.height
                }
                .absoluteOffset {
                    IntOffset(
                        x = 0,
                        // 8.dp is for the top padding that is added to the bottom sheet
                        y = (offsetWrapper.offset - (swipeableState.offset ?: 0f) - bottomNavBarHeight - 8.dp.toPx()).roundToInt()
                    )
                },
            selectedItemIndex = selectedItemIndex,
            onSelectionChange = { index ->
                selectedItemIndex = index
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(LibraryEvent.OnDispose)
        }
    }
}