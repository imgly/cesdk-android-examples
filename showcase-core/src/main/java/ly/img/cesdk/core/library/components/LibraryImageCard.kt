package ly.img.cesdk.core.library.components

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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ly.img.cesdk.core.iconpack.Erroroutline
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.library.util.shimmerWithLocalShimmer
import ly.img.cesdk.core.ui.GradientCard
import ly.img.cesdk.core.ui.utils.ifTrue

@Composable
internal fun LibraryImageCard(
    modifier: Modifier = Modifier,
    uri: String? = null,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    contentPadding: Dp = 0.dp,
    contentScale: ContentScale,
    tintImages: Boolean,
) {
    var state by remember { mutableStateOf(ImageState.Loading) }
    GradientCard(
        modifier = modifier
            .aspectRatio(1f)
            .ifTrue(state == ImageState.Loading) {
                shimmerWithLocalShimmer()
            },
        onClick = onClick,
        onLongClick = onLongClick
    ) {
        if (uri != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uri)
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                colorFilter = if (tintImages) ColorFilter.tint(MaterialTheme.colorScheme.onSurface) else null
            )
        }
        if (state == ImageState.Error) {
            Icon(
                IconPack.Erroroutline,
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

private enum class ImageState {
    Loading,
    Error,
    Success
}