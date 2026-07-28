import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.Font
import ly.img.engine.FontStyle
import ly.img.engine.FontWeight
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import ly.img.engine.Typeface
import java.io.File
import java.util.UUID

suspend fun exportProgrammaticComposition(engine: Engine): File = withContext(engine.dispatcher) {
    buildProgrammaticComposition(engine)
}

private suspend fun buildProgrammaticComposition(engine: Engine): File {
    // highlight-android-font-setup
    val robotoBase = "https://cdn.img.ly/assets/v3/ly.img.typeface/fonts/Roboto"
    val robotoTypeface = Typeface(
        name = "Roboto",
        fonts = listOf(
            Font(
                uri = Uri.parse("$robotoBase/Roboto-Regular.ttf"),
                subFamily = "Regular",
                weight = FontWeight.NORMAL,
                style = FontStyle.NORMAL,
            ),
            Font(
                uri = Uri.parse("$robotoBase/Roboto-Bold.ttf"),
                subFamily = "Bold",
                weight = FontWeight.BOLD,
                style = FontStyle.NORMAL,
            ),
            Font(
                uri = Uri.parse("$robotoBase/Roboto-Italic.ttf"),
                subFamily = "Italic",
                weight = FontWeight.NORMAL,
                style = FontStyle.ITALIC,
            ),
            Font(
                uri = Uri.parse("$robotoBase/Roboto-BoldItalic.ttf"),
                subFamily = "Bold Italic",
                weight = FontWeight.BOLD,
                style = FontStyle.ITALIC,
            ),
        ),
    )
    val robotoRegular = robotoTypeface.fonts.first {
        it.weight == FontWeight.NORMAL && it.style == FontStyle.NORMAL
    }
    // highlight-android-font-setup

    // highlight-android-create-scene
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 1080F)
    engine.block.setHeight(page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-android-create-scene

    // highlight-android-add-background
    val backgroundFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(block = page, fill = backgroundFill)
    engine.block.setFillSolidColor(
        block = page,
        color = Color.fromRGBA(r = 0.94F, g = 0.93F, b = 0.98F, a = 1F),
    )
    // highlight-android-add-background

    // highlight-android-text-create
    val headline = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(headline, text = "Integrate\nCreative Editing\ninto your App")
    engine.block.setFont(headline, fontFileUri = robotoRegular.uri, typeface = robotoTypeface)
    engine.block.setTextLineHeight(headline, lineHeight = 0.78F)
    // highlight-android-text-create

    // highlight-android-text-style-block
    if (engine.block.canToggleBoldFont(headline)) {
        engine.block.toggleBoldFont(headline)
    }
    engine.block.setTextColor(headline, color = Color.fromRGBA(r = 0F, g = 0F, b = 0F, a = 1F))
    // highlight-android-text-style-block

    // highlight-android-text-auto-size
    engine.block.setWidthMode(headline, mode = SizeMode.ABSOLUTE)
    engine.block.setHeightMode(headline, mode = SizeMode.ABSOLUTE)
    engine.block.setWidth(headline, value = 960F)
    engine.block.setHeight(headline, value = 300F)
    // The Android binding has no typed helper for this text option yet.
    engine.block.setBoolean(headline, property = "text/automaticFontSizeEnabled", value = true)
    // highlight-android-text-auto-size

    engine.block.setPositionX(headline, value = 60F)
    engine.block.setPositionY(headline, value = 80F)
    engine.block.appendChild(parent = page, child = headline)

    val tagline = engine.block.create(DesignBlockType.Text)
    val taglineText = "in hours,\nnot months."
    engine.block.replaceText(tagline, text = taglineText)
    engine.block.setFont(tagline, fontFileUri = robotoRegular.uri, typeface = robotoTypeface)
    engine.block.setTextLineHeight(tagline, lineHeight = 0.78F)

    // highlight-android-text-range-style
    engine.block.setTextColor(
        tagline,
        color = Color.fromRGBA(r = 0.2F, g = 0.2F, b = 0.8F, a = 1F),
        from = 0,
        to = 9,
    )
    if (engine.block.canToggleItalicFont(tagline, from = 0, to = 9)) {
        engine.block.toggleItalicFont(tagline, from = 0, to = 9)
    }

    engine.block.setTextColor(
        tagline,
        color = Color.fromRGBA(r = 0F, g = 0F, b = 0F, a = 1F),
        from = 10,
        to = 21,
    )
    if (engine.block.canToggleBoldFont(tagline, from = 10, to = 21)) {
        engine.block.toggleBoldFont(tagline, from = 10, to = 21)
    }
    // highlight-android-text-range-style

    engine.block.setWidthMode(tagline, mode = SizeMode.ABSOLUTE)
    engine.block.setHeightMode(tagline, mode = SizeMode.ABSOLUTE)
    engine.block.setWidth(tagline, value = 960F)
    engine.block.setHeight(tagline, value = 220F)
    // The Android binding has no typed helper for this text option yet.
    engine.block.setBoolean(tagline, property = "text/automaticFontSizeEnabled", value = true)
    engine.block.setPositionX(tagline, value = 60F)
    engine.block.setPositionY(tagline, value = 551F)
    engine.block.appendChild(parent = page, child = tagline)

    // highlight-android-text-fixed-size
    val ctaTitle = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(ctaTitle, text = "Start a Free Trial")
    engine.block.setFont(ctaTitle, fontFileUri = robotoRegular.uri, typeface = robotoTypeface)
    engine.block.setTextFontSize(ctaTitle, fontSize = 80F)
    engine.block.setTextLineHeight(ctaTitle, lineHeight = 1F)
    // highlight-android-text-fixed-size

    if (engine.block.canToggleBoldFont(ctaTitle)) {
        engine.block.toggleBoldFont(ctaTitle)
    }
    engine.block.setTextColor(ctaTitle, color = Color.fromRGBA(r = 0F, g = 0F, b = 0F, a = 1F))
    engine.block.setWidthMode(ctaTitle, mode = SizeMode.ABSOLUTE)
    engine.block.setHeightMode(ctaTitle, mode = SizeMode.AUTO)
    engine.block.setWidth(ctaTitle, value = 664.6F)
    engine.block.setPositionX(ctaTitle, value = 64F)
    engine.block.setPositionY(ctaTitle, value = 952F)
    engine.block.appendChild(parent = page, child = ctaTitle)

    val ctaUrl = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(ctaUrl, text = "www.img.ly")
    engine.block.setFont(ctaUrl, fontFileUri = robotoRegular.uri, typeface = robotoTypeface)
    engine.block.setTextFontSize(ctaUrl, fontSize = 80F)
    engine.block.setTextLineHeight(ctaUrl, lineHeight = 1F)
    engine.block.setTextColor(ctaUrl, color = Color.fromRGBA(r = 0F, g = 0F, b = 0F, a = 1F))
    engine.block.setWidthMode(ctaUrl, mode = SizeMode.ABSOLUTE)
    engine.block.setHeightMode(ctaUrl, mode = SizeMode.AUTO)
    engine.block.setWidth(ctaUrl, value = 664.6F)
    engine.block.setPositionX(ctaUrl, value = 64F)
    engine.block.setPositionY(ctaUrl, value = 1006F)
    engine.block.appendChild(parent = page, child = ctaUrl)

    // highlight-android-shape-create
    val dividerLine = engine.block.create(DesignBlockType.Graphic)
    val lineShape = engine.block.createShape(ShapeType.Line)
    engine.block.setShape(block = dividerLine, shape = lineShape)
    // highlight-android-shape-create

    // highlight-android-shape-fill
    val lineFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(block = dividerLine, fill = lineFill)
    engine.block.setFillSolidColor(
        block = dividerLine,
        color = Color.fromRGBA(r = 0F, g = 0F, b = 0F, a = 1F),
    )
    // highlight-android-shape-fill

    engine.block.setWidth(dividerLine, value = 418F)
    // Line shapes use block height as the visible stroke thickness.
    engine.block.setHeight(dividerLine, value = 11.3F)
    engine.block.setPositionX(dividerLine, value = 64F)
    engine.block.setPositionY(dividerLine, value = 460F)
    engine.block.appendChild(parent = page, child = dividerLine)

    // highlight-android-image-create
    val logo = engine.block.create(DesignBlockType.Graphic)
    val logoShape = engine.block.createShape(ShapeType.Rect)
    engine.block.setShape(block = logo, shape = logoShape)

    val logoFill = engine.block.createFill(FillType.Image)
    // Image fills currently expose their URI through the generic property API.
    engine.block.setUri(
        block = logoFill,
        property = "fill/image/imageFileURI",
        value = Uri.parse("https://img.ly/static/ubq_samples/imgly_logo.jpg"),
    )
    engine.block.setFill(block = logo, fill = logoFill)
    // highlight-android-image-create

    // highlight-android-block-position
    engine.block.setContentFillMode(logo, mode = ContentFillMode.CONTAIN)
    engine.block.setWidth(logo, value = 200F)
    engine.block.setHeight(logo, value = 65F)
    engine.block.setPositionX(logo, value = 820F)
    engine.block.setPositionY(logo, value = 960F)
    engine.block.appendChild(parent = page, child = logo)
    // highlight-android-block-position

    // highlight-android-export-api
    val exportOptions = ExportOptions(targetWidth = 1080F, targetHeight = 1080F)
    // Ensure remote font files and the image fill are ready before the offscreen export.
    engine.block.forceLoadResources(listOf(page, headline, tagline, ctaTitle, ctaUrl, logo))
    val blob = engine.block.export(page, mimeType = MimeType.PNG, options = exportOptions)
    // highlight-android-export-api

    // highlight-android-export-file
    return withContext(Dispatchers.IO) {
        val outputFile = File.createTempFile("composition-${UUID.randomUUID()}", ".png")
        val data = blob.asReadOnlyBuffer()
        outputFile.outputStream().channel.use { channel ->
            while (data.hasRemaining()) {
                channel.write(data)
            }
        }
        outputFile
    }
    // highlight-android-export-file
}
