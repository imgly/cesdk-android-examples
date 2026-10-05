package ly.img.editor.configuration.memories.widget

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ly.img.editor.configuration.memories.iconPack.Close
import ly.img.editor.configuration.memories.model.ImageItem
import ly.img.editor.configuration.memories.util.buildImageItems
import ly.img.editor.core.iconpack.IconPack

/**
 * Full-screen overlay for editing the slideshow's images while inside the editor.
 *
 * All edits (add / sort / reorder / delete) are staged in [images] and are only written to the
 * tracks when the user taps the confirm (✓) button. Tapping the close (X) button discards them.
 */
@Composable
fun ImageGridOverlay(
    images: List<ImageItem>,
    onImagesChanged: (List<ImageItem>) -> Unit,
    onAddImages: (List<ImageItem>) -> Unit,
    onClose: () -> Unit,
    onApply: () -> Unit,
    selectedForDeletion: Set<Int> = emptySet(),
    onToggleSelection: (Int) -> Unit = {},
    isSortEnabled: Boolean = false,
    isSortAscending: Boolean = true,
    onToggleSort: () -> Unit = {},
    onDeleteSelected: () -> Unit = {},
    onClearAll: () -> Unit = {},
    multiSelectMode: Boolean = false,
    onToggleMultiSelect: () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Media picker (images + videos) — added items get unique ids.
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val baseId = images.maxOfOrNull { it.id } ?: 0
            scope.launch {
                onAddImages(buildImageItems(context.contentResolver, uris, baseId))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.98f))
            .statusBarsPadding(),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ImageGrid(
                images = images,
                onImagesChanged = onImagesChanged, // engine == null → reorders the draft only
                contentPadding = PaddingValues(
                    top = 72.dp,
                    bottom = 90.dp,
                    start = 16.dp,
                    end = 16.dp,
                ),
                selectedForDeletion = if (multiSelectMode) selectedForDeletion else emptySet(),
                onToggleSelection = if (multiSelectMode) onToggleSelection else null,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            Color.Transparent,
                        ),
                    ),
                ),
        )

        // Bottom toolbar — confirm (✓) applies the staged changes to the tracks.
        ImageToolbar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .navigationBarsPadding(),
            onAddImage = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)) },
            onToggleSort = onToggleSort,
            onDeleteAll = onClearAll,
            onDeleteSelected = onDeleteSelected,
            onProceed = onApply,
            isSortEnabled = isSortEnabled,
            isSortAscending = isSortAscending,
            hasImages = images.isNotEmpty(),
            showDeleteButton = true,
            selectedForDeletionCount = selectedForDeletion.size,
            totalCount = images.size,
            confirmMode = true,
            multiSelectMode = multiSelectMode,
            onToggleMultiSelect = onToggleMultiSelect,
        )

        // Close button (X) at the top right — discards the staged changes.
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(72.dp)
                .padding(20.dp)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    CircleShape,
                ),
        ) {
            Icon(
                imageVector = IconPack.Close,
                contentDescription = "Cancel",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
