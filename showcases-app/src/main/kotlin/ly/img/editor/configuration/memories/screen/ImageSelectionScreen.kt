package ly.img.editor.configuration.memories.screen

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import ly.img.editor.configuration.memories.MemoriesViewModel
import ly.img.editor.configuration.memories.iconPack.Info
import ly.img.editor.configuration.memories.model.ImageItem
import ly.img.editor.configuration.memories.util.buildImageItems
import ly.img.editor.configuration.memories.widget.CenteredAddImageButton
import ly.img.editor.configuration.memories.widget.ImageGrid
import ly.img.editor.configuration.memories.widget.ImageToolbar
import ly.img.editor.core.iconpack.IconPack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageSelectionScreen(
    viewModel: MemoriesViewModel,
    onProceedToEditor: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedImages by viewModel.selectedImages.collectAsState()
    val imagesToDelete by viewModel.imagesToDelete.collectAsState()
    val multiSelectMode by viewModel.multiSelectMode.collectAsState()
    val videoTitle by viewModel.videoTitle.collectAsState()
    val isSortEnabled by viewModel.isSortEnabled.collectAsState()
    val isSortAscending by viewModel.isSortAscending.collectAsState()
    val context = LocalContext.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    // Back exits selection mode (rather than leaving the screen) while it's on.
    BackHandler(enabled = multiSelectMode) {
        viewModel.toggleMultiSelect()
    }

    // LazyGrid state for scroll monitoring
    val lazyGridState = rememberLazyGridState()

    // Height measurement for top content
    var columnHeight by remember { mutableFloatStateOf(0f) }

    // Calculate fade effect based on scroll position
    val fadeAlpha = remember {
        derivedStateOf {
            if (lazyGridState.firstVisibleItemIndex == 0) {
                val offset = lazyGridState.firstVisibleItemScrollOffset
                val fadeThreshold = with(density) { 100.dp.toPx() }
                (1f - (offset / fadeThreshold)).coerceIn(0f, 1f)
            } else {
                0f
            }
        }
    }

    // Media picker launcher (images and videos)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            // Base new ids on the current max id (not list size) so they stay unique
            // even after some images have been deleted.
            val baseId = selectedImages.maxOfOrNull { it.id } ?: 0
            scope.launch {
                val newImages = buildImageItems(context.contentResolver, uris, baseId)
                // The ViewModel merges these in (re-sorting if sort is on) and reconciles the
                // sort button: if the resulting list is chronological it flips sort on automatically.
                viewModel.addSelectedImages(newImages)
            }
        }
    }

    Box(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .zIndex(if (fadeAlpha.value > .9f) 100f else -1f)
                .fillMaxWidth()
                .padding(16.dp)
                .onGloballyPositioned { coordinates ->
                    columnHeight = coordinates.size.height.toFloat()
                }
                .graphicsLayer {
                    alpha = fadeAlpha.value
                    translationY = (1f - fadeAlpha.value) * -100f
                },
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            // Title with a standalone informational icon to its right (its hint is exposed via
            // contentDescription for accessibility).
            Row(
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Create Your Video Memory",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f),
                )

                // Only show the drag/select hint once there are images to act on
                if (selectedImages.isNotEmpty()) {
                    Icon(
                        imageVector = IconPack.Info,
                        contentDescription = "Drag and drop images by long pressing; tap to select for deletion.",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Video title input field
            OutlinedTextField(
                value = videoTitle,
                onValueChange = { viewModel.setVideoTitle(it) },
                label = { Text("Add a title") },
                placeholder = { Text("My Amazing Memory") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                ),
                shape = RoundedCornerShape(8.dp),
            )
        }

        // Image Grid
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            if (selectedImages.isEmpty()) {
                // Empty state: launch the device media picker
                CenteredAddImageButton(
                    modifier = Modifier.align(Alignment.TopCenter),
                    onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)) },
                    columnHeight = columnHeight,
                )
            } else {
                ImageGrid(
                    modifier = Modifier
                        .align(Alignment.TopCenter),
                    images = selectedImages,
                    onImagesChanged = { viewModel.updateSelectedImages(it) },
                    contentPadding = PaddingValues(
                        top = with(density) { (columnHeight / density.density).dp + 32.dp },
                        bottom = 96.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
                    lazyGridState = lazyGridState,
                    selectedForDeletion = if (multiSelectMode) imagesToDelete else emptySet(),
                    onToggleSelection = if (multiSelectMode) {
                        { viewModel.toggleImageForDeletion(it) }
                    } else {
                        null
                    },
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .height(12.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.background.copy(alpha = 0f),
                            ),
                        ),
                    ),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(64.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background.copy(alpha = 0f),
                                MaterialTheme.colorScheme.background,
                            ),
                        ),
                    ),
            )
        }

        // Bottom toolbar - only show if there are images
        if (selectedImages.isNotEmpty()) {
            ImageToolbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding(),
                onAddImage = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)) },
                onToggleSort = { viewModel.toggleSort() },
                onDeleteAll = { viewModel.clearSelectedImages() },
                onDeleteSelected = { viewModel.deleteSelectedImages() },
                onProceed = onProceedToEditor,
                isSortEnabled = isSortEnabled,
                isSortAscending = isSortAscending,
                hasImages = selectedImages.isNotEmpty(),
                showDeleteButton = true,
                selectedForDeletionCount = imagesToDelete.size,
                totalCount = selectedImages.size,
                multiSelectMode = multiSelectMode,
                onToggleMultiSelect = { viewModel.toggleMultiSelect() },
            )
        }
    }
}

@Composable
private fun createMockViewModel(
    selectedImages: List<ImageItem> = emptyList(),
    videoTitle: String = "",
): MemoriesViewModel {
    val viewModel = remember { MemoriesViewModel() }

    // Set the initial state synchronously in the preview
    viewModel.updateSelectedImages(selectedImages)
    if (videoTitle.isNotEmpty()) {
        viewModel.setVideoTitle(videoTitle)
    }

    return viewModel
}

@Preview(
    name = "Empty State",
    showBackground = true,
)
@Composable
fun ImageSelectionScreen_EmptyState_Preview() {
    val mockViewModel = createMockViewModel()

    ImageSelectionScreen(
        viewModel = mockViewModel,
        onProceedToEditor = { },
    )
}

@Preview(
    name = "Unfocused with Sample Images",
    showBackground = true,
)
@Composable
fun ImageSelectionScreen_WithImages_Preview() {
    val sampleImages = listOf(
        ImageItem(id = 1, url = ""),
        ImageItem(id = 2, url = ""),
        ImageItem(id = 3, url = ""),
    )
    val mockViewModel = createMockViewModel(selectedImages = sampleImages)

    ImageSelectionScreen(
        viewModel = mockViewModel,
        onProceedToEditor = { },
    )
}

@Preview(
    name = "Focused Text Input",
    showBackground = true,
)
@Composable
fun ImageSelectionScreen_FocusedInput_Preview() {
    val sampleImages = listOf(
        ImageItem(id = 1, url = ""),
        ImageItem(id = 2, url = ""),
    )
    val mockViewModel = createMockViewModel(
        selectedImages = sampleImages,
        videoTitle = "My Amazing Memory",
    )

    ImageSelectionScreen(
        viewModel = mockViewModel,
        onProceedToEditor = { },
    )
}

@Preview(
    name = "User Photos with Many Images",
    showBackground = true,
)
@Composable
fun ImageSelectionScreen_ManyUserPhotos_Preview() {
    val sampleImages = listOf(
        ImageItem(
            id = 1,
            url = "",
            creationDate = System.currentTimeMillis() - 86400000,
        ),
        ImageItem(
            id = 2,
            url = "",
            creationDate = System.currentTimeMillis() - 172800000,
        ),
        ImageItem(
            id = 3,
            url = "",
            creationDate = System.currentTimeMillis() - 259200000,
        ),
        ImageItem(
            id = 4,
            url = "",
            creationDate = System.currentTimeMillis() - 345600000,
        ),
        ImageItem(
            id = 5,
            url = "",
            creationDate = System.currentTimeMillis() - 432000000,
        ),
        ImageItem(
            id = 6,
            url = "",
            creationDate = System.currentTimeMillis() - 518400000,
        ),
    )
    val mockViewModel = createMockViewModel(
        selectedImages = sampleImages,
        videoTitle = "Summer Vacation 2024",
    )

    ImageSelectionScreen(
        viewModel = mockViewModel,
        onProceedToEditor = { },
    )
}
