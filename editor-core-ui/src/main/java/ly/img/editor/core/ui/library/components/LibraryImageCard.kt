package ly.img.editor.core.ui.library.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ly.img.editor.core.ui.GradientCard
import ly.img.editor.core.ui.iconpack.Erroroutline
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.library.util.shimmerWithLocalShimmer
import ly.img.editor.core.ui.utils.ifTrue

private const val HEADER_USER_AGENT_KEY = "User-Agent"
private const val HEADER_USER_AGENT_VALUE = "IMG.LY SDK"

@Composable
internal fun LibraryImageCard(
    modifier: Modifier = Modifier,
    uri: String? = null,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    contentPadding: Dp = 0.dp,
    contentScale: ContentScale,
    tintImages: Boolean,
    cornerRadius: Dp = 12.0.dp,
) {
    var state by remember { mutableStateOf(ImageState.Loading) }
    GradientCard(
        modifier =
            modifier
                .testTag(tag = "LibraryImageCard${uri?.toUri()?.path}")
                .aspectRatio(1f)
                .ifTrue(state == ImageState.Loading) {
                    shimmerWithLocalShimmer()
                },
        onClick = onClick,
        onLongClick = onLongClick,
        cornerRadius = cornerRadius,
    ) {
        if (uri != null) {
            AsyncImage(
                model =
                    ImageRequest.Builder(LocalContext.current)
                        .data(uri)
                        .addHeader(HEADER_USER_AGENT_KEY, HEADER_USER_AGENT_VALUE)
                        .build(),
                onLoading = {
                    state = ImageState.Loading
                },
                onSuccess = {
                    state = ImageState.Success
                },
                onError = {
                    state = ImageState.Error
                },
                contentScale = contentScale,
                contentDescription = null,
                placeholder =
                    if (LocalInspectionMode.current) {
                        GradientPainter(
                            Brush.linearGradient(
                                colors = listOf(Color.Blue, Color.Red),
                            ),
                        )
                    } else {
                        null
                    },
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                colorFilter =
                    if (tintImages) {
                        ColorFilter.tint(
                            MaterialTheme.colorScheme.onSurface,
                        )
                    } else {
                        null
                    },
            )
        }
        if (state == ImageState.Error) {
            Icon(
                IconPack.Erroroutline,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(18.dp)
                        .align(Alignment.Center),
            )
        }
    }
}

class GradientPainter(private val brush: Brush) : Painter() {
    override val intrinsicSize: Size
        get() = Size(256f, 256f)

    override fun DrawScope.onDraw() {
        drawRect(brush = brush)
    }
}

private enum class ImageState {
    Loading,
    Error,
    Success,
}
