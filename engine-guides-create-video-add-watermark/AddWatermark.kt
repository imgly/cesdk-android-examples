import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.BlendMode
import ly.img.engine.Color
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.HorizontalAlignment
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode

fun addWatermark(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.add.watermark.example")
    try {
        engine.start(license = license, userId = userId)
        engine.bindOffscreen(width = 1280, height = 720)

        // highlight-android-create-video-scene
        val videoUri = Uri.parse("https://img.ly/static/ubq_video_samples/bbb.mp4")
        engine.scene.createFromVideo(videoUri)

        val page = requireNotNull(engine.scene.getCurrentPage()) {
            "Expected createFromVideo() to create a page."
        }
        val pageWidth = engine.block.getWidth(page)
        val pageHeight = engine.block.getHeight(page)
        val videoDuration = engine.block.getDuration(page)
        // highlight-android-create-video-scene

        // highlight-android-create-text-watermark
        val textWatermark = engine.block.create(DesignBlockType.Text)

        engine.block.setWidthMode(block = textWatermark, mode = SizeMode.AUTO)
        engine.block.setHeightMode(block = textWatermark, mode = SizeMode.AUTO)
        engine.block.replaceText(block = textWatermark, text = "All rights reserved 2025")

        val textPadding = 20F
        engine.block.setPositionX(block = textWatermark, value = textPadding)
        engine.block.setPositionY(block = textWatermark, value = pageHeight - textPadding - 28F)
        // highlight-android-create-text-watermark

        // highlight-android-style-text-watermark
        engine.block.setTextFontSize(block = textWatermark, fontSize = 20F)
        engine.block.setTextColor(block = textWatermark, color = Color.fromRGBA(1F, 1F, 1F, 1F))
        engine.block.setTextHorizontalAlignment(block = textWatermark, alignment = HorizontalAlignment.Left)
        engine.block.setOpacity(block = textWatermark, value = 0.7F)
        // highlight-android-style-text-watermark

        // highlight-android-text-drop-shadow
        engine.block.setDropShadowEnabled(block = textWatermark, enabled = true)
        engine.block.setDropShadowColor(block = textWatermark, color = Color.fromRGBA(0F, 0F, 0F, 0.8F))
        engine.block.setDropShadowOffsetX(block = textWatermark, offsetX = 2F)
        engine.block.setDropShadowOffsetY(block = textWatermark, offsetY = 2F)
        engine.block.setDropShadowBlurRadiusX(block = textWatermark, blurRadiusX = 4F)
        engine.block.setDropShadowBlurRadiusY(block = textWatermark, blurRadiusY = 4F)
        // highlight-android-text-drop-shadow

        // highlight-android-text-timeline
        engine.block.setDuration(block = textWatermark, duration = videoDuration)
        engine.block.setTimeOffset(block = textWatermark, offset = 0.0)
        engine.block.appendChild(parent = page, child = textWatermark)
        // highlight-android-text-timeline

        // highlight-android-create-image-watermark
        val logoWatermark = engine.block.create(DesignBlockType.Graphic)
        val rectShape = engine.block.createShape(ShapeType.Rect)
        engine.block.setShape(block = logoWatermark, shape = rectShape)

        val imageFill = engine.block.createFill(FillType.Image)
        val logoUri = Uri.parse("https://img.ly/static/ubq_samples/imgly_logo.jpg")
        engine.block.setUri(
            block = imageFill,
            property = "fill/image/imageFileURI",
            value = logoUri,
        )
        engine.block.setFill(block = logoWatermark, fill = imageFill)
        engine.block.setContentFillMode(block = logoWatermark, mode = ContentFillMode.CONTAIN)
        // highlight-android-create-image-watermark

        // highlight-android-position-image-watermark
        val logoSize = 80F
        val logoPadding = 20F
        engine.block.setWidth(block = logoWatermark, value = logoSize)
        engine.block.setHeight(block = logoWatermark, value = logoSize)
        engine.block.setPositionX(block = logoWatermark, value = pageWidth - logoSize - logoPadding)
        engine.block.setPositionY(block = logoWatermark, value = logoPadding)
        // highlight-android-position-image-watermark

        // highlight-android-image-opacity-blend
        engine.block.setOpacity(block = logoWatermark, value = 0.6F)
        engine.block.setBlendMode(block = logoWatermark, blendMode = BlendMode.NORMAL)
        // highlight-android-image-opacity-blend

        // highlight-android-image-timeline
        engine.block.setDuration(block = logoWatermark, duration = videoDuration)
        engine.block.setTimeOffset(block = logoWatermark, offset = 0.0)
        engine.block.appendChild(parent = page, child = logoWatermark)
        // highlight-android-image-timeline

        check(videoDuration > 0.0) { "Expected the page duration to match the source video." }
        check(engine.block.getDuration(textWatermark) == videoDuration)
        check(engine.block.getDuration(logoWatermark) == videoDuration)
    } finally {
        engine.stop()
    }
}
