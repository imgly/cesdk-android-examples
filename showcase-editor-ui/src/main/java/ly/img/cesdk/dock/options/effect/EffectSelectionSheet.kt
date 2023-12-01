package ly.img.cesdk.dock.options.effect

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.cesdk.core.iconpack.Filteradjustments
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.library.SelectableAssetList

import ly.img.cesdk.core.ui.AnyComposable
import ly.img.cesdk.core.ui.SheetHeader
import ly.img.cesdk.dock.HalfHeightContainer
import ly.img.cesdk.editorui.Event

@Composable
fun EffectSelectionSheet(
    uiState: EffectUiState,
    onEvent: (Event) -> Unit,
    showAnyComposable: (AnyComposable) -> Unit,
) {

    val libraryCategory = uiState.libraryCategory
    val listState = rememberLazyListState()
    var screenState by remember { mutableStateOf(ScreenState.Main) }

    // The minHeight 204.dp is chosen to match the height of the "Main" screen, while you are on the "Adjustment" screen.
    HalfHeightContainer(minHeight = 204.dp) {
        when(screenState) {
            ScreenState.Main -> Column {
                SheetHeader(
                    title = stringResource(id = uiState.titleRes),
                    onClose = { onEvent(Event.OnHideSheet) }
                )
                SelectableAssetList(
                    libraryCategory = libraryCategory,
                    showAnyComposable = showAnyComposable,
                    selectedIcon = {
                        if (uiState.adjustments.isNotEmpty()) {
                            IconPack.Filteradjustments
                        } else null
                    },
                    listState = listState,
                    onCloseAssetDetails = { onEvent(Event.OnHideScrimSheet) },
                    onAssetReselected = {
                        uiState.updateEffectSelection(onEvent, it)
                        if (uiState.adjustments.isNotEmpty()) {
                            screenState = ScreenState.AdjustmentPage
                        }
                    },
                    onAssetSelected = { uiState.updateEffectSelection(onEvent, it) },
                    onAssetLongClick = {},
                    checkInitialSelection = uiState::checkSelection,
                )
            }
            ScreenState.AdjustmentPage -> {
                EffectOptionsSheet(
                    title = uiState.selectedAsset?.asset?.label ?: stringResource(id = uiState.titleRes),
                    onBack = { screenState = ScreenState.Main },
                    onEvent = onEvent,
                    adjustments = uiState.adjustments
                )
            }
        }
    }
}

enum class ScreenState {
    Main, AdjustmentPage,
}