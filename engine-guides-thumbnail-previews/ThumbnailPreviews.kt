import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.VideoThumbnailResult
import kotlin.math.roundToInt

/**
 * The engine renders thumbnails at most this large on each side. Larger requests are
 * bilinear-upscaled from that render, so they cost more memory without adding detail.
 */
private const val MAX_THUMBNAIL_RENDER_SIZE = 512

data class Waveform(
    val left: List<Float>,
    val right: List<Float>,
    val chunkCount: Int,
)

data class ThumbnailPreviews(
    val filmstrip: List<Bitmap>,
    val storyboard: List<Bitmap>,
    val previewFrame: Bitmap,
    val waveform: Waveform,
    val framesBeforeCancel: Int,
)

suspend fun thumbnailPreviews(
    engine: Engine,
    assetBaseUri: Uri,
): ThumbnailPreviews {
    val videoUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.video")
        .appendPath("videos")
        .appendPath("pexels-drone-footage-of-a-surfer-barrelling-a-wave-12715991.mp4")
        .build()
    val audioUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.audio")
        .appendPath("audios")
        .appendPath("far_from_home.m4a")
        .build()

    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1280F)
    engine.block.setHeight(page, value = 720F)
    engine.block.setDuration(page, duration = 10.0)

    val videoTrack = engine.block.create(DesignBlockType.Track)
    engine.block.appendChild(parent = page, child = videoTrack)

    val clip = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(clip, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(clip, value = 1280F)
    engine.block.setHeight(clip, value = 720F)

    val videoFill = engine.block.createFill(FillType.Video)
    engine.block.setUri(
        block = videoFill,
        property = "fill/video/fileURI",
        value = videoUri,
    )
    engine.block.setFill(clip, fill = videoFill)
    engine.block.appendChild(parent = videoTrack, child = clip)

    val audioTrack = engine.block.create(DesignBlockType.Track)
    engine.block.appendChild(parent = page, child = audioTrack)

    val audioClip = engine.block.create(DesignBlockType.Audio)
    engine.block.setUri(
        block = audioClip,
        property = "audio/fileURI",
        value = audioUri,
    )
    engine.block.appendChild(parent = audioTrack, child = audioClip)

    engine.block.setDuration(clip, duration = 10.0)
    engine.block.setDuration(audioClip, duration = 10.0)

    // highlight-android-media
    // Both APIs wait for the resource on their own, but loading it up front avoids a first
    // request that returns before the media is decodable.
    engine.block.forceLoadAVResource(videoFill)
    engine.block.forceLoadAVResource(audioClip)

    // Video fills are sampled in media time, so measure the source, not the clip.
    val sourceDuration = engine.block.getAVResourceTotalDuration(videoFill)
    // highlight-android-media

    return ThumbnailPreviews(
        filmstrip = generateFilmstrip(engine = engine, videoFill = videoFill, sourceDuration = sourceDuration),
        storyboard = generateStoryboard(engine = engine, page = page),
        previewFrame = capturePreviewFrame(engine = engine, page = page, time = 4.0),
        waveform = generateWaveform(engine = engine, block = audioClip, duration = 10.0),
        framesBeforeCancel = cancelFilmstripEarly(engine = engine, videoFill = videoFill, sourceDuration = sourceDuration),
    )
}

// highlight-android-density
// thumbnailHeight is measured in pixels, not dp, so convert your layout height first.
// Clamping to the engine's render cap keeps the request at a size that adds real detail.
fun thumbnailHeightPx(dp: Float): Int {
    val density = Resources.getSystem().displayMetrics.density
    return (dp * density).roundToInt().coerceIn(1, MAX_THUMBNAIL_RENDER_SIZE)
}
// highlight-android-density

// highlight-android-filmstrip
suspend fun generateFilmstrip(
    engine: Engine,
    videoFill: DesignBlock,
    sourceDuration: Double,
    numberOfFrames: Int = 8,
): List<Bitmap> {
    // Frames are not guaranteed to arrive in index order, so key them by the reported index.
    val frames = sortedMapOf<Int, VideoThumbnailResult>()

    engine.block.generateVideoThumbnailSequence(
        block = videoFill,
        thumbnailHeight = thumbnailHeightPx(dp = 48F),
        timeBegin = 0.0,
        timeEnd = sourceDuration,
        numberOfFrames = numberOfFrames,
    ).collect { result ->
        frames[result.frameIndex] = result
    }

    // Collection has to happen on the main thread, and the collector must not suspend:
    // the callback channel is bounded, so a slow collector drops frames. Decode after.
    return withContext(Dispatchers.Default) {
        frames.values.map(::videoThumbnailToBitmap)
    }
}
// highlight-android-filmstrip

// highlight-android-bitmap
fun videoThumbnailToBitmap(result: VideoThumbnailResult): Bitmap {
    val bitmap = Bitmap.createBitmap(result.width, result.height, Bitmap.Config.ARGB_8888)
    bitmap.copyPixelsFromBuffer(result.imageData)

    // copyPixelsFromBuffer leaves the buffer position at its limit. Rewind it, or converting
    // the same result again throws "Buffer not large enough for pixels".
    result.imageData.rewind()

    return bitmap
}
// highlight-android-bitmap

// highlight-android-storyboard
suspend fun generateStoryboard(
    engine: Engine,
    page: DesignBlock,
    numberOfFrames: Int = 6,
): List<Bitmap> {
    val frames = sortedMapOf<Int, VideoThumbnailResult>()

    engine.block.generateVideoThumbnailSequence(
        block = page,
        thumbnailHeight = thumbnailHeightPx(dp = 96F),
        timeBegin = 0.0,
        timeEnd = engine.block.getDuration(page),
        numberOfFrames = numberOfFrames,
    ).collect { result ->
        frames[result.frameIndex] = result
    }

    return withContext(Dispatchers.Default) {
        frames.values.map(::videoThumbnailToBitmap)
    }
}
// highlight-android-storyboard

// highlight-android-single-frame
suspend fun capturePreviewFrame(
    engine: Engine,
    page: DesignBlock,
    time: Double,
): Bitmap {
    // A one-frame request over a zero-length range samples exactly `time`.
    val result = engine.block.generateVideoThumbnailSequence(
        block = page,
        thumbnailHeight = MAX_THUMBNAIL_RENDER_SIZE,
        timeBegin = time,
        timeEnd = time,
        numberOfFrames = 1,
    ).first()

    return withContext(Dispatchers.Default) {
        videoThumbnailToBitmap(result)
    }
}
// highlight-android-single-frame

// highlight-android-waveform
suspend fun generateWaveform(
    engine: Engine,
    block: DesignBlock,
    duration: Double,
    numberOfSamples: Int = 240,
    samplesPerChunk: Int = 60,
    numberOfChannels: Int = 2,
): Waveform {
    val chunks = sortedMapOf<Int, List<Float>>()

    engine.block.generateAudioThumbnailSequence(
        block = block,
        samplesPerChunk = samplesPerChunk,
        timeBegin = 0.0,
        timeEnd = duration,
        numberOfSamples = numberOfSamples,
        numberOfChannels = numberOfChannels,
    ).collect { chunk ->
        // `samples` is a List<Float>, not a FloatArray, and every value already sits in 0..1.
        chunks[chunk.chunkIndex] = chunk.samples
    }

    val left = ArrayList<Float>(numberOfSamples)
    val right = ArrayList<Float>(numberOfSamples)
    chunks.values.forEach { samples ->
        // Stereo chunks interleave left-then-right, and the last chunk may be short.
        samples.chunked(numberOfChannels).forEach { frame ->
            left += frame.first()
            right += frame.last()
        }
    }

    return Waveform(left = left, right = right, chunkCount = chunks.size)
}
// highlight-android-waveform

// highlight-android-cancel
suspend fun cancelFilmstripEarly(
    engine: Engine,
    videoFill: DesignBlock,
    sourceDuration: Double,
): Int = coroutineScope {
    var received = 0

    // In an app this is viewModelScope or lifecycleScope — both dispatch on the main thread,
    // which is where the Flow requires you to collect.
    val job = launch {
        engine.block.generateVideoThumbnailSequence(
            block = videoFill,
            thumbnailHeight = thumbnailHeightPx(dp = 48F),
            timeBegin = 0.0,
            timeEnd = sourceDuration,
            numberOfFrames = 120,
        ).collect { received += 1 }
    }

    delay(timeMillis = 250)

    // There is no cancel method on the API. Cancelling the collecting Job closes the Flow,
    // which cancels the native request. Do this before requesting a new sequence for the
    // same block, otherwise the new request waits for the old one to finish.
    job.cancelAndJoin()

    received
}
// highlight-android-cancel
