package ly.img.editor.base.timeline.thumbnail

import android.graphics.Bitmap
import androidx.core.graphics.drawable.toDrawable
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.Options
import ly.img.engine.VideoThumbnailResult

class VideoThumbnailResultFetcher(
    private val data: VideoThumbnailResult,
    private val options: Options,
) : Fetcher {
    override suspend fun fetch(): FetchResult {
        val config = Bitmap.Config.ARGB_8888
        val bitmap = Bitmap.createBitmap(data.width, data.height, config)
        bitmap.copyPixelsFromBuffer(data.imageData)

        // Ensure buffer is at the beginning
        data.imageData.rewind()

        return DrawableResult(
            drawable = bitmap.toDrawable(options.context.resources),
            isSampled = false,
            dataSource = DataSource.MEMORY,
        )
    }

    class Factory : Fetcher.Factory<VideoThumbnailResult> {
        override fun create(
            data: VideoThumbnailResult,
            options: Options,
            imageLoader: ImageLoader,
        ): Fetcher {
            return VideoThumbnailResultFetcher(data, options)
        }
    }
}
