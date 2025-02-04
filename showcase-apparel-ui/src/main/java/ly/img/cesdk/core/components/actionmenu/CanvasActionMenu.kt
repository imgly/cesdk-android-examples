package ly.img.cesdk.core.components.actionmenu

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
import ly.img.cesdk.apparel.BlockEvent
import ly.img.cesdk.apparelui.R
import ly.img.cesdk.core.iconpack.Bringforward
import ly.img.cesdk.core.iconpack.Controlpointduplicate
import ly.img.cesdk.core.iconpack.Delete
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.iconpack.NavigateBefore
import ly.img.cesdk.core.iconpack.NavigateNext
import ly.img.cesdk.core.iconpack.Sendbackward
import ly.img.cesdk.core.utils.toPx
import kotlin.math.cos
import kotlin.math.roundToInt

@Composable
fun CanvasActionMenu(
    uiState: CanvasActionMenuUiState,
    onEvent: (BlockEvent) -> Unit
) {
    var page by remember(uiState.selectedBlock) {
        mutableStateOf(if (uiState.isDuplicateAllowed || uiState.isDeleteAllowed) 0 else 1)
    }

    if (!uiState.show) return

    val rotation = uiState.selectedBlockRotation
    val gizmoLength = 48.dp.toPx()
    val topPadding = 24.dp
    val cos = cos(rotation)
    val dy = cos * gizmoLength

    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shadowElevation = 1.dp,
        modifier = Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    val x = uiState.selectedBlockRect.centerX().dp.roundToPx() - placeable.width / 2
                    val y = (uiState.selectedBlockRect.centerY() - uiState.selectedBlockRect.height() / 2).dp.roundToPx() -
                        placeable.height - topPadding.roundToPx() + if (dy < 0) dy.roundToInt() else 0
                    placeable.place(x, y)
                }
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (page == 0) {
                if (uiState.isDuplicateAllowed) {
                    IconButton(onClick = { onEvent(BlockEvent.OnDuplicate) }) {
                        Icon(IconPack.Controlpointduplicate, contentDescription = stringResource(R.string.cesdk_duplicate))
                    }
                }
                if (uiState.isDeleteAllowed) {
                    IconButton(onClick = { onEvent(BlockEvent.OnDelete) }) {
                        Icon(IconPack.Delete, contentDescription = stringResource(R.string.cesdk_delete))
                    }
                }
            }
            if (uiState.isDuplicateAllowed || uiState.isDeleteAllowed) {
                val next = page == 0
                IconButton(onClick = {
                    page = if (next) 1 else 0
                }) {
                    Icon(
                        if (next) IconPack.NavigateNext else IconPack.NavigateBefore,
                        contentDescription = if (next) stringResource(R.string.cesdk_next_page) else stringResource(R.string.cesdk_previous_page)
                    )
                }
            }
            if (page == 1) {
                if (uiState.canBringForward != null) {
                    IconButton(onClick = { onEvent(BlockEvent.OnUp) }, enabled = uiState.canBringForward) {
                        Icon(IconPack.Bringforward, contentDescription = stringResource(R.string.cesdk_up))
                    }
                }
                if (uiState.canBringBackward != null) {
                    IconButton(onClick = { onEvent(BlockEvent.OnDown) }, enabled = uiState.canBringBackward) {
                        Icon(IconPack.Sendbackward, contentDescription = stringResource(R.string.cesdk_down))
                    }
                }
            }
        }
    }
}