package ly.img.cesdk.dock.options.layer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.cesdk.apparel.BlockEvent
import ly.img.cesdk.apparel.Event
import ly.img.cesdk.apparelui.R
import ly.img.cesdk.core.UiDefaults
import ly.img.cesdk.core.components.ActionRow
import ly.img.cesdk.core.components.CardButton
import ly.img.cesdk.core.components.CheckedTextRow
import ly.img.cesdk.core.components.NestedSheetHeader
import ly.img.cesdk.core.components.PropertyLink
import ly.img.cesdk.core.components.PropertySlider
import ly.img.cesdk.core.components.SheetHeader
import ly.img.cesdk.core.iconpack.Bringforward
import ly.img.cesdk.core.iconpack.Bringtofront
import ly.img.cesdk.core.iconpack.Controlpointduplicate
import ly.img.cesdk.core.iconpack.Delete
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.iconpack.Sendbackward
import ly.img.cesdk.core.iconpack.Sendtoback
import ly.img.cesdk.core.inspectorSheetPadding
import ly.img.cesdk.dock.HalfHeightContainer

@Composable
fun LayerOptionsSheet(
    uiState: LayerUiState,
    onEvent: (Event) -> Unit
) {
    HalfHeightContainer {
        var selectBlendMode by remember { mutableStateOf(false) }

        if (selectBlendMode) {
            BackHandler {
                selectBlendMode = false
            }
            Column {
                NestedSheetHeader(
                    title = stringResource(R.string.cesdk_blendmode),
                    onBack = { selectBlendMode = false },
                    onClose = { onEvent(Event.HideSheet) }
                )
                Card(
                    colors = UiDefaults.cardColors,
                    modifier = Modifier.inspectorSheetPadding()
                ) {
                    val selectedIndex = remember(uiState.blendMode) { blendModesList.indexOfFirst { uiState.blendMode == it.second } }
                    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
                    LazyColumn(state = lazyListState) {
                        items(blendModesList) {
                            val isChecked = uiState.blendMode == it.second
                            CheckedTextRow(
                                isChecked = isChecked,
                                text = stringResource(it.second),
                                onClick = {
                                    onEvent(BlockEvent.OnChangeBlendMode(it.first))
                                }
                            )
                        }
                    }
                }
            }
        } else {
            Column {
                SheetHeader(
                    title = stringResource(id = R.string.cesdk_layer),
                    onClose = { onEvent(Event.HideSheet) }
                )

                Column(
                    Modifier
                        .inspectorSheetPadding()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (uiState.opacity != null) {
                        PropertySlider(
                            title = stringResource(R.string.cesdk_opacity),
                            value = uiState.opacity,
                            onValueChange = { onEvent(BlockEvent.OnChangeOpacity(it)) },
                            onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (uiState.blendMode != null) {
                        Card(
                            colors = UiDefaults.cardColors
                        ) {
                            PropertyLink(
                                title = stringResource(id = R.string.cesdk_blendmode),
                                value = stringResource(id = uiState.blendMode)
                            ) {
                                selectBlendMode = true
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (uiState.isMoveAllowed) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CardButton(
                                text = stringResource(R.string.cesdk_to_top),
                                icon = IconPack.Bringtofront,
                                modifier = Modifier.weight(1f),
                                enabled = uiState.canBringForward,
                                onClick = { onEvent(BlockEvent.ToTop) }
                            )
                            CardButton(
                                text = stringResource(R.string.cesdk_up),
                                icon = IconPack.Bringforward,
                                modifier = Modifier.weight(1f),
                                enabled = uiState.canBringForward,
                                onClick = { onEvent(BlockEvent.OnUp) }
                            )
                            CardButton(
                                text = stringResource(R.string.cesdk_down),
                                icon = IconPack.Sendbackward,
                                modifier = Modifier.weight(1f),
                                enabled = uiState.canSendBackward,
                                onClick = { onEvent(BlockEvent.OnDown) }
                            )
                            CardButton(
                                text = stringResource(R.string.cesdk_to_bottom),
                                icon = IconPack.Sendtoback,
                                modifier = Modifier.weight(1f),
                                enabled = uiState.canSendBackward,
                                onClick = { onEvent(BlockEvent.ToBottom) }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    val isDuplicateAllowed = uiState.isDuplicateAllowed
                    val isDeleteAllowed = uiState.isDeleteAllowed
                    if (isDeleteAllowed || isDuplicateAllowed) {
                        Card(
                            colors = UiDefaults.cardColors
                        ) {
                            if (isDuplicateAllowed) {
                                ActionRow(
                                    text = stringResource(R.string.cesdk_duplicate),
                                    icon = IconPack.Controlpointduplicate,
                                    onClick = { onEvent(BlockEvent.OnDuplicate) }
                                )
                            }
                            if (isDeleteAllowed && isDuplicateAllowed) {
                                Divider(Modifier.padding(horizontal = 16.dp))
                            }
                            if (isDeleteAllowed) {
                                CompositionLocalProvider(
                                    LocalContentColor provides MaterialTheme.colorScheme.error,
                                ) {
                                    ActionRow(
                                        text = stringResource(R.string.cesdk_delete),
                                        icon = IconPack.Delete,
                                        onClick = { onEvent(BlockEvent.OnDelete) }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}
