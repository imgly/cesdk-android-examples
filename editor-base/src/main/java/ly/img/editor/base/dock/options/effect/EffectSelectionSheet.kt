package ly.img.editor.base.dock.options.effect

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ly.img.editor.base.dock.options.fillstroke.ColorPickerSheet
import ly.img.editor.base.engine.AdjustmentState
import ly.img.editor.base.engine.EffectAndBlurOptions
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.core.ui.iconpack.Filteradjustments
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.library.SelectableAssetList
import ly.img.editor.core.ui.library.SelectableAssetListProvider
import ly.img.editor.core.ui.library.state.WrappedAsset

@Composable
fun EffectSelectionSheet(
    uiState: EffectUiState,
    onColorPickerActiveChanged: (active: Boolean) -> Unit,
    onEvent: (EditorEvent) -> Unit,
) {
    val listState = rememberLazyListState()
    var screenState by remember { mutableStateOf<ScreenState>(ScreenState.Main) }
    var assets by remember {
        mutableStateOf<List<WrappedAsset>>(emptyList())
    }
    var selectedAsset by remember {
        mutableStateOf<WrappedAsset?>(null)
    }
    SelectableAssetListProvider(uiState.libraryCategory) {
        assets = it
    }
    LaunchedEffect(assets, uiState.appliedEffectId) {
        when {
            // Close adjustments if the new effect has no adjustments
            screenState is ScreenState.AdjustmentPage && uiState.adjustments.isEmpty() -> {
                screenState = ScreenState.Main
            }
        }
        selectedAsset = uiState.getSelectedAsset(assets)
    }
    Column {
        when (screenState) {
            ScreenState.Main -> {
                SheetHeader(
                    title = stringResource(id = uiState.titleRes),
                    onClose = { onEvent(EditorEvent.Sheet.Close(animate = true)) },
                )
                SelectableAssetList(
                    modifier = Modifier.navigationBarsPadding(),
                    selectedAsset = selectedAsset,
                    libraryCategory = uiState.libraryCategory,
                    listState = listState,
                    selectedIcon = {
                        if (uiState.adjustments.isNotEmpty()) {
                            IconPack.Filteradjustments
                        } else {
                            null
                        }
                    },
                    onAssetSelected = {
                        uiState.onAssetSelected(onEvent, it)
                    },
                    onAssetReselected = {
                        if (uiState.adjustments.isNotEmpty()) {
                            screenState = ScreenState.AdjustmentPage
                        }
                    },
                    onAssetLongClick = {},
                )
            }

            is ScreenState.AdjustmentPage -> {
                EffectOptionsSheet(
                    title = selectedAsset?.asset?.label ?: stringResource(id = uiState.titleRes),
                    onBack = { screenState = ScreenState.Main },
                    openColorPicker = {
                        onColorPickerActiveChanged(true)
                        screenState = ScreenState.ColorPicker(it)
                    },
                    onEvent = onEvent,
                    adjustments = uiState.adjustments,
                )
            }

            is ScreenState.ColorPicker -> {
                val colorPickerState = screenState as ScreenState.ColorPicker
                uiState.adjustments.firstOrNull {
                    it.type == colorPickerState.type
                }?.let {
                    ColorPickerSheet(
                        color = (it.value as AdjustmentState.Value.Color).value,
                        title = stringResource(id = colorPickerState.type.nameRes),
                        showOpacity = false,
                        onBack = {
                            onColorPickerActiveChanged(false)
                            screenState = ScreenState.AdjustmentPage
                        },
                        onColorChange = { color ->
                            onEvent(BlockEvent.OnChangeEffectSettings(colorPickerState.type, AdjustmentState.Value.Color(color)))
                        },
                        onEvent = onEvent,
                    )
                }
            }
        }
    }
}

sealed interface ScreenState {
    data object Main : ScreenState

    data object AdjustmentPage : ScreenState

    data class ColorPicker(
        val type: EffectAndBlurOptions,
    ) : ScreenState
}
