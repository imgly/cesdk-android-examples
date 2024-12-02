package ly.img.editor.postcard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.base.R
import ly.img.editor.base.components.ToggleIconButton
import ly.img.editor.base.ui.Event
import ly.img.editor.core.iconpack.ArrowBack
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Redo
import ly.img.editor.core.ui.iconpack.Share
import ly.img.editor.core.ui.iconpack.Undo
import ly.img.editor.core.ui.iconpack.Visibility
import ly.img.editor.core.ui.iconpack.Visibilityoutline
import ly.img.editor.core.R as coreR
import ly.img.editor.core.iconpack.IconPack as CoreIconPack
import ly.img.editor.postcard.R as postCardR

@Composable
internal fun PostcardUiToolbar(
    navigationIcon: ImageVector,
    onEvent: (Event) -> Unit,
    postcardMode: PostcardMode,
    isInPreviewMode: Boolean,
    isUndoEnabled: Boolean,
    isRedoEnabled: Boolean,
) {
    Surface(
        Modifier
            .fillMaxWidth()
            .height(64.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
    ) {
        Box {
            Row(Modifier.align(Alignment.CenterStart)) {
                Spacer(modifier = Modifier.width(4.dp))
                if (!isInPreviewMode) {
                    if (postcardMode == PostcardMode.Design) {
                        IconButton(onClick = {
                            onEvent(Event.OnBack)
                        }) {
                            Icon(
                                navigationIcon,
                                contentDescription = stringResource(coreR.string.ly_img_editor_back),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        TextButton(
                            colors =
                                ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                ),
                            onClick = {
                                onEvent(Event.OnPreviousPage)
                            },
                        ) {
                            Icon(
                                CoreIconPack.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = stringResource(postCardR.string.ly_img_editor_design),
                                modifier = Modifier.padding(start = 4.dp),
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                    }
                }
            }

            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
            ) {
                Row(Modifier.align(Alignment.Center)) {
                    if (!isInPreviewMode) {
                        IconButton(
                            onClick = { onEvent(Event.OnUndoClick) },
                            enabled = isUndoEnabled,
                        ) {
                            Icon(
                                IconPack.Undo,
                                contentDescription = stringResource(R.string.ly_img_editor_undo),
                            )
                        }
                        IconButton(
                            onClick = { onEvent(Event.OnRedoClick) },
                            enabled = isRedoEnabled,
                        ) {
                            Icon(
                                IconPack.Redo,
                                contentDescription = stringResource(R.string.ly_img_editor_redo),
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(96.dp))
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
                }
            }

            Row(Modifier.align(Alignment.CenterEnd)) {
                if (isInPreviewMode || postcardMode == PostcardMode.Write) {
                    IconButton(
                        onClick = { onEvent(Event.OnExportClick) },
                    ) {
                        Icon(
                            IconPack.Share,
                            contentDescription = stringResource(R.string.ly_img_editor_share),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    TextButton(
                        colors =
                            ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                        onClick = {
                            onEvent(Event.OnNextPage)
                        },
                    ) {
                        Text(
                            text = stringResource(postCardR.string.ly_img_editor_write),
                            modifier = Modifier.padding(end = 4.dp),
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}
