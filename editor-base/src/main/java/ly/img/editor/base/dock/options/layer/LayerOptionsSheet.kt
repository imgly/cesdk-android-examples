package ly.img.editor.base.dock.options.layer

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
import ly.img.editor.base.R
import ly.img.editor.base.components.ActionRow
import ly.img.editor.base.components.CardButton
import ly.img.editor.base.components.CheckedTextRow
import ly.img.editor.base.components.NestedSheetHeader
import ly.img.editor.base.components.PropertyLink
import ly.img.editor.base.components.PropertySlider
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.Delete
import ly.img.editor.core.iconpack.Duplicate
import ly.img.editor.core.ui.SheetHeader
import ly.img.editor.core.ui.UiDefaults
import ly.img.editor.core.ui.iconpack.Bringforward
import ly.img.editor.core.ui.iconpack.Bringtofront
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Sendbackward
import ly.img.editor.core.ui.iconpack.Sendtoback
import ly.img.editor.core.ui.sheetCardContentModifier
import ly.img.editor.core.ui.sheetScrollableContentModifier
import ly.img.editor.core.R as CoreR
import ly.img.editor.core.iconpack.IconPack as CoreIconPack

@Composable
fun LayerOptionsSheet(
    uiState: LayerUiState,
    onEvent: (EditorEvent) -> Unit,
) {
    var selectBlendMode by remember { mutableStateOf(false) }

    if (selectBlendMode) {
        BackHandler {
            selectBlendMode = false
        }
        Column {
            NestedSheetHeader(
                title = stringResource(R.string.ly_img_editor_blendmode),
                onBack = { selectBlendMode = false },
                onClose = { onEvent(EditorEvent.Sheet.Close(animate = true)) },
            )
            Card(
                colors = UiDefaults.cardColors,
                modifier = Modifier.sheetCardContentModifier(),
            ) {
                val selectedIndex =
                    remember(
                        uiState.blendMode,
                    ) { blendModesList.indexOfFirst { uiState.blendMode == it.second } }
                val lazyListState =
                    rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
                LazyColumn(state = lazyListState) {
                    items(blendModesList) {
                        val isChecked = uiState.blendMode == it.second
                        CheckedTextRow(
                            isChecked = isChecked,
                            text = stringResource(it.second),
                            onClick = {
                                onEvent(BlockEvent.OnChangeBlendMode(it.first))
                            },
                        )
                    }
                }
            }
        }
    } else {
        Column {
            SheetHeader(
                title = stringResource(id = CoreR.string.ly_img_editor_layer),
                onClose = { onEvent(EditorEvent.Sheet.Close(animate = true)) },
            )

            Column(
                Modifier.sheetScrollableContentModifier(),
            ) {
                if (uiState.opacity != null) {
                    PropertySlider(
                        title = stringResource(R.string.ly_img_editor_opacity),
                        value = uiState.opacity,
                        onValueChange = { onEvent(BlockEvent.OnChangeOpacity(it)) },
                        onValueChangeFinished = { onEvent(BlockEvent.OnChangeFinish) },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (uiState.blendMode != null) {
                    Card(
                        colors = UiDefaults.cardColors,
                    ) {
                        PropertyLink(
                            title = stringResource(id = R.string.ly_img_editor_blendmode),
                            value = stringResource(id = uiState.blendMode),
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
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CardButton(
                            text = stringResource(R.string.ly_img_editor_to_front),
                            icon = IconPack.Bringtofront,
                            modifier = Modifier.weight(1f),
                            enabled = uiState.canBringForward,
                            onClick = { onEvent(BlockEvent.ToFront) },
                        )
                        CardButton(
                            text = stringResource(R.string.ly_img_editor_forward),
                            icon = IconPack.Bringforward,
                            modifier = Modifier.weight(1f),
                            enabled = uiState.canBringForward,
                            onClick = { onEvent(BlockEvent.OnForward) },
                        )
                        CardButton(
                            text = stringResource(R.string.ly_img_editor_backward),
                            icon = IconPack.Sendbackward,
                            modifier = Modifier.weight(1f),
                            enabled = uiState.canSendBackward,
                            onClick = { onEvent(BlockEvent.OnBackward) },
                        )
                        CardButton(
                            text = stringResource(R.string.ly_img_editor_to_back),
                            icon = IconPack.Sendtoback,
                            modifier = Modifier.weight(1f),
                            enabled = uiState.canSendBackward,
                            onClick = { onEvent(BlockEvent.ToBack) },
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                val isDuplicateAllowed = uiState.isDuplicateAllowed
                val isDeleteAllowed = uiState.isDeleteAllowed
                if (isDeleteAllowed || isDuplicateAllowed) {
                    Card(
                        colors = UiDefaults.cardColors,
                    ) {
                        if (isDuplicateAllowed) {
                            ActionRow(
                                text = stringResource(CoreR.string.ly_img_editor_duplicate),
                                icon = CoreIconPack.Duplicate,
                                onClick = { onEvent(BlockEvent.OnDuplicate) },
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
                                    text = stringResource(CoreR.string.ly_img_editor_delete),
                                    icon = CoreIconPack.Delete,
                                    onClick = { onEvent(BlockEvent.OnDelete) },
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
