package ly.img.cesdk.apparel

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import ly.img.cesdk.apparelui.R
import ly.img.cesdk.core.components.ToggleIconButton
import ly.img.cesdk.core.iconpack.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Toolbar(
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
                    Icon(navigationIcon, contentDescription = stringResource(R.string.cesdk_back))
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