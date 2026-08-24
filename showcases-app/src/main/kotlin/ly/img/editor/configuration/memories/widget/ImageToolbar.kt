@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package ly.img.editor.configuration.memories.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ly.img.editor.configuration.memories.iconPack.AddPhotoAlternate
import ly.img.editor.configuration.memories.iconPack.ArrowForward
import ly.img.editor.configuration.memories.iconPack.Check
import ly.img.editor.configuration.memories.iconPack.Delete
import ly.img.editor.configuration.memories.iconPack.DeleteOutline
import ly.img.editor.configuration.memories.iconPack.Sort
import ly.img.editor.configuration.memories.iconPack.TouchTriple
import ly.img.editor.core.iconpack.IconPack

@Composable
fun ImageToolbar(
    modifier: Modifier = Modifier,
    onAddImage: () -> Unit = {},
    onToggleSort: () -> Unit = {},
    onDeleteAll: (() -> Unit)? = null,
    onDeleteSelected: () -> Unit = {},
    onProceed: () -> Unit = {},
    isSortEnabled: Boolean = false,
    isSortAscending: Boolean = true,
    hasImages: Boolean = false,
    showDeleteButton: Boolean = true,
    selectedForDeletionCount: Int = 0,
    totalCount: Int = 0,
    confirmMode: Boolean = false,
    multiSelectMode: Boolean = false,
    onToggleMultiSelect: () -> Unit = {},
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            12.dp,
            alignment = Alignment.CenterHorizontally,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left pill with multiple actions
        Card(
            modifier = Modifier
                .weight(1f, false)
                .height(64.dp),
            shape = CircleShape,
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 3.dp,
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Add photos button — hidden while selecting images to delete. Its trailing
                // gap lives inside the AnimatedVisibility so it collapses with the button.
                AnimatedVisibility(visible = !multiSelectMode) {
                    IconButton(
                        onClick = onAddImage,
                        modifier = Modifier.padding(end = 4.dp),
                    ) {
                        Icon(
                            imageVector = IconPack.AddPhotoAlternate,
                            contentDescription = "Add photos",
                        )
                    }
                }

                // Multi-select toggle
                IconButton(
                    onClick = onToggleMultiSelect,
                    modifier = Modifier
                        .background(
                            color = if (multiSelectMode) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                Color.Transparent
                            },
                            shape = CircleShape,
                        ),
                ) {
                    Icon(
                        imageVector = IconPack.TouchTriple,
                        contentDescription = if (multiSelectMode) {
                            "Exit selection mode"
                        } else {
                            "Select media"
                        },
                        tint = if (multiSelectMode) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            LocalContentColor.current
                        },
                    )
                }

                // Sort button
                IconButton(
                    onClick = onToggleSort,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .background(
                            color = if (isSortEnabled) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                Color.Transparent
                            },
                            shape = CircleShape,
                        ),
                ) {
                    Icon(
                        imageVector = IconPack.Sort,
                        contentDescription = "Sort by date",
                        modifier = Modifier
                            .graphicsLayer(
                                scaleY = if (isSortEnabled && !isSortAscending) -1f else 1f,
                            ),
                        tint = if (isSortEnabled) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            LocalContentColor.current
                        },
                    )
                }

                // Delete button. Off: filled trash → delete all. On: outlined trash with a
                // count badge (from 0) → delete the current selection.
                if (showDeleteButton) {
                    val deleteEnabled =
                        if (multiSelectMode) selectedForDeletionCount > 0 else hasImages
                    // A plain IconButton clips its content to a circular state layer, which cuts off
                    // the BadgedBox count badge at the icon's corner. Use an unclipped, unbounded button.
                    UnboundedIconButton(
                        onClick = { showDeleteDialog = true },
                        enabled = deleteEnabled,
                        modifier = Modifier.padding(start = 4.dp),
                    ) {
                        BadgedBox(
                            badge = {
                                SelectionCountBadge(
                                    visible = multiSelectMode,
                                    count = selectedForDeletionCount,
                                )
                            },
                        ) {
                            Icon(
                                imageVector = if (multiSelectMode) {
                                    IconPack.DeleteOutline
                                } else {
                                    IconPack.Delete
                                },
                                contentDescription = if (multiSelectMode) {
                                    "Delete selected media"
                                } else {
                                    "Delete all media"
                                },
                            )
                        }
                    }
                }
            }
        }

        // Right pill - Proceed button
        Card(
            modifier = Modifier
                .size(56.dp),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 3.dp,
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (hasImages) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
            ),
        ) {
            IconButton(
                onClick = onProceed,
                enabled = hasImages,
                modifier = Modifier.fillMaxSize(),
            ) {
                Icon(
                    imageVector = if (confirmMode) {
                        IconPack.Check
                    } else {
                        IconPack.ArrowForward
                    },
                    contentDescription = if (confirmMode) "Apply changes" else "Proceed",
                    tint = if (hasImages) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }

    if (showDeleteDialog) {
        val count = if (multiSelectMode) selectedForDeletionCount else totalCount
        val noun = if (count == 1) "item" else "items"
        DeleteConfirmationDialog(
            title = if (multiSelectMode) "Delete selected media?" else "Delete all media?",
            message = if (multiSelectMode) {
                "Remove $count selected $noun? This can't be undone."
            } else {
                "Remove all $count $noun? This can't be undone."
            },
            onConfirm = {
                showDeleteDialog = false
                if (multiSelectMode) onDeleteSelected() else onDeleteAll?.invoke()
            },
            onDismiss = { showDeleteDialog = false },
        )
    }
}

/**
 * An icon button that does not clip its content to a circle (so a [BadgedBox] badge can overflow
 * past the icon's corner) and ripples without a bounded circular mask.
 */
@Composable
private fun UnboundedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .size(48.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false, radius = 22.dp),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        val baseColor = LocalContentColor.current
        CompositionLocalProvider(
            LocalContentColor provides if (enabled) baseColor else baseColor.copy(alpha = 0.38f),
            content = content,
        )
    }
}

@Composable
private fun SelectionCountBadge(
    visible: Boolean,
    count: Int,
) {
    AnimatedVisibility(
        visible,
        enter = scaleIn(initialScale = 0f),
        exit = scaleOut(targetScale = 0f),
    ) {
        Badge {
            Text("$count")
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(imageVector = IconPack.Delete, contentDescription = null)
        },
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Preview(name = "Empty State", showBackground = true)
@Composable
fun ImageToolbar_EmptyState_Preview() {
    MaterialTheme {
        ImageToolbar(
            modifier = Modifier.padding(16.dp),
            hasImages = false,
            showDeleteButton = true,
            onDeleteAll = {},
        )
    }
}

@Preview(name = "With Images - Clear all", showBackground = true)
@Composable
fun ImageToolbar_WithImages_Preview() {
    MaterialTheme {
        ImageToolbar(
            modifier = Modifier.padding(16.dp),
            hasImages = true,
            totalCount = 8,
            showDeleteButton = true,
            onDeleteAll = {},
        )
    }
}

@Preview(name = "Multi-select mode (badge)", showBackground = true)
@Composable
fun ImageToolbar_SelectionMode_Preview() {
    MaterialTheme {
        ImageToolbar(
            modifier = Modifier.padding(16.dp),
            hasImages = true,
            totalCount = 8,
            selectedForDeletionCount = 3,
            showDeleteButton = true,
            onDeleteAll = {},
            multiSelectMode = true,
        )
    }
}

@Preview(name = "Overlay Mode (No Delete)", showBackground = true)
@Composable
fun ImageToolbar_OverlayMode_Preview() {
    MaterialTheme {
        ImageToolbar(
            modifier = Modifier.padding(16.dp),
            hasImages = true,
            isSortEnabled = true,
            showDeleteButton = false,
        )
    }
}
