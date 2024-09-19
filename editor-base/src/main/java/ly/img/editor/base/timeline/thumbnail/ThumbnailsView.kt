package ly.img.editor.base.timeline.thumbnail

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ly.img.engine.VideoThumbnailResult

@Composable
fun ThumbnailsView(thumbnails: List<VideoThumbnailResult>) {
    Row(
        modifier = Modifier.fillMaxSize(),
    ) {
        thumbnails.forEach {
            val key = "VideoThumbnailResult(width=${it.width}, height=${it.height}, imageData=${it.imageData.hashCode()})"
            AsyncImage(
                model =
                    ImageRequest.Builder(LocalContext.current)
                        .fetcherFactory(VideoThumbnailResultFetcher.Factory())
                        .memoryCacheKey(key)
                        .data(it)
                        .build(),
                modifier = Modifier.fillMaxHeight(),
                alignment = Alignment.CenterStart,
                contentScale = ContentScale.FillHeight,
                contentDescription = null,
            )
        }
    }
}
