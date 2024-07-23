package ly.img.editor.base.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ly.img.editor.base.engine.NoneDesignBlock
import ly.img.editor.compose.animation.AnimatedVisibility
import ly.img.editor.compose.animation_core.fadeIn
import ly.img.editor.compose.animation_core.fadeOut
import ly.img.editor.compose.animation_core.tween
import ly.img.editor.compose.material3.Card
import ly.img.editor.compose.material3.CardDefaults
import ly.img.editor.core.theme.surface1
import ly.img.editor.core.theme.surface3
import ly.img.editor.core.ui.iconpack.Add
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.utils.animateItemPlacement as animateGridItem

private const val DOUBLE_CLICK_DELAY = 200L
private const val FADE_ANIMATION_DURATION = 200
private const val LOADING_APPEAR_DELAY = 300L

@Composable
fun EditorPagesUi(
    modifier: Modifier,
    state: EditorPagesState?,
    onEvent: (Event) -> Unit,
) {
    var cachedState: EditorPagesState? by remember {
        mutableStateOf(state)
    }
    LaunchedEffect(state) {
        if (state != null) {
            cachedState = state
        }
    }
    AnimatedVisibility(
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut(),
        visible = state != null,
    ) {
        cachedState?.let { cachedState ->
            Box(
                modifier =
                    Modifier
                        .background(color = MaterialTheme.colorScheme.surface1),
            ) {
                val shape = remember { RoundedCornerShape(12.dp) }
                val borderShape = remember { RoundedCornerShape(18.dp) }
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
                    columns = GridCells.Fixed(count = 2),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    items(
                        count = cachedState.pages.size + 1,
                        key = {
                            val blockId = cachedState.pages.getOrNull(it)?.block ?: NoneDesignBlock
                            "${cachedState.sessionId}$blockId"
                        },
                    ) { index ->
                        if (index < cachedState.pages.size) {
                            val page = cachedState.pages[index]
                            var loadingDelayPassed by remember {
                                mutableStateOf(false)
                            }
                            var imageHeight by remember {
                                mutableStateOf(0)
                            }
                            LaunchedEffect(page, imageHeight) {
                                onEvent(Event.OnPagesModePageBind(page = page, pageHeight = imageHeight))
                                if (!loadingDelayPassed) {
                                    delay(LOADING_APPEAR_DELAY)
                                    loadingDelayPassed = true
                                }
                            }
                            Column(
                                modifier =
                                    Modifier
                                        .animateGridItem()
                                        .fillMaxWidth(),
                            ) {
                                val borderColor =
                                    if (page == cachedState.selectedPage) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        Color.Transparent
                                    }
                                Box(
                                    modifier =
                                        Modifier
                                            .padding(2.dp)
                                            .fillMaxWidth()
                                            .border(color = borderColor, shape = borderShape, width = 3.dp),
                                ) {
                                    var clickData by remember { mutableStateOf(0 to 0L) }
                                    Card(
                                        modifier =
                                            Modifier
                                                .padding(6.dp)
                                                .fillMaxWidth()
                                                .aspectRatio(cachedState.pageAspectRatio),
                                        onClick = {
                                            val (clickCounter, lastClickTime) = clickData
                                            val newClickTime = System.currentTimeMillis()
                                            var newClickCounter = clickCounter
                                            val diff = newClickTime - lastClickTime
                                            when {
                                                diff < DOUBLE_CLICK_DELAY && clickCounter == 1 -> {
                                                    onEvent(Event.OnTogglePagesMode)
                                                }
                                                diff >= DOUBLE_CLICK_DELAY -> {
                                                    newClickCounter = 0
                                                }
                                            }
                                            if (++newClickCounter == 1) {
                                                onEvent(Event.OnPagesModePageSelectionChange(page = page))
                                                onEvent(Event.OnPage(page = index))
                                            }
                                            clickData = newClickCounter to newClickTime
                                        },
                                        shape = shape,
                                        elevation =
                                            CardDefaults.cardElevation(
                                                defaultElevation = 2.dp,
                                                pressedElevation = 2.dp,
                                            ),
                                    ) {
                                        Box(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(cachedState.pageAspectRatio)
                                                    .onSizeChanged { imageHeight = it.height },
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            if (loadingDelayPassed && page.thumbnail == null) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(40.dp),
                                                    color = MaterialTheme.colorScheme.primary,
                                                )
                                            }
                                            ly.img.editor.compose.animation.AnimatedVisibility(
                                                visible = page.thumbnail != null,
                                                enter =
                                                    fadeIn(
                                                        animationSpec = tween(durationMillis = FADE_ANIMATION_DURATION),
                                                    ),
                                                exit =
                                                    fadeOut(
                                                        animationSpec = tween(durationMillis = FADE_ANIMATION_DURATION),
                                                    ),
                                            ) {
                                                page.thumbnail?.let {
                                                    Image(
                                                        modifier =
                                                            Modifier
                                                                .fillMaxWidth()
                                                                .aspectRatio(cachedState.pageAspectRatio),
                                                        bitmap = it.asImageBitmap(),
                                                        contentDescription = null,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                Text(
                                    modifier =
                                        Modifier
                                            .padding(top = 12.dp)
                                            .align(Alignment.CenterHorizontally),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color =
                                        if (page == cachedState.selectedPage) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        },
                                    text = "${index + 1}",
                                )
                            }
                        } else {
                            Card(
                                modifier =
                                    Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                        .animateGridItem()
                                        .aspectRatio(cachedState.pageAspectRatio),
                                onClick = {
                                    onEvent(Event.OnAddPage(index))
                                },
                                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant),
                                shape = shape,
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor = Color.Transparent,
                                    ),
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                ) {
                                    Image(
                                        modifier =
                                            Modifier
                                                .size(40.dp)
                                                .align(Alignment.Center)
                                                .background(color = MaterialTheme.colorScheme.surface3, shape = shape)
                                                .padding(8.dp),
                                        imageVector = IconPack.Add,
                                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                                        contentDescription = null,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
