package ly.img.cesdk.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ly.img.cesdk.core.iconpack.Erroroutline
import ly.img.cesdk.core.iconpack.IconPack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryImageCard(
    uri: String,
    onClick: () -> Unit,
    contentScale: ContentScale,
    contentPadding: Dp = 0.dp,
    tintImages: Boolean
) {
    Surface(
        modifier = Modifier.aspectRatio(1f),
        shape = MaterialTheme.shapes.medium,
        onClick = onClick
    ) {
        val gradient = remember { Brush.linearGradient(listOf(Color(0x14FEFBFF), Color(0x141B1B1F))) }
        Box(modifier = Modifier.background(Color(0x29ACAAAF))) {
            Box(modifier = Modifier.background(gradient)) {
                var state by remember { mutableStateOf(ImageState.Idle) }
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
                if (state == ImageState.Loading) {
                    CircularProgressIndicator(
                        Modifier
                            .then(Modifier.size(18.dp))
                            .align(Alignment.Center), strokeWidth = 1.dp
                    )
                } else if (state == ImageState.Error) {
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
    }
}

private enum class ImageState {
    Idle,
    Loading,
    Error,
    Success
}