package ly.img.editor.design

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.base.R
import ly.img.editor.base.components.ToggleIconButton
import ly.img.editor.base.ui.Event
import ly.img.editor.compose.material3.TopAppBar
import ly.img.editor.compose.material3.TopAppBarDefaults
import ly.img.editor.core.ui.iconpack.Book
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Redo
import ly.img.editor.core.ui.iconpack.Share
import ly.img.editor.core.ui.iconpack.Undo
import ly.img.editor.core.R as coreR

@Composable
fun DesignUiToolbar(
    navigationIcon: ImageVector,
    onEvent: (Event) -> Unit,
    pageCount: Int,
    isPagesScreenActive: Boolean,
    isUndoEnabled: Boolean,
    isRedoEnabled: Boolean,
) {
    TopAppBar(
        title = {
            // Empty title
        },
        navigationIcon = {
            if (isPagesScreenActive.not()) {
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
            ToggleIconButton(
                modifier =
                    Modifier
                        .height(40.dp)
                        .padding(horizontal = 8.dp),
                checked = isPagesScreenActive,
                checkedContainerColor = MaterialTheme.colorScheme.primary,
                onCheckedChange = { onEvent(Event.OnTogglePagesMode) },
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(IconPack.Book, contentDescription = stringResource(R.string.ly_img_editor_toggle_preview_mode))
                    val textAlpha =
                        when {
                            pageCount == 0 -> 0F
                            isPagesScreenActive -> 0.75F
                            else -> 1F
                        }
                    Text(
                        modifier =
                            Modifier
                                .padding(start = 3.dp)
                                .alpha(textAlpha),
                        text = "$pageCount",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
            IconButton(
                onClick = { onEvent(Event.OnExportClick) },
            ) {
                Icon(IconPack.Share, contentDescription = stringResource(R.string.ly_img_editor_share))
            }
        },
    )
}
