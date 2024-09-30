package ly.img.editor.video

import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import ly.img.editor.base.ui.Event
import ly.img.editor.compose.material3.TopAppBar
import ly.img.editor.compose.material3.TopAppBarDefaults
import ly.img.editor.core.ui.iconpack.Arrowrightbig
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Redo
import ly.img.editor.core.ui.iconpack.Undo
import ly.img.editor.core.R as coreR

@Composable
internal fun VideoUiToolbar(
    navigationIcon: ImageVector,
    onEvent: (Event) -> Unit,
    isUndoEnabled: Boolean,
    isRedoEnabled: Boolean,
    isExportEnabled: Boolean,
) {
    TopAppBar(
        title = {
            // Empty title
        },
        navigationIcon = {
            IconButton(onClick = {
                onEvent(Event.OnBack)
            }) {
                Icon(navigationIcon, contentDescription = stringResource(coreR.string.ly_img_editor_back))
            }
        },
        colors =
            TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        actions = {
            IconButton(
                onClick = { onEvent(Event.OnUndoClick) },
                enabled = isUndoEnabled,
            ) {
                Icon(IconPack.Undo, contentDescription = stringResource(ly.img.editor.base.R.string.ly_img_editor_undo))
            }
            IconButton(
                onClick = { onEvent(Event.OnRedoClick) },
                enabled = isRedoEnabled,
            ) {
                Icon(IconPack.Redo, contentDescription = stringResource(ly.img.editor.base.R.string.ly_img_editor_redo))
            }
            FilledIconButton(
                onClick = { onEvent(Event.OnExportClick) },
                enabled = isExportEnabled,
            ) {
                Icon(
                    IconPack.Arrowrightbig,
                    contentDescription = stringResource(ly.img.editor.video.R.string.ly_img_editor_export),
                )
            }
        },
    )
}
