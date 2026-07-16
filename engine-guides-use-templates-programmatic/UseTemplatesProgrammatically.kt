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
import ly.img.engine.assetBaseUri
import java.nio.ByteBuffer

@Suppress("DEPRECATION")
suspend fun useTemplatesProgrammatically(
    engine: Engine,
    templateBaseUri: Uri = Engine.assetBaseUri,
): UseTemplatesProgrammaticallyResult {
    val previousVariables = engine.variable.findAll().associateWith { key ->
        engine.variable.get(key)
    }

    return try {
        previousVariables.keys.forEach(engine.variable::remove)

        // highlight-android-create-template
        val scene = engine.scene.create(designUnit = DesignUnit.PIXEL, fontSizeUnit = FontUnit.PIXEL)
        val page = engine.block.create(DesignBlockType.Page)
        engine.block.setWidth(page, value = 800F)
        engine.block.setHeight(page, value = 600F)
        engine.block.appendChild(parent = scene, child = page)

        val pageFill = engine.block.createFill(FillType.Color)
        engine.block.setFill(block = page, fill = pageFill)
        engine.block.setFillSolidColor(
            block = page,
            color = Color.fromRGBA(r = 0.95F, g = 0.95F, b = 0.95F, a = 1F),
        )

        engine.variable.set(key = "recipientName", value = "Template")
        engine.variable.set(key = "customMessage", value = "This is a template example.")

        val titleBlock = engine.block.create(DesignBlockType.Text)
        engine.block.setName(titleBlock, name = "title")
        engine.block.replaceText(titleBlock, text = "Hello, {{recipientName}}!")
        engine.block.setTextFontSize(titleBlock, fontSize = 48F)
        engine.block.setTextColor(titleBlock, color = Color.fromHex("#333333"))
        engine.block.setPositionX(titleBlock, value = 50F)
        engine.block.setPositionY(titleBlock, value = 50F)
        engine.block.setWidth(titleBlock, value = 700F)
        engine.block.setHeight(titleBlock, value = 80F)
        engine.block.appendChild(parent = page, child = titleBlock)

        val messageBlock = engine.block.create(DesignBlockType.Text)
        engine.block.setName(messageBlock, name = "message")
        engine.block.replaceText(messageBlock, text = "{{customMessage}}")
        engine.block.setTextFontSize(messageBlock, fontSize = 28F)
        engine.block.setTextColor(messageBlock, color = Color.fromHex("#4D4D4D"))
        engine.block.setPositionX(messageBlock, value = 50F)
        engine.block.setPositionY(messageBlock, value = 140F)
        engine.block.setWidth(messageBlock, value = 700F)
        engine.block.setHeight(messageBlock, value = 120F)
        engine.block.appendChild(parent = page, child = messageBlock)
        // highlight-android-create-template

        // highlight-android-configure-placeholder
        val imageBlock = engine.block.create(DesignBlockType.Graphic)
        engine.block.setName(imageBlock, name = "hero-image")
        engine.block.setShape(imageBlock, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setPositionX(imageBlock, value = 50F)
        engine.block.setPositionY(imageBlock, value = 300F)
        engine.block.setWidth(imageBlock, value = 700F)
        engine.block.setHeight(imageBlock, value = 220F)
        engine.block.setContentFillMode(imageBlock, mode = ContentFillMode.COVER)

        val imageFill = engine.block.createFill(FillType.Image)
        val imageUri = templateBaseUri.buildUpon()
            .appendPath("ly.img.image")
            .appendPath("images")
            .appendPath("sample_1.jpg")
            .build()
        engine.block.setString(
            block = imageFill,
            property = "fill/image/imageFileURI",
            value = imageUri.toString(),
        )
        engine.block.setFill(block = imageBlock, fill = imageFill)
        engine.block.appendChild(parent = page, child = imageBlock)

        val placeholderFill = engine.block.getFill(imageBlock)
        if (engine.block.supportsPlaceholderBehavior(placeholderFill)) {
            engine.block.setPlaceholderBehaviorEnabled(block = placeholderFill, enabled = true)
        }
        engine.block.setPlaceholderEnabled(block = imageBlock, enabled = true)
        val placeholderBlocks = engine.block.findAllPlaceholders()
        // highlight-android-configure-placeholder
        val placeholderBehaviorEnabled = engine.block.supportsPlaceholderBehavior(placeholderFill) &&
            engine.block.isPlaceholderBehaviorEnabled(placeholderFill)
        val placeholderEnabled = engine.block.isPlaceholderEnabled(imageBlock)

        // highlight-android-manage-variables
        val variableNames = engine.variable.findAll()
        val currentRecipientName = engine.variable.get(key = "recipientName")
        val titleUsesVariables = engine.block.referencesAnyVariables(titleBlock)
        // highlight-android-manage-variables

        // highlight-android-populate-media
        val mediaPlaceholder = engine.block.findByName(name = "hero-image").first()
        val mediaFill = runCatching { engine.block.getFill(mediaPlaceholder) }
            .getOrNull()
            ?.takeIf { fill -> engine.block.getType(fill) == FillType.Image.key }
            ?: engine.block.createFill(FillType.Image).also { fill ->
                engine.block.setFill(block = mediaPlaceholder, fill = fill)
            }
        val replacementImageUri = templateBaseUri.buildUpon()
            .appendPath("ly.img.image")
            .appendPath("images")
            .appendPath("sample_2.jpg")
            .build()
        engine.block.setString(
            block = mediaFill,
            property = "fill/image/imageFileURI",
            value = replacementImageUri.toString(),
        )
        // highlight-android-populate-media

        // highlight-android-save-template
        engine.block.forceLoadResources(blocks = listOf(page))
        val templateString = engine.scene.saveToString(scene = scene)
        val templateArchive = engine.scene.saveToArchive(scene = scene)
        // highlight-android-save-template

        val personalizedCards = mutableListOf<ByteBuffer>()

        // highlight-android-batch-processing
        val recipients = listOf(
            "Avery" to "Congratulations on the launch.",
            "Jordan" to "Thanks for your work this quarter.",
        )

        for ((recipientName, customMessage) in recipients) {
            engine.variable.set(key = "recipientName", value = recipientName)
            engine.variable.set(key = "customMessage", value = customMessage)

            val cardData = engine.block.export(
                block = page,
                mimeType = MimeType.PNG,
                options = ExportOptions(targetWidth = 800F, targetHeight = 600F),
            )
            personalizedCards += cardData
        }
        // highlight-android-batch-processing

        val resetExports = mutableListOf<ByteBuffer>()

        // highlight-android-data-driven
        val records = listOf(
            "Riley" to "Welcome to the team.",
            "Morgan" to "Great work on the campaign.",
        )

        for ((recipientName, customMessage) in records) {
            engine.scene.load(scene = templateString, overrideEditorConfig = true, waitForResources = true)
            val recordPage = engine.scene.getPages().first()
            engine.variable.set(key = "recipientName", value = recipientName)
            engine.variable.set(key = "customMessage", value = customMessage)

            val recordData = engine.block.export(block = recordPage, mimeType = MimeType.PNG)
            resetExports += recordData
        }
        // highlight-android-data-driven

        // highlight-android-remove-variable
        engine.variable.remove(key = "customMessage")
        val variablesAfterRemoval = engine.variable.findAll()
        // highlight-android-remove-variable

        // highlight-android-load-existing
        val existingTemplateUri = templateBaseUri.buildUpon()
            .appendPath("ly.img.templates")
            .appendPath("templates")
            .appendPath("cesdk_business_card_1.scene")
            .build()
        engine.scene.load(sceneUri = existingTemplateUri, overrideEditorConfig = true, waitForResources = true)
        val loadedTemplatePageCount = engine.scene.getPages().size
        // highlight-android-load-existing

        UseTemplatesProgrammaticallyResult(
            templateString = templateString,
            templateArchive = templateArchive,
            variableNames = variableNames,
            currentRecipientName = currentRecipientName,
            titleUsesVariables = titleUsesVariables,
            placeholderCount = placeholderBlocks.size,
            placeholderBehaviorEnabled = placeholderBehaviorEnabled,
            placeholderEnabled = placeholderEnabled,
            personalizedCards = personalizedCards,
            resetExports = resetExports,
            variablesAfterRemoval = variablesAfterRemoval,
            loadedTemplatePageCount = loadedTemplatePageCount,
        )
    } finally {
        engine.variable.findAll().forEach { key ->
            runCatching { engine.variable.remove(key) }
        }
        previousVariables.forEach(engine.variable::set)
    }
}
