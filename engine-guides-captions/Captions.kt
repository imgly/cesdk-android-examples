import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.AnimationType
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.PositionMode
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import java.nio.ByteBuffer

private const val TAG = "CaptionsGuide"

suspend fun editVideoCaptions(engine: Engine): ByteBuffer = withContext(Dispatchers.Main) {
    // highlight-android-setup-scene
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1280F)
    engine.block.setHeight(page, value = 720F)
    engine.editor.setSettingBoolean(keypath = "features/videoCaptionsEnabled", value = true)
    // highlight-android-setup-scene

    // highlight-android-set-page-duration
    engine.block.setDuration(page, duration = 20.0)
    // highlight-android-set-page-duration

    // highlight-android-add-video
    val video = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(video, shape = engine.block.createShape(ShapeType.Rect))
    val videoFill = engine.block.createFill(FillType.Video)
    engine.block.setUri(
        block = videoFill,
        property = "fill/video/fileURI",
        value = Uri.parse(
            "https://cdn.img.ly/assets/demo/v1/ly.img.video/videos/pexels-drone-footage-of-a-surfer-barrelling-a-wave-12715991.mp4",
        ),
    )
    engine.block.setFill(video, fill = videoFill)
    engine.block.setDuration(video, duration = 20.0)

    val videoTrack = engine.block.create(DesignBlockType.Track)
    engine.block.appendChild(parent = page, child = videoTrack)
    engine.block.appendChild(parent = videoTrack, child = video)
    engine.block.fillParent(videoTrack)
    // highlight-android-add-video

    // highlight-android-create-caption-track
    val captionTrack = engine.block.create(DesignBlockType.CaptionTrack)
    engine.block.appendChild(parent = page, child = captionTrack)
    // highlight-android-create-caption-track

    // highlight-android-manage-caption-offsets
    val manageOffsetsAutomatically = false
    engine.block.setBoolean(
        block = captionTrack,
        property = "track/automaticallyManageBlockOffsets",
        value = manageOffsetsAutomatically,
    )
    // highlight-android-manage-caption-offsets

    // highlight-android-create-captions
    val caption1 = engine.block.create(DesignBlockType.Caption)
    engine.block.setString(caption1, property = "caption/text", value = "Caption text 1")
    val caption2 = engine.block.create(DesignBlockType.Caption)
    engine.block.setString(caption2, property = "caption/text", value = "Caption text 2")
    engine.block.appendChild(parent = captionTrack, child = caption1)
    engine.block.appendChild(parent = captionTrack, child = caption2)
    // highlight-android-create-captions

    // highlight-android-set-timing
    engine.block.setDuration(caption1, duration = 3.0)
    engine.block.setDuration(caption2, duration = 5.0)

    engine.block.setTimeOffset(caption1, offset = 0.0)
    engine.block.setTimeOffset(caption2, offset = 3.0)
    // highlight-android-set-timing

    // highlight-android-import-captions
    // Captions can also be loaded from a caption file, i.e., from SRT and VTT files.
    // The text and timing of the captions are read from the file.
    val captions = engine.block.createCaptionsFromURI("https://img.ly/static/examples/captions.srt")
    for (caption in captions) {
        engine.block.appendChild(parent = captionTrack, child = caption)
    }
    // highlight-android-import-captions

    // highlight-android-position-size
    // Position and size sync only with caption blocks under the same caption track.
    engine.block.setPositionX(caption1, 0.05F)
    engine.block.setPositionXMode(caption1, PositionMode.PERCENT)
    engine.block.setPositionY(caption1, 0.8F)
    engine.block.setPositionYMode(caption1, PositionMode.PERCENT)
    engine.block.setHeight(caption1, 0.15F)
    engine.block.setHeightMode(caption1, SizeMode.PERCENT)
    engine.block.setWidth(caption1, 0.9F)
    engine.block.setWidthMode(caption1, SizeMode.PERCENT)
    // highlight-android-position-size

    // highlight-android-style-captions
    // Style properties sync only with caption blocks under the same caption track.
    engine.block.setTextColor(caption1, color = Color.fromRGBA(0.9F, 0.9F, 0F, 1F))
    engine.block.setDropShadowEnabled(caption1, enabled = true)
    engine.block.setDropShadowColor(caption1, color = Color.fromRGBA(0F, 0F, 0F, 0.8F))
    engine.block.setBackgroundColorEnabled(caption1, enabled = true)
    engine.block.setBackgroundColor(caption1, color = Color.fromRGBA(0F, 0F, 0F, 0.7F))
    // Use property-keyed setters for caption automatic font sizing properties.
    engine.block.setBoolean(caption1, property = "caption/automaticFontSizeEnabled", value = true)
    engine.block.setFloat(caption1, property = "caption/minAutomaticFontSize", value = 24F)
    engine.block.setFloat(caption1, property = "caption/maxAutomaticFontSize", value = 72F)
    // highlight-android-style-captions

    // highlight-android-add-animation
    val fadeInAnimation = engine.block.createAnimation(AnimationType.Fade)
    engine.block.setDuration(fadeInAnimation, duration = 0.3)
    engine.block.setInAnimation(caption1, animation = fadeInAnimation)
    // highlight-android-add-animation

    // highlight-android-export-video
    // Export page as mp4 video.
    val videoBytes = engine.block.exportVideo(
        block = page,
        timeOffset = 0.0,
        duration = engine.block.getDuration(page),
        mimeType = MimeType.MP4,
        progressCallback = {
            Log.i(
                TAG,
                "Rendered ${it.renderedFrames} frames and encoded ${it.encodedFrames} frames out of ${it.totalFrames} frames",
            )
        },
    )
    // highlight-android-export-video

    check(videoBytes.remaining() > 0)
    videoBytes
}
