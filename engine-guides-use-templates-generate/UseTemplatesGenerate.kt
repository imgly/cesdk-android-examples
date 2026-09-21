import android.net.Uri
import ly.img.engine.Color
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.DesignUnit
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.FontUnit
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

private val generatedTemplateVariableKeys = listOf("recipientName", "message")

suspend fun useTemplatesGenerate(
    engine: Engine,
    assetBaseUri: Uri,
): UseTemplatesGenerateResult {
    val existingVariableKeys = engine.variable.findAll().toSet()
    val previousVariables = generatedTemplateVariableKeys
        .filter(existingVariableKeys::contains)
        .associateWith(engine.variable::get)

    return try {
        generatedTemplateVariableKeys
            .filter(existingVariableKeys::contains)
            .forEach(engine.variable::remove)

        generateFromTemplate(engine = engine, assetBaseUri = assetBaseUri)
    } finally {
        val currentVariableKeys = engine.variable.findAll().toSet()
        generatedTemplateVariableKeys
            .filter(currentVariableKeys::contains)
            .forEach(engine.variable::remove)
        previousVariables.forEach { (key, value) ->
            engine.variable.set(key = key, value = value)
        }
    }
}

private suspend fun generateFromTemplate(
    engine: Engine,
    assetBaseUri: Uri,
): UseTemplatesGenerateResult {
    val templateString = createGenerateTemplate(engine = engine, assetBaseUri = assetBaseUri)

    // highlight-android-generate-load
    engine.scene.load(
        scene = templateString,
        overrideEditorConfig = true,
        waitForResources = true,
    )
    // highlight-android-generate-load

    // highlight-android-generate-discover-variables
    val variableNames = engine.variable.findAll()
    val defaultRecipient = engine.variable.get(key = "recipientName")
    // highlight-android-generate-discover-variables

    // highlight-android-generate-populate-variables
    engine.variable.set(key = "recipientName", value = "Avery")
    engine.variable.set(key = "message", value = "Wishing you a wonderful year ahead!")
    // highlight-android-generate-populate-variables

    // highlight-android-generate-find-placeholders
    val placeholders = engine.block.findAllPlaceholders()
    val imagePlaceholder = engine.block.findByName(name = "Image").first()
    val namedPlaceholder = engine.block.getName(imagePlaceholder)
    // highlight-android-generate-find-placeholders

    // highlight-android-generate-update-image
    val replacementImageUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.image")
        .appendPath("images")
        .appendPath("sample_2.jpg")
        .build()
    val imageFill = engine.block.getFill(imagePlaceholder)
    engine.block.setUri(
        block = imageFill,
        property = "fill/image/imageFileURI",
        value = replacementImageUri,
    )
    engine.block.resetCrop(block = imagePlaceholder)
    // highlight-android-generate-update-image

    // highlight-android-generate-export-image
    val page = engine.scene.getPages().first()
    engine.block.forceLoadResources(blocks = listOf(page))
    val pngData = engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
        options = ExportOptions(targetWidth = 800F, targetHeight = 600F),
    ).asReadOnlyBuffer()
    // highlight-android-generate-export-image

    // highlight-android-generate-export-pdf
    val scene = requireNotNull(engine.scene.get()) { "No scene loaded for export." }
    val pdfData = engine.block.export(
        block = scene,
        mimeType = MimeType.PDF,
    ).asReadOnlyBuffer()
    // highlight-android-generate-export-pdf

    // highlight-android-generate-batch
    val records = listOf(
        "Jordan" to "Congratulations on the new home!",
        "Riley" to "Thank you for everything.",
    )
    val batchExports = mutableListOf<ByteBuffer>()

    for ((recipientName, message) in records) {
        engine.scene.load(
            scene = templateString,
            overrideEditorConfig = true,
            waitForResources = true,
        )
        engine.variable.set(key = "recipientName", value = recipientName)
        engine.variable.set(key = "message", value = message)

        val recordPage = engine.scene.getPages().first()
        engine.block.forceLoadResources(blocks = listOf(recordPage))
        batchExports += engine.block.export(
            block = recordPage,
            mimeType = MimeType.PNG,
        ).asReadOnlyBuffer()
    }
    // highlight-android-generate-batch

    return UseTemplatesGenerateResult(
        variableNames = variableNames,
        defaultRecipient = defaultRecipient,
        placeholderCount = placeholders.size,
        namedPlaceholder = namedPlaceholder,
        replacementImageUri = replacementImageUri,
        pngData = pngData,
        pdfData = pdfData,
        batchExports = batchExports,
    )
}

private suspend fun createGenerateTemplate(
    engine: Engine,
    assetBaseUri: Uri,
): String {
    val scene = engine.scene.create(
        designUnit = DesignUnit.PIXEL,
        fontSizeUnit = FontUnit.PIXEL,
    )
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val pageFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(block = page, fill = pageFill)
    engine.block.setFillSolidColor(
        block = page,
        color = Color.fromHex("#F7F4EE"),
    )

    val imageBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(imageBlock, name = "Image")
    engine.block.setShape(imageBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(imageBlock, value = 50F)
    engine.block.setPositionY(imageBlock, value = 140F)
    engine.block.setWidth(imageBlock, value = 320F)
    engine.block.setHeight(imageBlock, value = 320F)
    engine.block.setContentFillMode(imageBlock, mode = ContentFillMode.COVER)

    val imageFill = engine.block.createFill(FillType.Image)
    val initialImageUri = assetBaseUri.buildUpon()
        .appendPath("ly.img.image")
        .appendPath("images")
        .appendPath("sample_1.jpg")
        .build()
    engine.block.setUri(
        block = imageFill,
        property = "fill/image/imageFileURI",
        value = initialImageUri,
    )
    engine.block.setFill(block = imageBlock, fill = imageFill)
    engine.block.setPlaceholderEnabled(block = imageBlock, enabled = true)
    engine.block.appendChild(parent = page, child = imageBlock)

    val greetingBlock = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(greetingBlock, text = "Dear {{recipientName}},")
    engine.block.setTextFontSize(greetingBlock, fontSize = 44F)
    engine.block.setTextColor(greetingBlock, color = Color.fromHex("#25211D"))
    engine.block.setPositionX(greetingBlock, value = 420F)
    engine.block.setPositionY(greetingBlock, value = 180F)
    engine.block.setWidth(greetingBlock, value = 330F)
    engine.block.setHeight(greetingBlock, value = 90F)
    engine.block.appendChild(parent = page, child = greetingBlock)

    val messageBlock = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(messageBlock, text = "{{message}}")
    engine.block.setTextFontSize(messageBlock, fontSize = 28F)
    engine.block.setTextColor(messageBlock, color = Color.fromHex("#5D534B"))
    engine.block.setPositionX(messageBlock, value = 420F)
    engine.block.setPositionY(messageBlock, value = 280F)
    engine.block.setWidth(messageBlock, value = 320F)
    engine.block.setHeight(messageBlock, value = 160F)
    engine.block.appendChild(parent = page, child = messageBlock)

    engine.variable.set(key = "recipientName", value = "Friend")
    engine.variable.set(key = "message", value = "Best wishes")
    engine.block.forceLoadResources(blocks = listOf(page))

    return engine.scene.saveToString(scene = scene)
}
