import android.app.Application
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

suspend fun exportDesignToBase64(
    application: Application,
    license: String?,
    userId: String,
): List<String> = withContext(Dispatchers.Main) {
    Engine.init(application)
    val engine = Engine.getInstance(id = "ly.img.engine.to-base64-guide")

    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1080)

    try {
        val scene = engine.scene.create()
        val page = createSamplePage(engine)
        engine.block.appendChild(parent = scene, child = page)

        // highlight-android-export-base64
        val mimeType = MimeType.PNG
        val buffer = engine.block.export(block = page, mimeType = mimeType)
        val base64 = buffer.toBase64()
        val dataUri = "data:${mimeType.key};base64,$base64"
        // highlight-android-export-base64

        // highlight-android-data-uri
        val inlineImageSource = "data:${mimeType.key};base64,$base64"
        // Use inlineImageSource wherever your app expects a URI string,
        // for example in a WebView, JSON payload, or HTML email template.
        // highlight-android-data-uri

        // highlight-android-mime-types
        val jpegOptions = ExportOptions(jpegQuality = 0.7F, targetWidth = 720F)
        val jpegBuffer = engine.block.export(
            block = page,
            mimeType = MimeType.JPEG,
            options = jpegOptions,
        )
        val jpegDataUri = jpegBuffer.toDataUri(MimeType.JPEG)

        val compressedPngOptions = ExportOptions(pngCompressionLevel = 9, targetWidth = 720F)
        val compressedPngBuffer = engine.block.export(
            block = page,
            mimeType = MimeType.PNG,
            options = compressedPngOptions,
        )
        val compressedPngDataUri = compressedPngBuffer.toDataUri(MimeType.PNG)
        // highlight-android-mime-types

        val secondPage = createSamplePage(engine)
        engine.block.appendChild(parent = scene, child = secondPage)

        // highlight-android-batch
        val pages = engine.scene.getPages()
        val pageBuffers = engine.block.export(blocks = pages, mimeType = MimeType.PNG)
        val pageDataUris = pageBuffers.map { pageBuffer ->
            pageBuffer.toDataUri(MimeType.PNG)
        }
        // highlight-android-batch

        check(inlineImageSource.startsWith("data:image/png;base64,"))
        check(jpegDataUri.startsWith("data:image/jpeg;base64,"))
        check(compressedPngDataUri.startsWith("data:image/png;base64,"))
        check(pageDataUris.size == pages.size)
        check(pageDataUris.all { it.startsWith("data:image/png;base64,") })

        listOf(dataUri, jpegDataUri, compressedPngDataUri) + pageDataUris
    } finally {
        engine.stop()
    }
}

// highlight-android-convert-buffer
private fun ByteBuffer.toBase64(): String {
    val copy = asReadOnlyBuffer()
    copy.rewind()
    val bytes = ByteArray(copy.remaining())
    copy.get(bytes)
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}

private fun ByteBuffer.toDataUri(mimeType: MimeType): String = "data:${mimeType.key};base64,${toBase64()}"
// highlight-android-convert-buffer

private fun createSamplePage(engine: Engine): DesignBlock {
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 1080F)
    engine.block.setHeight(page, value = 1080F)

    val background = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(background, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(background, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(background, color = Color.fromHex("#F4F0EA"))
    engine.block.appendChild(parent = page, child = background)
    engine.block.fillParent(background)

    val text = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(text, text = "Base64 export")
    engine.block.setPositionX(text, value = 96F)
    engine.block.setPositionY(text, value = 456F)
    engine.block.setWidth(text, value = 888F)
    engine.block.setTextFontSize(text, fontSize = 96F)
    engine.block.setTextColor(text, color = Color.fromHex("#23201D"))
    engine.block.appendChild(parent = page, child = text)

    return page
}
