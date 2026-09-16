import android.net.Uri
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import kotlin.math.abs

suspend fun insertMediaVideos(engine: Engine): InsertMediaVideosResult {
    val scene = engine.scene.createForVideo()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 1280F)
    engine.block.setHeight(page, value = 720F)
    engine.block.appendChild(parent = scene, child = page)

    val videoUri = Uri.parse("https://img.ly/static/ubq_video_samples/bbb.mp4")

    // highlight-android-create-video-block
    val videoBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(block = videoBlock, shape = engine.block.createShape(ShapeType.Rect))

    val videoFill = engine.block.createFill(FillType.Video)
    engine.block.setUri(
        block = videoFill,
        property = "fill/video/fileURI",
        value = videoUri,
    )
    engine.block.setFill(block = videoBlock, fill = videoFill)
    engine.block.appendChild(parent = page, child = videoBlock)
    // highlight-android-create-video-block

    // highlight-android-position-and-size
    engine.block.setWidth(block = videoBlock, value = 720F)
    engine.block.setHeight(block = videoBlock, value = 405F)
    engine.block.setPositionX(block = videoBlock, value = 280F)
    engine.block.setPositionY(block = videoBlock, value = 150F)
    engine.block.setContentFillMode(block = videoBlock, mode = ContentFillMode.COVER)
    // highlight-android-position-and-size

    // highlight-android-configure-trim
    engine.block.forceLoadAVResource(block = videoFill)
    val sourceDuration = engine.block.getAVResourceTotalDuration(block = videoFill)

    val trimOffset = 2.0
    require(sourceDuration > trimOffset) { "The source video must be longer than the trim offset." }
    // Keep the demo clip short while clamping to the loaded source duration.
    val trimLength = (sourceDuration - trimOffset).coerceAtMost(5.0).coerceAtLeast(0.1)

    engine.block.setTrimOffset(block = videoFill, offset = trimOffset)
    engine.block.setTrimLength(block = videoFill, length = trimLength)
    engine.block.setDuration(block = videoBlock, duration = trimLength)
    // highlight-android-configure-trim

    val fillType = engine.block.getType(videoFill)
    val actualTrimOffset = engine.block.getTrimOffset(videoFill)
    val actualTrimLength = engine.block.getTrimLength(videoFill)
    val blockDuration = engine.block.getDuration(videoBlock)

    // highlight-android-find-video-blocks
    val videoBlocks = engine.block.findByType(DesignBlockType.Graphic)
        .filter { block ->
            runCatching {
                val fill = engine.block.getFill(block)
                engine.block.getType(fill) == FillType.Video.key
            }.getOrDefault(false)
        }

    val currentFill = engine.block.getFill(videoBlocks.first())
    val currentVideoUri = engine.block.getUri(
        block = currentFill,
        property = "fill/video/fileURI",
    )
    // highlight-android-find-video-blocks

    check(currentVideoUri == videoUri)
    check(fillType == FillType.Video.key)
    check(abs(actualTrimOffset - trimOffset) < 0.001)
    check(abs(actualTrimLength - trimLength) < 0.001)
    check(abs(blockDuration - trimLength) < 0.001)

    val exportedImage = engine.block.export(block = page, mimeType = MimeType.PNG)

    // highlight-android-remove-video
    engine.block.destroy(block = videoBlocks.first())

    val remainingVideoBlocks = engine.block.findByType(DesignBlockType.Graphic)
        .filter { block ->
            runCatching {
                val fill = engine.block.getFill(block)
                engine.block.getType(fill) == FillType.Video.key
            }.getOrDefault(false)
        }
    // highlight-android-remove-video

    check(exportedImage.hasRemaining()) { "The exported video preview is empty." }

    return InsertMediaVideosResult(
        videoUri = currentVideoUri,
        fillType = fillType,
        sourceDuration = sourceDuration,
        trimOffset = actualTrimOffset,
        trimLength = actualTrimLength,
        blockDuration = blockDuration,
        videoBlockCount = videoBlocks.size,
        remainingVideoBlockCount = remainingVideoBlocks.size,
        exportedImage = exportedImage.asReadOnlyBuffer(),
    )
}
