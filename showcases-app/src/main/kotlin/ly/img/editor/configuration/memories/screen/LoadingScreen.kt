package ly.img.editor.configuration.memories.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ly.img.editor.configuration.memories.model.ImageItem
import ly.img.editor.core.theme.EditorTheme
import kotlin.random.Random

@Composable
fun LoadingScreen(
    selectedImages: List<ImageItem>,
    modifier: Modifier = Modifier,
) {
    // Create a 4x4 grid (16 items)
    val gridSize = 16
    var flashingStates by remember { mutableStateOf(List(gridSize) { Random.nextBoolean() }) }
    var currentImages by remember { mutableStateOf<List<ImageItem?>>(List(gridSize) { null }) }

    // Staggered entrance: everything starts hidden, then the grid, title, and
    // description fade in one after another. Each fade has the same duration but a
    // later start (delayMillis), so they overlap instead of waiting for the
    // previous one to finish.
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }

    val entranceDuration = 600
    val entranceStagger = 300
    val gridAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(durationMillis = entranceDuration, delayMillis = 0),
        label = "grid_entrance",
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(durationMillis = entranceDuration, delayMillis = entranceStagger),
        label = "title_entrance",
    )
    val descriptionAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(durationMillis = entranceDuration, delayMillis = entranceStagger * 2),
        label = "description_entrance",
    )

    // Staggered flashing with random timing for each cell
    LaunchedEffect(selectedImages) {
        // Nothing to flash without images — and selectedImages.random() below would throw on an
        // empty list (e.g. the loading screen shown for an empty draft).
        if (selectedImages.isEmpty()) return@LaunchedEffect
        // Launch individual coroutines for each cell with random timing
        for (i in 0 until gridSize) {
            launch {
                while (true) {
                    if (flashingStates[i]) {
                        // Hide image
                        flashingStates = flashingStates.toMutableList().apply {
                            this[i] = false
                        }

                        val hiddenTime = (300..500).random().toLong()
                        delay(hiddenTime)
                    } else {
                        // Select a new random image for this specific cell
                        val newImages = currentImages.toMutableList()
                        newImages[i] = selectedImages.random()
                        currentImages = newImages

                        // Show image
                        flashingStates = flashingStates.toMutableList().apply {
                            this[i] = true
                        }

                        val visibleTime = (300..400).random().toLong()
                        delay(visibleTime)
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            // Swallow all input so taps/gestures don't reach the still-composed (alpha 0) editor
            // underneath while it is loading.
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent().changes.forEach { it.consume() }
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            // 4x4 Image Grid
            Box {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .alpha(gridAlpha)
                        .aspectRatio(1f)
                        .fillMaxSize()
                        .padding(32.dp),
                    userScrollEnabled = false,
                ) {
                    items(gridSize) { index ->
                        val isFlashing = flashingStates[index]
                        val imageToShow = currentImages[index]

                        val alpha by animateFloatAsState(
                            targetValue = if (isFlashing) 1f else 0f,
                            animationSpec = tween(300),
                            label = "flash_alpha",
                        )

                        val scale by animateFloatAsState(
                            targetValue = if (isFlashing) .9f else 1f,
                            animationSpec = spring(),
                            label = "flash_scale",
                        )

                        Box(
                            modifier = Modifier
                                .scale(scale)
                                .aspectRatio(1f)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp),
                                ),
                        ) {
                            if (imageToShow != null) {
                                AsyncImage(
                                    model = imageToShow.url,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp))
                                        .alpha(alpha),
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        }
                    }
                }

                // Loading Text
                val isDark = isSystemInDarkTheme()
                val backgroundColor = MaterialTheme.colorScheme.background
                Column(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxSize()
                        .drawBehind {
                            scale(
                                scaleY = .8f,
                                scaleX = 1f,
                            ) {
                                drawRect(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            backgroundColor.copy(alpha = if (isDark) .85f else 1f),
                                            Color.Transparent,
                                        ),
                                    ),
                                )
                            }
                        }
                        .padding(32.dp)
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterVertically,
                    ),
                ) {
                    Text(
                        text = "Hold tight",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.alpha(titleAlpha),
                    )
                    Text(
                        text = "We're magically creating\nmemories for you ✨",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .alpha(descriptionAlpha)
                            .padding(horizontal = 32.dp),
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Preview(name = "Loading Screen", showBackground = true)
@Composable
private fun LoadingScreen_Preview() {
    EditorTheme {
        LoadingScreen(
            selectedImages = List(6) { index ->
                ImageItem(
                    id = index + 1,
                    url = "",
                )
            },
        )
    }
}
