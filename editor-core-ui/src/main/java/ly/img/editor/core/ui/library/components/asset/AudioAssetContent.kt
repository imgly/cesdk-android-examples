package ly.img.editor.core.ui.library.components.asset

import androidx.annotation.OptIn
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import kotlinx.coroutines.delay
import ly.img.editor.compose.foundation.basicMarquee
import ly.img.editor.compose.foundation.combinedClickable
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.ui.library.components.LibraryImageCard
import ly.img.editor.core.ui.library.getFormattedDuration
import ly.img.editor.core.ui.library.getMeta
import ly.img.editor.core.ui.library.getThumbnailUri
import ly.img.editor.core.ui.library.getUri
import ly.img.editor.core.ui.library.state.WrappedAsset
import ly.img.engine.Asset
import ly.img.engine.AssetContext
import kotlin.time.Duration.Companion.seconds

@OptIn(UnstableApi::class)
@Composable
internal fun AudioAssetContent(
    wrappedAsset: WrappedAsset,
    activatedPreviewItem: MutableState<String?> = mutableStateOf(""),
    exoPlayer: () -> ExoPlayer?,
    onAssetClick: (WrappedAsset) -> Unit,
    onAssetLongClick: (WrappedAsset) -> Unit,
) {
    val notInPreviewMode = !LocalInspectionMode.current

    val id = wrappedAsset.asset.id
    val context = LocalContext.current
    val exoPlayerInstance = exoPlayer()

    var isLoading by remember { mutableStateOf(exoPlayerInstance?.isLoading ?: false) }
    var isPlaying by remember { mutableStateOf(!notInPreviewMode) }
    var playbackPositionPercent by remember { mutableStateOf(0f) }
    var playbackTime by remember { mutableStateOf(0L) }

    val isCurrentAssetPlaying = (isPlaying && activatedPreviewItem.value == wrappedAsset.asset.id)

    val rotation: Float by animateFloatAsState(
        if (isCurrentAssetPlaying) 180f else 0f,
        animationSpec = TweenSpec(durationMillis = 300),
        label = "Icon Animation",
    )
    val animatedCornerRadius by animateDpAsState(
        targetValue = if (isCurrentAssetPlaying) 28.dp else 8.dp,
        animationSpec = TweenSpec(durationMillis = 300),
        label = "Corner Radius Animation",
    )
    val playStateTransitionProgress by animateFloatAsState(
        targetValue = if (isCurrentAssetPlaying) 1f else 0f,
        animationSpec = TweenSpec(durationMillis = 300),
        label = "Play State Transition Animation",
    )

    DisposableEffect(exoPlayerInstance) {
        val listener =
            object : Player.Listener {
                override fun onIsLoadingChanged(isLoadingState: Boolean) {
                    isLoading = isLoadingState
                }
            }

        exoPlayerInstance?.addListener(listener)

        onDispose {
            exoPlayerInstance ?: return@onDispose
            exoPlayerInstance.playWhenReady = false
            exoPlayerInstance.removeListener(listener)
        }
    }

    LaunchedEffect(isCurrentAssetPlaying) {
        while (isCurrentAssetPlaying) {
            val duration = exoPlayerInstance?.duration ?: 0
            val currentPosition = exoPlayerInstance?.currentPosition ?: 0
            if (duration > 0) {
                playbackTime = currentPosition
                playbackPositionPercent = (currentPosition.toDouble() / duration).toFloat().coerceIn(0f, 1f)
            }
            delay(1.seconds / 30)
        }
    }

    ListItem(
        headlineContent = {
            Text(
                modifier = Modifier.basicMarquee(),
                text = wrappedAsset.asset.label ?: "",
            )
        },
        leadingContent = {
            Box(
                Modifier
                    .shadow(
                        (playStateTransitionProgress * 3).dp,
                        RoundedCornerShape(animatedCornerRadius),
                        ambientColor = Color(0x4D000000),
                        clip = false,
                    )
                    .shadow(
                        (playStateTransitionProgress * 4).dp,
                        RoundedCornerShape(animatedCornerRadius),
                        ambientColor = Color(0x26000000),
                        clip = false,
                    )
                    .clip(RoundedCornerShape(animatedCornerRadius))
                    .background(Color.White),
            ) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(animatedCornerRadius))
                        .size(56.dp)
                        .clickable {
                            if (notInPreviewMode) {
                                val player = exoPlayerInstance ?: return@clickable
                                if (id == activatedPreviewItem.value) {
                                    player.playWhenReady = !exoPlayerInstance.playWhenReady
                                    isPlaying = player.playWhenReady
                                } else {
                                    val dataSourceFactory = DefaultDataSource.Factory(context)
                                    val mediaSource: MediaSource =
                                        ProgressiveMediaSource.Factory(dataSourceFactory)
                                            .createMediaSource(
                                                MediaItem.fromUri(
                                                    wrappedAsset.asset.getMeta("previewUri") ?: wrappedAsset.asset.getUri(),
                                                ),
                                            )
                                    player.prepare(mediaSource)
                                    player.repeatMode = Player.REPEAT_MODE_ONE
                                    player.playWhenReady = true
                                    isPlaying = true
                                    activatedPreviewItem.value = id
                                }
                            } else {
                                activatedPreviewItem.value = id
                                isPlaying = !isPlaying
                            }
                        },
                ) {
                    LibraryImageCard(
                        uri = wrappedAsset.asset.getThumbnailUri(),
                        contentPadding = 0.dp,
                        contentScale = ContentScale.Crop,
                        tintImages = false,
                        cornerRadius = animatedCornerRadius,
                        modifier = Modifier.size(56.dp),
                    )
                    PlayPauseButton(Modifier.align(Alignment.Center), rotation)
                    if (isCurrentAssetPlaying) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center).size(36.dp),
                                color = Color.White,
                                trackColor = Color.Transparent,
                            )
                        } else {
                            CircularProgressIndicator(
                                progress = playbackPositionPercent * playStateTransitionProgress,
                                modifier = Modifier.align(Alignment.Center).size(56.dp).alpha(playStateTransitionProgress),
                                color = Color.White,
                                strokeCap = StrokeCap.Round,
                                strokeWidth = 3.dp,
                                trackColor = Color.Transparent,
                            )
                        }
                    }
                }
            }
        },
        trailingContent = {
            if (isCurrentAssetPlaying) {
                val minutes = playbackTime / 60000
                val seconds = (playbackTime / 1000) % 60
                val playTimeFormatted = String.format("%d:%02d", minutes, seconds)

                val annotatedString =
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                            append(playTimeFormatted)
                        }
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.75f))) {
                            append(" / ")
                        }
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.75f))) {
                            append(wrappedAsset.asset.getFormattedDuration())
                        }
                    }
                Text(annotatedString)
            } else {
                Text(wrappedAsset.asset.getFormattedDuration())
            }
        },
        modifier =
            Modifier.combinedClickable(
                onClick = {
                    onAssetClick(wrappedAsset)
                },
                onLongClick = { onAssetLongClick(wrappedAsset) },
            ),
    )
}

@Composable
fun PlayPauseButton(
    modifier: Modifier = Modifier,
    rotation: Float,
) {
    val densityContext = LocalDensity.current
    val triangleToRectangle =
        GenericShape { size, _ ->
            with(densityContext) {
                if (rotation < 90f || rotation > 270f) {
                    val width = Dp(11f).toPx()
                    val height = Dp(14f).toPx()
                    moveTo(width, height / 2)
                    lineTo(0f, height)
                    lineTo(0f, 0f)
                    translate(Offset((size.width - width) / 2f + 2.dp.toPx(), (size.height - height) / 2f))
                    close()
                } else {
                    val width = Dp(4f).toPx()
                    val height = Dp(14f).toPx()
                    val rect = Rect(0f, 0f, width, height).translate((size.width - (width * 3)) / 2, (size.height - height) / 2)
                    addRect(rect)
                    addRect(rect.translate(width * 2, 0f))
                }
            }
        }

    Surface(
        modifier = modifier.size(40.dp),
        shape = RoundedCornerShape(200.dp),
        color = Color.Black.copy(0.5f),
    ) {
        Box(
            modifier =
                Modifier
                    .rotate(rotation)
                    .padding(16.dp)
                    .background(Color.White, triangleToRectangle)
                    .size(24.dp),
        )
    }
}

@UnstableApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AudioAssetContent(
        wrappedAsset =
            WrappedAsset.GenericAsset(
                asset =
                    Asset(
                        id = "",
                        label = "Audio 1",
                        context = AssetContext(""),
                        meta =
                            mapOf(
                                "uri" to "https://dummy",
                                "thumbUri" to "https://dummy",
                                "duration" to "4333",
                            ),
                    ),
                assetSourceType = AssetSourceType.Audio,
                assetType = AssetType.Audio,
            ),
        onAssetClick = {},
        onAssetLongClick = {},
        exoPlayer = { null },
    )
}
