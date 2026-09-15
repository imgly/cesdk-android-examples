package ly.img.editor.showcases.util

import android.graphics.PointF
import android.graphics.RectF
import android.util.SizeF
import ly.img.camera.core.CameraResult
import ly.img.camera.core.Recording
import ly.img.editor.configuration.video.VideoConfigurationBuilder
import ly.img.engine.BlockApi
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

suspend fun VideoConfigurationBuilder.onCreateSceneFromReaction(cameraResult: CameraResult.Reaction) {
    val engine = editorContext.engine
    engine.scene.get()?.let { return }
    engine.scene.createFromVideo(cameraResult.video.uri)
}

suspend fun VideoConfigurationBuilder.onPostCreateSceneFromReaction(cameraResult: CameraResult.Reaction) {
    val engine = editorContext.engine
    val video = cameraResult.video
    val page = checkNotNull(engine.scene.getCurrentPage())
    val reaction = cameraResult.reaction
    val firstReactionVideoRect = reaction
        .first()
        .videos
        .first()
        .rect
    val unionRect = RectF(video.rect).apply {
        union(firstReactionVideoRect)
    }
    engine.block.setFrame(page, unionRect)

    val videoBlock = engine.block.findByType(DesignBlockType.Graphic).first()
    engine.block.setFrame(videoBlock, video.rect)

    val fill = engine.block.getFill(videoBlock)
    engine.block.forceLoadAVResource(fill)
    val totalDuration = engine.block.getAVResourceTotalDuration(fill).seconds

    var offsetDuration = Duration.ZERO
    reaction.forEachIndexed { index, recording ->
        val recordingBlock = engine.addRecording(
            recording,
            offsetDuration.toDouble(DurationUnit.SECONDS),
            page,
        )

        // Trim the last recording to match the duration
        if (index == reaction.lastIndex) {
            val recordingFill = engine.block.getFill(recordingBlock)
            engine.block.forceLoadAVResource(recordingFill)
            val needsTrimming = recording.duration > (totalDuration - offsetDuration)
            if (needsTrimming) {
                engine.block.setDuration(recordingBlock, (totalDuration - offsetDuration).toDouble(DurationUnit.SECONDS))
            }
        }

        offsetDuration += recording.duration
    }

    // Finally set the duration of the video being reacted to, to the total length of the reaction.
    // The total length should not exceed the duration of the video being reacted to.
    val duration = minOf(offsetDuration.toDouble(DurationUnit.SECONDS), totalDuration.toDouble(DurationUnit.SECONDS))
    engine.block.setTrimLength(fill, duration)
    engine.block.setTrimOffset(fill, 0.0)
    engine.block.setDuration(videoBlock, duration)
}

private fun Engine.addRecording(
    recording: Recording,
    offset: Double,
    parent: DesignBlock,
): DesignBlock {
    val id = block.create(DesignBlockType.Graphic)
    val rectShape = block.createShape(ShapeType.Rect)
    block.setShape(id, rectShape)
    block.appendChild(parent, id)
    val video = recording.videos.first()
    block.setFrame(id, video.rect)
    block.setTimeOffset(id, offset)
    val fill = block.createFill(FillType.Video)
    block.setString(fill, "fill/video/fileURI", video.uri.toString())
    block.setFill(id, fill)
    block.setDuration(id, recording.duration.toDouble(DurationUnit.SECONDS))
    return id
}

private fun BlockApi.setFrame(
    designBlock: DesignBlock,
    rect: RectF,
) {
    setSize(designBlock, SizeF(rect.width(), rect.height()))
    setPosition(designBlock, PointF(rect.left, rect.top))
}

private fun BlockApi.setSize(
    designBlock: DesignBlock,
    size: SizeF,
) {
    setWidth(designBlock, size.width)
    setHeight(designBlock, size.height)
}

private fun BlockApi.setPosition(
    designBlock: DesignBlock,
    point: PointF,
) {
    setPositionX(designBlock, point.x)
    setPositionY(designBlock, point.y)
}
