package ly.img.editor.photo

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import ly.img.editor.base.R
import ly.img.editor.base.components.ToggleIconButton
import ly.img.editor.base.ui.Event
import ly.img.editor.compose.material3.TopAppBar
import ly.img.editor.compose.material3.TopAppBarDefaults
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Redo
import ly.img.editor.core.ui.iconpack.Share
import ly.img.editor.core.ui.iconpack.Undo
import ly.img.editor.core.ui.iconpack.Visibility
import ly.img.editor.core.ui.iconpack.Visibilityoutline
import ly.img.editor.core.R as coreR

@Composable
fun PhotoUiToolbar(
    navigationIcon: ImageVector,
    onEvent: (Event) -> Unit,
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
                    Icon(
                        navigationIcon,
                        contentDescription = stringResource(coreR.string.ly_img_editor_back),
                    )
                }
            }
        },
        colors =
            TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        actions = {
            if (!isInPreviewMode) {
                IconButton(
                    onClick = { onEvent(Event.OnUndoClick) },
                    enabled = isUndoEnabled,
                ) {
                    Icon(IconPack.Undo, contentDescription = stringResource(R.string.ly_img_editor_undo))
                }
                IconButton(
                    onClick = { onEvent(Event.OnRedoClick) },
                    enabled = isRedoEnabled,
                ) {
                    Icon(IconPack.Redo, contentDescription = stringResource(R.string.ly_img_editor_redo))
                }
            }
            ToggleIconButton(
                checked = isInPreviewMode,
                onCheckedChange = { onEvent(Event.OnTogglePreviewMode(it)) },
            ) {
                Icon(
                    if (isInPreviewMode) IconPack.Visibility else IconPack.Visibilityoutline,
                    contentDescription = stringResource(R.string.ly_img_editor_toggle_preview_mode),
                )
            }
            IconButton(
                onClick = { onEvent(Event.OnExportClick) },
            ) {
                Icon(IconPack.Share, contentDescription = stringResource(R.string.ly_img_editor_share))
            }
        },
    )
}
