import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.MimeType
import ly.img.engine.PositionMode
import ly.img.engine.SizeMode

fun editVideoCaptions(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1280, height = 720)

    // highlight-setupScene
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 1280F)
    engine.block.setHeight(page, value = 720F)
    engine.editor.setSettingBoolean(keypath = "features/videoCaptionsEnabled", value = true)
    // highlight-setupScene

    // highlight-setPageDuration
    engine.block.setDuration(page, duration = 20.0)
    // highlight-setPageDuration

    // highlight-createCaptions
    val caption1 = engine.block.create(DesignBlockType.Caption)
    engine.block.setString(caption1, property = "caption/text", value = "Caption text 1")
    val caption2 = engine.block.create(DesignBlockType.Caption)
    engine.block.setString(caption2, property = "caption/text", value = "Caption text 2")
    // highlight-createCaptions

    // highlight-addToTrack
    val captionTrack = engine.block.create("captionTrack")
    engine.block.appendChild(parent = page, child = captionTrack)
    engine.block.appendChild(parent = captionTrack, child = caption1)
    engine.block.appendChild(parent = captionTrack, child = caption2)
    // highlight-addToTrack

    // highlight-setDuration
    engine.block.setDuration(caption1, duration = 3.0)
    engine.block.setDuration(caption2, duration = 5.0)
    // highlight-setDuration

    // highlight-setTimeOffset
    engine.block.setTimeOffset(caption1, offset = 0.0)
    engine.block.setTimeOffset(caption2, offset = 3.0)
    // highlight-setTimeOffset

    // highlight-createCaptionsFromURI
    // Captions can also be loaded from a caption file, i.e., from SRT and VTT files.
    // The text and timing of the captions are read from the file.
    val captions = engine.block.createCaptionsFromURI("https://img.ly/static/examples/captions.srt")
    for (caption in captions) {
        engine.block.appendChild(parent = captionTrack, child = caption)
    }
    // highlight-createCaptionsFromURI

    // highlight-positionAndSize
    // The position and size are synced with all caption blocks in the scene so only needs to be set once.
    engine.block.setPositionX(caption1, 0.05F)
    engine.block.setPositionXMode(caption1, PositionMode.PERCENT)
    engine.block.setPositionY(caption1, 0.8F)
    engine.block.setPositionYMode(caption1, PositionMode.PERCENT)
    engine.block.setHeight(caption1, 0.15F)
    engine.block.setHeightMode(caption1, SizeMode.PERCENT)
    engine.block.setWidth(caption1, 0.9F)
    engine.block.setWidthMode(caption1, SizeMode.PERCENT)
    // highlight-positionAndSize

    // highlight-changeStyle
    // The style is synced with all caption blocks in the scene so only needs to be set once.
    engine.block.setColor(caption1, property = "fill/solid/color", value = Color.fromRGBA(0.9F, 0.9F, 0F, 1F))
    engine.block.setBoolean(caption1, property = "dropShadow/enabled", value = true)
    engine.block.setColor(caption1, property = "dropShadow/color", value = Color.fromRGBA(0F, 0F, 0F, 0.8F))
    // highlight-changeStyle

    // highlight-exportVideo
    // Export page as mp4 video.
    val blob = engine.block.exportVideo(
        block = page,
        timeOffset = 0.0,
        duration = engine.block.getDuration(page),
        mimeType = MimeType.MP4,
        progressCallback = {
            println(
                "Rendered ${it.renderedFrames} frames and encoded ${it.encodedFrames} frames out of ${it.totalFrames} frames",
            )
        },
    )
    // highlight-exportVideo

    engine.stop()
}
