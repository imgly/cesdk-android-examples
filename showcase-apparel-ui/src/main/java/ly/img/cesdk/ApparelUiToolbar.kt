package ly.img.cesdk

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import ly.img.cesdk.components.ToggleIconButton
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.iconpack.Redo
import ly.img.cesdk.core.iconpack.Share
import ly.img.cesdk.core.iconpack.Undo
import ly.img.cesdk.core.iconpack.Visibility
import ly.img.cesdk.core.iconpack.Visibilityoutline
import ly.img.cesdk.editorui.Event
import ly.img.cesdk.editorui.R
import ly.img.cesdk.core.R as coreR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ApparelUiToolbar(
    navigationIcon: ImageVector,
    onEvent: (Event) -> Unit,
    isLoading: Boolean,
    isInPreviewMode: Boolean,
    isUndoEnabled: Boolean,
    isRedoEnabled: Boolean,
) {
    TopAppBar(
        title = {
            // Empty title
        },
        navigationIcon = {
            if (!isInPreviewMode) {
                IconButton(onClick = {
                    onEvent(Event.OnBack)
                }) {
                    Icon(navigationIcon, contentDescription = stringResource(coreR.string.cesdk_back))
                }
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        actions = {
            if (!isInPreviewMode) {
                IconButton(
                    onClick = { onEvent(Event.OnUndoClick) },
                    enabled = isUndoEnabled
                ) {
                    Icon(IconPack.Undo, contentDescription = stringResource(R.string.cesdk_undo))
                }
                IconButton(
                    onClick = { onEvent(Event.OnRedoClick) },
                    enabled = isRedoEnabled
                ) {
                    Icon(IconPack.Redo, contentDescription = stringResource(R.string.cesdk_redo))
                }
            }
            ToggleIconButton(
                checked = isInPreviewMode,
                onCheckedChange = { onEvent(Event.OnTogglePreviewMode(it)) },
                enabled = !isLoading
            ) {
                Icon(
                    if (isInPreviewMode) IconPack.Visibility else IconPack.Visibilityoutline,
                    contentDescription = stringResource(R.string.cesdk_toggle_preview_mode)
                )
            }
            IconButton(
                onClick = { onEvent(Event.OnExportClick) },
                enabled = !isLoading
            ) {
                Icon(IconPack.Share, contentDescription = stringResource(R.string.cesdk_share))
            }
        }
    )
}