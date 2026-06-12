import android.net.Uri
import kotlinx.coroutines.flow.toList
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.Source

suspend fun videoFills(engine: Engine): VideoFillsResult {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 1280F)
    engine.block.setHeight(page, value = 720F)
    engine.block.appendChild(parent = scene, child = page)

    // highlight-android-check-fill-support
    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = block, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(block, value = 640F)
    engine.block.setHeight(block, value = 360F)
    engine.block.appendChild(parent = page, child = block)

    check(engine.block.supportsFill(block)) { "Graphic blocks can receive fills." }
    // highlight-android-check-fill-support

    // highlight-android-create-video-fill
    val videoFill = engine.block.createFill(FillType.Video)
    val videoUri = Uri.parse("https://img.ly/static/ubq_video_samples/bbb.mp4")
    engine.block.setUri(
        block = videoFill,
        property = "fill/video/fileURI",
        value = videoUri,
    )
    engine.block.setFill(block = block, fill = videoFill)
    // highlight-android-create-video-fill

    // highlight-android-read-current-fill
    val currentFill = engine.block.getFill(block)
    val currentFillType = engine.block.getType(currentFill)
    val currentVideoUri = engine.block.getUri(
        block = currentFill,
        property = "fill/video/fileURI",
    )
    // highlight-android-read-current-fill

    // highlight-android-content-fill-modes
    check(engine.block.supportsContentFillMode(block)) {
        "Graphic blocks can scale video fills inside their frame."
    }

    engine.block.setContentFillMode(block = block, mode = ContentFillMode.COVER)
    val coverMode = engine.block.getContentFillMode(block)

    engine.block.setContentFillMode(block = block, mode = ContentFillMode.CONTAIN)
    val containMode = engine.block.getContentFillMode(block)
    // highlight-android-content-fill-modes

    // highlight-android-source-set
    engine.block.setSourceSet(
        block = videoFill,
        property = "fill/video/sourceSet",
        sourceSet = listOf(
            Source(
                uri = Uri.parse("https://img.ly/static/example-assets/sourceset/1x.mp4"),
                width = 720,
                height = 1280,
            ),
            Source(
                uri = Uri.parse("https://img.ly/static/example-assets/sourceset/2x.mp4"),
                width = 1440,
                height = 2560,
            ),
        ),
    )
    val responsiveSources = engine.block.getSourceSet(
        block = videoFill,
        property = "fill/video/sourceSet",
    )
    // Setting a source set can reset crop state on connected blocks.
    engine.block.setContentFillMode(block = block, mode = ContentFillMode.CONTAIN)
    val finalContentFillMode = engine.block.getContentFillMode(block)
    // highlight-android-source-set

    // highlight-android-load-resource
    engine.block.forceLoadAVResource(block = videoFill)
    val durationSeconds = engine.block.getAVResourceTotalDuration(videoFill)
    check(durationSeconds > 0.0) { "The video fill must contain playable media." }

    // Generate thumbnails from the first two seconds of the video fill.
    val thumbnailPreviewEndSeconds = durationSeconds.coerceAtMost(2.0)
    val thumbnails = engine.block.generateVideoThumbnailSequence(
        block = videoFill,
        thumbnailHeight = 72,
        timeBegin = 0.0,
        timeEnd = thumbnailPreviewEndSeconds,
        numberOfFrames = 3,
    ).toList()
    // highlight-android-load-resource

    // highlight-android-shape-opacity-shared-fill
    val ellipseBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(ellipseBlock, shape = engine.block.createShape(ShapeType.Ellipse))
    engine.block.setWidth(ellipseBlock, value = 320F)
    engine.block.setHeight(ellipseBlock, value = 320F)
    engine.block.setPositionX(ellipseBlock, value = 760F)
    engine.block.setPositionY(ellipseBlock, value = 200F)
    engine.block.setOpacity(ellipseBlock, value = 0.72F)
    engine.block.setFill(block = ellipseBlock, fill = videoFill)
    engine.block.appendChild(parent = page, child = ellipseBlock)

    val sharedFill = engine.block.getFill(ellipseBlock)
    val opacity = engine.block.getOpacity(ellipseBlock)
    // highlight-android-shape-opacity-shared-fill

    return VideoFillsResult(
        currentFillType = currentFillType,
        currentVideoUri = currentVideoUri,
        coverMode = coverMode,
        containMode = containMode,
        finalContentFillMode = finalContentFillMode,
        responsiveSourceCount = responsiveSources.size,
        durationSeconds = durationSeconds,
        thumbnailCount = thumbnails.size,
        thumbnailsHavePixels = thumbnails.all { it.imageData.remaining() > 0 },
        sharedFillType = engine.block.getType(sharedFill),
        opacity = opacity,
    )
}
