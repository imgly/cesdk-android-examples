import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.BlurType
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

private const val PAGE_WIDTH = 1280F
private const val PAGE_HEIGHT = 720F
private const val SEGMENT_DURATION = 5.0

data class Redaction(
    val pageDuration: Double,
    val fullBlockBlurEnabled: Boolean,
    val partialBlurEnabled: Boolean,
    val partialRedactionX: Float,
    val partialRedactionY: Float,
    val partialRedactionWidth: Float,
    val partialRedactionHeight: Float,
    val partialCropScaleX: Float,
    val partialCropScaleY: Float,
    val partialCropTranslationX: Float,
    val partialCropTranslationY: Float,
    val pixelizationEnabled: Boolean,
    val solidOverlayDuration: Double,
    val timedSourceOffset: Double,
    val timedSourceDuration: Double,
    val timedRedactionOffset: Double,
    val timedRedactionDuration: Double,
    val radialBlurEnabled: Boolean,
)

suspend fun redaction(engine: Engine): Redaction = withContext(Dispatchers.Main) {
    val scene = engine.scene.createForVideo()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = PAGE_WIDTH)
    engine.block.setHeight(page, value = PAGE_HEIGHT)
    engine.block.setDuration(page, duration = SEGMENT_DURATION * 5)

    val videoUris = listOf(
        "https://cdn.img.ly/assets/demo/v3/ly.img.video/videos/pexels-taryn-elliott-8713114.mp4",
        "https://cdn.img.ly/assets/demo/v3/ly.img.video/videos/pexels-drone-footage-of-a-surfer-barrelling-a-wave-12715991.mp4",
        "https://cdn.img.ly/assets/demo/v3/ly.img.video/videos/pexels-taryn-elliott-7108793.mp4",
        "https://cdn.img.ly/assets/demo/v3/ly.img.video/videos/pexels-taryn-elliott-7108801.mp4",
        "https://cdn.img.ly/assets/demo/v3/ly.img.video/videos/pexels-taryn-elliott-8713109.mp4",
    )
    val videos = videoUris.map { uri ->
        createRedactionVideoBlock(engine, uri)
    }

    videos.forEachIndexed { index, video ->
        engine.block.setPositionX(video, value = 0F)
        engine.block.setPositionY(video, value = 0F)
        engine.block.setDuration(video, duration = SEGMENT_DURATION)
        engine.block.setTimeOffset(video, offset = index * SEGMENT_DURATION)
        engine.block.appendChild(parent = page, child = video)
    }

    val radialVideo = videos[0]
    val fullBlurVideo = videos[1]
    val pixelVideo = videos[2]
    val partialBlurVideo = videos[3]
    val timedVideo = videos[4]

    // highlight-android-full-block-blur
    if (engine.block.supportsBlur(fullBlurVideo)) {
        val uniformBlur = engine.block.createBlur(type = BlurType.Uniform)
        engine.block.setFloat(
            block = uniformBlur,
            property = "blur/uniform/intensity",
            value = 0.7F,
        )
        engine.block.setBlur(block = fullBlurVideo, blurBlock = uniformBlur)
        engine.block.setBlurEnabled(block = fullBlurVideo, enabled = true)
    }
    // highlight-android-full-block-blur

    var partialBlurEnabled = false

    // highlight-android-partial-blur
    val partialBlurRedaction = engine.block.duplicate(block = partialBlurVideo)
    if (
        engine.block.supportsBlur(partialBlurRedaction) &&
        engine.block.supportsCrop(partialBlurRedaction)
    ) {
        val redactionX = PAGE_WIDTH * 0.22F
        val redactionY = PAGE_HEIGHT * 0.18F
        val redactionWidth = PAGE_WIDTH * 0.35F
        val redactionHeight = PAGE_HEIGHT * 0.28F

        val cropBlur = engine.block.createBlur(type = BlurType.Uniform)
        engine.block.setFloat(
            block = cropBlur,
            property = "blur/uniform/intensity",
            value = 0.75F,
        )
        engine.block.setBlur(block = partialBlurRedaction, blurBlock = cropBlur)
        engine.block.setBlurEnabled(block = partialBlurRedaction, enabled = true)

        engine.block.setWidth(block = partialBlurRedaction, value = redactionWidth)
        engine.block.setHeight(block = partialBlurRedaction, value = redactionHeight)
        engine.block.setPositionX(block = partialBlurRedaction, value = redactionX)
        engine.block.setPositionY(block = partialBlurRedaction, value = redactionY)

        // The smaller frame bounds the redaction; crop keeps its source pixels aligned.
        engine.block.setCropScaleX(block = partialBlurRedaction, scaleX = PAGE_WIDTH / redactionWidth)
        engine.block.setCropScaleY(block = partialBlurRedaction, scaleY = PAGE_HEIGHT / redactionHeight)
        engine.block.setCropTranslationX(
            block = partialBlurRedaction,
            translationX = -redactionX / redactionWidth,
        )
        engine.block.setCropTranslationY(
            block = partialBlurRedaction,
            translationY = -redactionY / redactionHeight,
        )
        partialBlurEnabled = engine.block.isBlurEnabled(partialBlurRedaction)
    }
    // highlight-android-partial-blur

    var pixelizationEnabled = false

    // highlight-android-pixelization
    if (engine.block.supportsEffects(pixelVideo)) {
        val pixelizeEffect = engine.block.createEffect(type = EffectType.Pixelize)
        engine.block.setInt(
            block = pixelizeEffect,
            property = "effect/pixelize/horizontalPixelSize",
            value = 24,
        )
        engine.block.setInt(
            block = pixelizeEffect,
            property = "effect/pixelize/verticalPixelSize",
            value = 24,
        )
        engine.block.appendEffect(block = pixelVideo, effectBlock = pixelizeEffect)
        engine.block.setEffectEnabled(effectBlock = pixelizeEffect, enabled = true)
        pixelizationEnabled = engine.block.isEffectEnabled(pixelizeEffect)
    }
    // highlight-android-pixelization

    // highlight-android-solid-overlay
    val overlay = engine.block.create(DesignBlockType.Graphic)
    val rectShape = engine.block.createShape(type = ShapeType.Rect)
    engine.block.setShape(block = overlay, shape = rectShape)

    val solidFill = engine.block.createFill(fillType = FillType.Color)
    engine.block.setColor(
        block = solidFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.1F, g = 0.1F, b = 0.1F, a = 1.0F),
    )
    engine.block.setFill(block = overlay, fill = solidFill)

    engine.block.setWidth(overlay, value = PAGE_WIDTH * 0.4F)
    engine.block.setHeight(overlay, value = PAGE_HEIGHT * 0.3F)
    engine.block.setPositionX(overlay, value = PAGE_WIDTH * 0.55F)
    engine.block.setPositionY(overlay, value = PAGE_HEIGHT * 0.65F)
    engine.block.appendChild(parent = page, child = overlay)
    // highlight-android-solid-overlay
    engine.block.setTimeOffset(overlay, offset = 3 * SEGMENT_DURATION)
    engine.block.setDuration(overlay, duration = SEGMENT_DURATION)

    // highlight-android-time-based-redaction
    val timedRedaction = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(
        block = timedRedaction,
        shape = engine.block.createShape(type = ShapeType.Rect),
    )

    val timedFill = engine.block.createFill(fillType = FillType.Color)
    engine.block.setColor(
        block = timedFill,
        property = "fill/color/value",
        value = Color.fromRGBA(r = 0.05F, g = 0.05F, b = 0.05F, a = 1.0F),
    )
    engine.block.setFill(block = timedRedaction, fill = timedFill)

    engine.block.setWidth(timedRedaction, value = PAGE_WIDTH * 0.35F)
    engine.block.setHeight(timedRedaction, value = PAGE_HEIGHT * 0.22F)
    engine.block.setPositionX(timedRedaction, value = PAGE_WIDTH * 0.32F)
    engine.block.setPositionY(timedRedaction, value = PAGE_HEIGHT * 0.24F)
    engine.block.setTimeOffset(timedRedaction, offset = 4 * SEGMENT_DURATION)
    engine.block.setDuration(timedRedaction, duration = SEGMENT_DURATION)
    engine.block.appendChild(parent = page, child = timedRedaction)
    // highlight-android-time-based-redaction

    // highlight-android-radial-blur
    if (engine.block.supportsBlur(radialVideo)) {
        val radialBlur = engine.block.createBlur(type = BlurType.Radial)
        // Radial blur leaves the radius clear; use it to protect content outside that focus area.
        engine.block.setFloat(radialBlur, property = "blur/radial/blurRadius", value = 50F)
        engine.block.setFloat(radialBlur, property = "blur/radial/radius", value = 75F)
        engine.block.setFloat(radialBlur, property = "blur/radial/gradientRadius", value = 80F)
        engine.block.setFloat(radialBlur, property = "blur/radial/x", value = 0.5F)
        engine.block.setFloat(radialBlur, property = "blur/radial/y", value = 0.45F)
        engine.block.setBlur(block = radialVideo, blurBlock = radialBlur)
        engine.block.setBlurEnabled(block = radialVideo, enabled = true)
    }
    // highlight-android-radial-blur

    Redaction(
        pageDuration = engine.block.getDuration(page),
        fullBlockBlurEnabled = engine.block.isBlurEnabled(fullBlurVideo),
        partialBlurEnabled = partialBlurEnabled,
        partialRedactionX = engine.block.getPositionX(partialBlurRedaction),
        partialRedactionY = engine.block.getPositionY(partialBlurRedaction),
        partialRedactionWidth = engine.block.getWidth(partialBlurRedaction),
        partialRedactionHeight = engine.block.getHeight(partialBlurRedaction),
        partialCropScaleX = engine.block.getCropScaleX(partialBlurRedaction),
        partialCropScaleY = engine.block.getCropScaleY(partialBlurRedaction),
        partialCropTranslationX = engine.block.getCropTranslationX(partialBlurRedaction),
        partialCropTranslationY = engine.block.getCropTranslationY(partialBlurRedaction),
        pixelizationEnabled = pixelizationEnabled,
        solidOverlayDuration = engine.block.getDuration(overlay),
        timedSourceOffset = engine.block.getTimeOffset(timedVideo),
        timedSourceDuration = engine.block.getDuration(timedVideo),
        timedRedactionOffset = engine.block.getTimeOffset(timedRedaction),
        timedRedactionDuration = engine.block.getDuration(timedRedaction),
        radialBlurEnabled = engine.block.isBlurEnabled(radialVideo),
    )
}

private fun createRedactionVideoBlock(
    engine: Engine,
    uri: String,
): DesignBlock {
    val video = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(video, shape = engine.block.createShape(ShapeType.Rect))
    val videoFill = engine.block.createFill(FillType.Video)
    engine.block.setUri(
        block = videoFill,
        property = "fill/video/fileURI",
        value = Uri.parse(uri),
    )
    engine.block.setFill(video, fill = videoFill)
    engine.block.setWidth(video, value = PAGE_WIDTH)
    engine.block.setHeight(video, value = PAGE_HEIGHT)
    return video
}
