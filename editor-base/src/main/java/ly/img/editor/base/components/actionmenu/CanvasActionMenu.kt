package ly.img.editor.base.components.actionmenu

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.base.R
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.core.iconpack.Delete
import ly.img.editor.core.iconpack.Duplicate
import ly.img.editor.core.ui.iconpack.Bringforward
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.NavigateBefore
import ly.img.editor.core.ui.iconpack.NavigateNext
import ly.img.editor.core.ui.iconpack.Sendbackward
import ly.img.editor.core.ui.utils.roundToPx
import ly.img.editor.core.ui.utils.toPx
import kotlin.math.cos
import kotlin.math.roundToInt
import ly.img.editor.core.R as CoreR
import ly.img.editor.core.iconpack.IconPack as CoreIconPack

@Composable
fun CanvasActionMenu(
    uiState: CanvasActionMenuUiState,
    onEvent: (BlockEvent) -> Unit,
) {
    var page by remember(uiState.selectedBlock) {
        mutableStateOf(if (uiState.firstPageExists()) 0 else 1)
    }

    if (!uiState.show) return

    val rotation = uiState.selectedBlockRotation
    val gizmoLength = if (uiState.isGizmoPresent) 48.dp.toPx() else 0f
    val topPadding = 24.dp.roundToPx()
    val sideMargin = 16.dp.roundToPx()
    val cos = cos(rotation)
    val dy = (cos * gizmoLength).roundToInt()

    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shadowElevation = 1.dp,
        modifier =
            Modifier
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    val width = placeable.width
                    val height = placeable.height
                    layout(width, height) {
                        // In certain scenarios (eg. changing theme while the Canvas Action Menu is visible),
                        // it was observed that minWidth = maxWidth = 0. Not sure why this happens, for now, we just return here.
                        if (constraints.isZero) return@layout

                        val x = uiState.selectedBlockRect.centerX().dp.roundToPx() - width / 2
                        val minX = constraints.minWidth + sideMargin
                        val maxX = constraints.maxWidth - width - sideMargin
                        val constrainedX = x.coerceIn(minX, maxX)

                        // Preference order -
                        // 1. Top
                        // 2. Bottom
                        // 3. Below top handle
                        fun calculateConstrainedY(): Int {
                            val blockCenterY = uiState.selectedBlockRect.centerY()
                            val blockHeight = uiState.selectedBlockRect.height()
                            val minY = constraints.minHeight + uiState.currentInsets.top.dp.roundToPx()
                            val topY =
                                (blockCenterY - blockHeight / 2).dp.roundToPx() - height - topPadding + if (dy < 0) dy else 0
                            if (topY > minY) {
                                return topY
                            }
                            val bottomY = (blockCenterY + blockHeight / 2).dp.roundToPx() + topPadding + if (dy > 0) dy else 0
                            val bottomCutOff = constraints.maxHeight - uiState.currentInsets.bottom.dp.roundToPx()
                            if (bottomY + height + sideMargin <= bottomCutOff) {
                                return bottomY
                            }
                            return (blockCenterY - blockHeight / 2).dp.roundToPx() + sideMargin + if (dy < 0) dy else 0
                        }

                        val constrainedY = calculateConstrainedY()
                        placeable.place(constrainedX, constrainedY)
                    }
                },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (page == 0) {
                if (uiState.isDuplicateAllowed) {
                    IconButton(onClick = { onEvent(BlockEvent.OnDuplicate) }) {
                        Icon(
                            CoreIconPack.Duplicate,
                            contentDescription = stringResource(CoreR.string.ly_img_editor_duplicate),
                        )
                    }
                }
                if (uiState.isDeleteAllowed) {
                    IconButton(onClick = { onEvent(BlockEvent.OnDelete) }) {
                        Icon(
                            CoreIconPack.Delete,
                            contentDescription = stringResource(CoreR.string.ly_img_editor_delete),
                        )
                    }
                }
            }
            if ((page == 0 && uiState.secondPageExists()) || page == 1 && uiState.firstPageExists()) {
                val next = page == 0
                IconButton(onClick = {
                    page = if (next) 1 else 0
                }) {
                    Icon(
                        if (next) IconPack.NavigateNext else IconPack.NavigateBefore,
                        contentDescription =
                            if (next) {
                                stringResource(
                                    R.string.ly_img_editor_next_page,
                                )
                            } else {
                                stringResource(R.string.ly_img_editor_previous_page)
                            },
                    )
                }
            }
            if (page == 1) {
                if (uiState.canBringForward != null) {
                    IconButton(
                        onClick = { onEvent(BlockEvent.OnForward) },
                        enabled = uiState.canBringForward,
                    ) {
                        Icon(
                            IconPack.Bringforward,
                            contentDescription = stringResource(R.string.ly_img_editor_forward),
                        )
                    }
                }
                if (uiState.canBringBackward != null) {
                    IconButton(
                        onClick = { onEvent(BlockEvent.OnBackward) },
                        enabled = uiState.canBringBackward,
                    ) {
                        Icon(
                            IconPack.Sendbackward,
                            contentDescription = stringResource(R.string.ly_img_editor_backward),
                        )
                    }
                }
            }
        }
    }
}
