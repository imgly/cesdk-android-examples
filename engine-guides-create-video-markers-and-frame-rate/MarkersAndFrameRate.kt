import android.util.Log
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.EngineException
import ly.img.engine.FillType
import ly.img.engine.Keyframe
import ly.img.engine.KeyframeValue
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

private const val TAG = "MarkersAndFrameRate"

data class MarkersAndFrameRate(
    val defaultFrameRate: Float,
    val frameRate: Float,
    val markerNames: List<String>,
    val playbackTimeAtBeat: Double,
    val markerCountAfterRemove: Int,
    val frameRateErrorCode: String?,
    val markerTimeErrorCode: String?,
    val markerIndexErrorCode: String?,
    val previewPng: ByteBuffer,
)

suspend fun markersAndFrameRate(engine: Engine): MarkersAndFrameRate {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 1920F)
    engine.block.setHeight(block = page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setDuration(block = page, duration = 10.0)

    // highlight-android-create-ball
    // Creates a ball that crosses the page in 10 seconds
    fun createBall() {
        val ball = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block = ball, shape = engine.block.createShape(ShapeType.Ellipse))
        engine.block.setFill(block = ball, fill = engine.block.createFill(FillType.Color))
        engine.block.setFillSolidColor(block = ball, color = Color.fromRGBA(r = 0.2F, g = 0.4F, b = 0.9F, a = 1F))
        engine.block.setWidth(block = ball, value = 160F)
        engine.block.setHeight(block = ball, value = 160F)
        engine.block.setPositionY(block = ball, value = 460F)
        engine.block.appendChild(parent = page, child = ball)
        engine.block.setDuration(block = ball, duration = 10.0)
        engine.block.setKeyframes(
            block = ball,
            property = "position/x",
            keyframes = listOf(
                Keyframe(time = 0F, value = KeyframeValue.Float(100F)),
                Keyframe(time = 1F, value = KeyframeValue.Float(1660F)),
            ),
        )
    }
    // highlight-android-create-ball

    // highlight-android-ball
    // A ball crosses the page over the 10 seconds of the page
    createBall()
    // highlight-android-ball

    // highlight-android-frame-rate
    // 30 frames per second by default
    val defaultFrameRate = engine.block.getFloat(block = scene, property = "scene/frameRate")
    engine.block.setFloat(block = scene, property = "scene/frameRate", value = 25F)
    // 25.0
    val frameRate = engine.block.getFloat(block = scene, property = "scene/frameRate")
    Log.i(TAG, "$defaultFrameRate, $frameRate")
    // highlight-android-frame-rate

    // highlight-android-add-markers
    // A marker has a name, a start time and a duration in seconds.
    // A duration of 0 marks a point.
    engine.scene.addMarker(name = "intro", time = 0.0, duration = 2.5)
    engine.scene.addMarker(name = "beat", time = 4.0)
    engine.scene.addMarker(name = "beat", time = 6.0)
    engine.scene.addMarker(name = "outro", time = 8.0, duration = 2.0)
    // highlight-android-add-markers

    // highlight-android-read-markers
    val markers = engine.scene.getMarkers()
    // [intro, beat, beat, outro]
    val markerNames = markers.map { it.name }
    Log.i(TAG, "$markerNames")
    // highlight-android-read-markers

    // highlight-android-seek
    // Jump to the first marker named "beat"
    markers.firstOrNull { it.name == "beat" }?.let { beat ->
        engine.block.setPlaybackTime(block = page, time = beat.time)
    }
    // 4.0
    val playbackTimeAtBeat = engine.block.getPlaybackTime(block = page)
    Log.i(TAG, "$playbackTimeAtBeat")
    // highlight-android-seek

    // highlight-android-remove-marker
    // Remove the second "beat" by its index in the list
    engine.scene.removeMarker(index = 2)
    // 3
    val markerCountAfterRemove = engine.scene.getMarkers().size
    Log.i(TAG, "$markerCountAfterRemove")
    // highlight-android-remove-marker

    // highlight-android-errors
    val frameRateErrorCode = try {
        // A frame rate is greater than 0 and at most 240
        engine.block.setFloat(block = scene, property = "scene/frameRate", value = 0F)
        null
    } catch (error: EngineException) {
        // SCENE.FRAME_RATE_OUT_OF_RANGE
        error.code
    }
    val markerTimeErrorCode = try {
        engine.scene.addMarker(name = "before", time = -1.0)
        null
    } catch (error: EngineException) {
        // SCENE.MARKER_INVALID_TIME
        error.code
    }
    val markerIndexErrorCode = try {
        engine.scene.removeMarker(index = 10)
        null
    } catch (error: EngineException) {
        // SCENE.MARKER_INDEX_OUT_OF_RANGE
        error.code
    }
    // highlight-android-errors

    // highlight-android-export
    // The playback time is at the "beat" marker, 4 seconds into the page
    val previewPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    Log.i(TAG, "PNG bytes: ${previewPng.remaining()}")
    // highlight-android-export

    return MarkersAndFrameRate(
        defaultFrameRate = defaultFrameRate,
        frameRate = frameRate,
        markerNames = markerNames,
        playbackTimeAtBeat = playbackTimeAtBeat,
        markerCountAfterRemove = markerCountAfterRemove,
        frameRateErrorCode = frameRateErrorCode,
        markerTimeErrorCode = markerTimeErrorCode,
        markerIndexErrorCode = markerIndexErrorCode,
        previewPng = previewPng,
    )
}
