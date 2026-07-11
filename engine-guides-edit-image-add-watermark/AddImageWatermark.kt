import android.net.Uri
import ly.img.engine.Color
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.HorizontalAlignment
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import kotlin.math.abs

suspend fun addImageWatermark(engine: Engine): ImageWatermarkResult {
    // highlight-android-setup
    val imageUri = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg")
    engine.scene.createFromImage(imageUri)

    val page = requireNotNull(engine.scene.getCurrentPage()) {
        "Expected createFromImage() to create a page."
    }
    val pageWidth = engine.block.getWidth(page)
    val pageHeight = engine.block.getHeight(page)
    // highlight-android-setup

    // highlight-android-create-text-watermark
    val textWatermark = engine.block.create(DesignBlockType.Text)

    engine.block.setWidthMode(block = textWatermark, mode = SizeMode.AUTO)
    engine.block.setHeightMode(block = textWatermark, mode = SizeMode.AUTO)
    engine.block.replaceText(block = textWatermark, text = "All rights reserved")
    engine.block.appendChild(parent = page, child = textWatermark)
    // highlight-android-create-text-watermark

    // highlight-android-style-text-watermark
    engine.block.setTextFontSize(block = textWatermark, fontSize = 28F)
    engine.block.setTextColor(block = textWatermark, color = Color.fromRGBA(1F, 1F, 1F, 1F))
    engine.block.setTextHorizontalAlignment(block = textWatermark, alignment = HorizontalAlignment.Left)
    engine.block.setOpacity(block = textWatermark, value = 0.7F)
    // highlight-android-style-text-watermark

    // highlight-android-create-logo-watermark
    val logoWatermark = engine.block.create(DesignBlockType.Graphic)
    val rectShape = engine.block.createShape(ShapeType.Rect)
    engine.block.setShape(block = logoWatermark, shape = rectShape)

    val logoFill = engine.block.createFill(FillType.Image)
    val logoUri = Uri.parse("https://img.ly/static/ubq_samples/imgly_logo.jpg")
    engine.block.setUri(
        block = logoFill,
        property = "fill/image/imageFileURI",
        value = logoUri,
    )
    engine.block.setFill(block = logoWatermark, fill = logoFill)
    engine.block.setContentFillMode(block = logoWatermark, mode = ContentFillMode.CONTAIN)
    engine.block.appendChild(parent = page, child = logoWatermark)
    // highlight-android-create-logo-watermark

    // highlight-android-size-logo-watermark
    val logoSize = pageWidth * 0.14F

    engine.block.setWidth(block = logoWatermark, value = logoSize)
    engine.block.setHeight(block = logoWatermark, value = logoSize)
    engine.block.setOpacity(block = logoWatermark, value = 0.62F)
    // highlight-android-size-logo-watermark

    // highlight-android-position-watermarks
    val spacing = 18F
    val bottomPadding = 36F
    val textWidth = engine.block.getFrameWidth(textWatermark)
    val textHeight = engine.block.getFrameHeight(textWatermark)
    val totalWatermarkWidth = logoSize + spacing + textWidth
    val startX = (pageWidth - totalWatermarkWidth) / 2F
    val centerY = pageHeight - bottomPadding - maxOf(logoSize, textHeight) / 2F

    engine.block.setPositionX(block = logoWatermark, value = startX)
    engine.block.setPositionY(block = logoWatermark, value = centerY - logoSize / 2F)
    engine.block.setPositionX(block = textWatermark, value = startX + logoSize + spacing)
    engine.block.setPositionY(block = textWatermark, value = centerY - textHeight / 2F)
    // highlight-android-position-watermarks

    // highlight-android-add-drop-shadow
    listOf(textWatermark, logoWatermark).forEach { watermark ->
        if (engine.block.supportsDropShadow(watermark)) {
            engine.block.setDropShadowEnabled(block = watermark, enabled = true)
            engine.block.setDropShadowColor(block = watermark, color = Color.fromRGBA(0F, 0F, 0F, 0.55F))
            engine.block.setDropShadowOffsetX(block = watermark, offsetX = 3F)
            engine.block.setDropShadowOffsetY(block = watermark, offsetY = 3F)
            engine.block.setDropShadowBlurRadiusX(block = watermark, blurRadiusX = 6F)
            engine.block.setDropShadowBlurRadiusY(block = watermark, blurRadiusY = 6F)
        }
    }
    // highlight-android-add-drop-shadow

    // highlight-android-export-watermarked
    val exportedPng = engine.block.export(block = page, mimeType = MimeType.PNG)
    val exportedJpeg = engine.block.export(
        block = page,
        mimeType = MimeType.JPEG,
        options = ExportOptions(jpegQuality = 0.86F),
    )
    // highlight-android-export-watermarked

    check(exportedPng.hasRemaining()) { "PNG export is empty." }
    check(exportedJpeg.hasRemaining()) { "JPEG export is empty." }

    val textOpacity = engine.block.getOpacity(textWatermark)
    val logoOpacity = engine.block.getOpacity(logoWatermark)
    check(abs(textOpacity - 0.7F) < 0.0001F)
    check(abs(logoOpacity - 0.62F) < 0.0001F)

    return ImageWatermarkResult(
        pageWidth = pageWidth,
        pageHeight = pageHeight,
        textWatermark = textWatermark,
        logoWatermark = logoWatermark,
        textOpacity = textOpacity,
        logoOpacity = logoOpacity,
        textShadowEnabled = engine.block.isDropShadowEnabled(textWatermark),
        logoUri = engine.block.getUri(
            block = logoFill,
            property = "fill/image/imageFileURI",
        ),
        exportedPng = exportedPng.asReadOnlyBuffer(),
        exportedJpeg = exportedJpeg.asReadOnlyBuffer(),
    )
}
