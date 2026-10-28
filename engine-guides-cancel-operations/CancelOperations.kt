import android.net.Uri
import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

private const val TAG = "CancelOperations"

data class CancelOperationsResult(
    val firstExportCancelled: Boolean,
    val nextExport: ByteBuffer,
    val sceneLoadedInTime: Boolean,
)

suspend fun cancelOperations(
    engine: Engine,
    assetBaseUri: Uri,
): CancelOperationsResult = coroutineScope {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    // The image downloads while the export runs, so the export has work for the cancel to stop.
    val imageBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(imageBlock, shape = engine.block.createShape(ShapeType.Rect))
    val imageFill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        block = imageFill,
        property = "fill/image/imageFileURI",
        value = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg"),
    )
    engine.block.setFill(imageBlock, fill = imageFill)
    engine.block.setWidth(imageBlock, value = 800F)
    engine.block.setHeight(imageBlock, value = 600F)
    engine.block.appendChild(parent = page, child = imageBlock)

    // highlight-android-start-export
    val export = launch {
        try {
            engine.block.export(block = page, mimeType = MimeType.PNG)
            Log.i(TAG, "The export ended before the cancel")
        } catch (e: CancellationException) {
            Log.i(TAG, "The export stopped")
            throw e
        }
    }
    // highlight-android-start-export

    letTheExportStart()

    // highlight-android-cancel-export
    export.cancelAndJoin()
    // highlight-android-cancel-export
    val firstExportCancelled = export.isCancelled

    // highlight-android-export-again
    val png = engine.block.export(block = page, mimeType = MimeType.PNG)
    Log.i(TAG, "The next export made a PNG file of ${png.remaining()} bytes")
    // highlight-android-export-again

    val sceneUri = assetBaseUri
        .buildUpon()
        .appendPath("ly.img.templates")
        .appendPath("templates")
        .appendPath("cesdk_postcard_1.scene")
        .build()

    // highlight-android-deadline
    val sceneLoadedInTime = try {
        withTimeout(10_000) {
            engine.scene.load(sceneUri = sceneUri)
        }
        Log.i(TAG, "The scene loaded in time")
        true
    } catch (e: TimeoutCancellationException) {
        Log.i(TAG, "The scene did not load within 10 seconds")
        false
    }
    // highlight-android-deadline

    CancelOperationsResult(
        firstExportCancelled = firstExportCancelled,
        nextExport = png,
        sceneLoadedInTime = sceneLoadedInTime,
    )
}

// highlight-android-cancel-from-progress
suspend fun exportVideoUnlessSlow(
    engine: Engine,
    page: DesignBlock,
): ByteBuffer? = coroutineScope {
    val startedAt = SystemClock.elapsedRealtime()
    val export = async {
        engine.block.exportVideo(
            block = page,
            timeOffset = 0.0,
            duration = engine.block.getDuration(page),
            mimeType = MimeType.MP4,
            progressCallback = { progress ->
                val elapsedMillis = SystemClock.elapsedRealtime() - startedAt
                // Less than half of the frames after one minute: the export takes too long.
                if (elapsedMillis > 60_000 && progress.renderedFrames < progress.totalFrames / 2) {
                    this@async.cancel()
                }
            },
        )
    }
    try {
        export.await().also { video ->
            Log.i(TAG, "The video file has ${video.remaining()} bytes")
        }
    } catch (e: CancellationException) {
        // Throws again when the caller was cancelled, not the slow export.
        ensureActive()
        Log.i(TAG, "The export was too slow and stopped")
        null
    }
}
// highlight-android-cancel-from-progress

// A coroutine that is cancelled before it runs never starts its export. In an app the cancel comes later, for example
// from a cancel button, so the sample lets the launched export start first.
private suspend fun letTheExportStart() = yield()
