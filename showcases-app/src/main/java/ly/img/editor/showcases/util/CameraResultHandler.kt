package ly.img.editor.showcases.util

import android.graphics.PointF
import android.graphics.RectF
import android.net.Uri
import android.util.SizeF
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ly.img.camera.core.CameraResult
import ly.img.camera.core.Recording
import ly.img.editor.HideLoading
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.library.data.TextAssetSource
import ly.img.editor.core.library.data.TypefaceProvider
import ly.img.engine.BlockApi
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.addDefaultAssetSources
import ly.img.engine.addDemoAssetSources
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

suspend fun Engine.createSceneFromReaction(
    cameraResult: CameraResult.Reaction,
    eventHandler: EditorEventHandler,
) = coroutineScope {
    createScene(cameraResult)
    addAssetSources()

    coroutineContext[Job]?.invokeOnCompletion {
        eventHandler.send(HideLoading)
    }
}

private suspend fun Engine.createScene(cameraResult: CameraResult.Reaction) {
    if (scene.get() != null) return

    val video = cameraResult.video

    scene.createFromVideo(video.uri)
    val page = checkNotNull(scene.getCurrentPage())

    val reaction = cameraResult.reaction
    val firstReactionVideoRect = reaction
        .first()
        .videos
        .first()
        .rect
    val unionRect = RectF(video.rect).apply {
        union(firstReactionVideoRect)
    }
    block.setFrame(page, unionRect)

    val videoBlock = block.findByType(DesignBlockType.Graphic).first()
    block.setFrame(videoBlock, video.rect)

    val fill = block.getFill(videoBlock)
    block.forceLoadAVResource(fill)
    val totalDuration = block.getAVResourceTotalDuration(fill).seconds

    var offsetDuration = Duration.ZERO
    reaction.forEachIndexed { index, recording ->
        val recordingBlock = addRecording(
            recording,
            offsetDuration.toDouble(DurationUnit.SECONDS),
            page,
        )

        // Trim the last recording to match the duration
        if (index == reaction.lastIndex) {
            val recordingFill = block.getFill(recordingBlock)
            block.forceLoadAVResource(recordingFill)
            val needsTrimming = recording.duration > (totalDuration - offsetDuration)
            if (needsTrimming) {
                block.setDuration(recordingBlock, (totalDuration - offsetDuration).toDouble(DurationUnit.SECONDS))
            }
        }

        offsetDuration += recording.duration
    }

    // Finally set the duration of the video being reacted to, to the total length of the reaction.
    // The total length should not exceed the duration of the video being reacted to.
    val duration = minOf(offsetDuration.toDouble(DurationUnit.SECONDS), totalDuration.toDouble(DurationUnit.SECONDS))
    block.setTrimLength(fill, duration)
    block.setTrimOffset(fill, 0.0)
    block.setDuration(videoBlock, duration)
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

private suspend fun Engine.addAssetSources() = coroutineScope {
    launch {
        val baseUri = Uri.parse("https://cdn.img.ly/assets/v3")
        addDefaultAssetSources(baseUri = baseUri)
        val defaultTypeface = TypefaceProvider().provideTypeface(this@addAssetSources, "Roboto")
        requireNotNull(defaultTypeface)
        asset.addSource(TextAssetSource(this@addAssetSources, defaultTypeface))
    }
    launch {
        addDemoAssetSources(
            sceneMode = scene.getMode(),
            withUploadAssetSources = true,
            baseUri = Uri.parse("https://cdn.img.ly/assets/demo/v2"),
        )
    }
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
