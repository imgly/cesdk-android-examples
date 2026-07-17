import android.net.Uri
import ly.img.engine.Color
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode

suspend fun replaceContent(engine: Engine): ReplaceContentResult {
    val previousVariables = engine.variable.findAll().associateWith { key ->
        engine.variable.get(key)
    }

    return try {
        previousVariables.keys.forEach(engine.variable::remove)

        val scene = engine.scene.create()
        val page = engine.block.create(DesignBlockType.Page)
        engine.block.setWidth(page, value = 1080F)
        engine.block.setHeight(page, value = 720F)
        engine.block.appendChild(parent = scene, child = page)

        val backgroundFill = engine.block.createFill(FillType.Color)
        engine.block.setFill(block = page, fill = backgroundFill)
        engine.block.setFillSolidColor(
            block = page,
            color = Color.fromHex("#FFF5EDF8"),
        )

        val headline = engine.block.create(DesignBlockType.Text)
        engine.block.setName(headline, name = "campaign-headline")
        engine.block.replaceText(headline, text = "Hello, {{first_name}}")
        engine.block.setPositionX(headline, value = 64F)
        engine.block.setPositionY(headline, value = 64F)
        engine.block.setWidth(headline, value = 540F)
        engine.block.setHeightMode(headline, mode = SizeMode.AUTO)
        engine.block.setTextFontSize(headline, fontSize = 24F)
        engine.block.setTextColor(headline, color = Color.fromHex("#FF1E1B2E"))
        engine.block.appendChild(parent = page, child = headline)

        val subtitle = engine.block.create(DesignBlockType.Text)
        engine.block.setName(subtitle, name = "campaign-subtitle")
        engine.block.replaceText(subtitle, text = "Launching in {{city}}")
        engine.block.setPositionX(subtitle, value = 64F)
        engine.block.setPositionY(subtitle, value = 260F)
        engine.block.setWidth(subtitle, value = 540F)
        engine.block.setHeightMode(subtitle, mode = SizeMode.AUTO)
        engine.block.setTextFontSize(subtitle, fontSize = 18F)
        engine.block.setTextColor(subtitle, color = Color.fromHex("#FF4B445E"))
        engine.block.appendChild(parent = page, child = subtitle)

        val imagePlaceholder = engine.block.create(DesignBlockType.Graphic)
        engine.block.setName(imagePlaceholder, name = "campaign-image")
        engine.block.setShape(imagePlaceholder, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setPositionX(imagePlaceholder, value = 620F)
        engine.block.setPositionY(imagePlaceholder, value = 64F)
        engine.block.setWidth(imagePlaceholder, value = 396F)
        engine.block.setHeight(imagePlaceholder, value = 560F)
        engine.block.setContentFillMode(imagePlaceholder, mode = ContentFillMode.COVER)

        val imageFill = engine.block.createFill(FillType.Image)
        engine.block.setUri(
            block = imageFill,
            property = "fill/image/imageFileURI",
            value = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg"),
        )
        engine.block.setFill(block = imagePlaceholder, fill = imageFill)
        if (engine.block.supportsPlaceholderBehavior(imageFill)) {
            engine.block.setPlaceholderBehaviorEnabled(imageFill, enabled = true)
        }
        engine.block.setPlaceholderEnabled(imagePlaceholder, enabled = true)
        engine.block.appendChild(parent = page, child = imagePlaceholder)

        val templateSceneString = engine.scene.saveToString(scene = scene)

        engine.scene.load(scene = templateSceneString)

        // highlight-android-find-by-name
        val namedPlaceholder = engine.block.findByName(name = "campaign-image").first()
        val namedPlaceholderName = engine.block.getName(namedPlaceholder)
        // highlight-android-find-by-name

        // highlight-android-find-all-placeholders
        val placeholderNames = engine.block.findAllPlaceholders()
            .map { placeholder -> engine.block.getName(placeholder) }
        // highlight-android-find-all-placeholders

        // highlight-android-query-placeholder-state
        val placeholderFill = engine.block.getFill(namedPlaceholder)
        val imagePlaceholderSupportsBehavior =
            engine.block.supportsPlaceholderBehavior(placeholderFill)
        val imagePlaceholderEnabled = engine.block.isPlaceholderEnabled(namedPlaceholder)
        // highlight-android-query-placeholder-state

        // highlight-android-text-variables
        engine.variable.set(key = "first_name", value = "Alex")
        engine.variable.set(key = "city", value = "Berlin")

        val firstName = engine.variable.get(key = "first_name")
        // highlight-android-text-variables

        // highlight-android-manage-variables
        engine.variable.set(key = "campaign_tag", value = "summer-2026")
        val variableNames = engine.variable.findAll()
        val campaignTag = engine.variable.get(key = "campaign_tag")
        engine.variable.remove(key = "campaign_tag")
        val campaignTagRemoved = "campaign_tag" !in engine.variable.findAll()
        // highlight-android-manage-variables

        check(campaignTag == "summer-2026")

        // highlight-android-replace-image
        val replacementImageUri = Uri.parse("https://img.ly/static/ubq_samples/sample_2.jpg")
        val replacementFill = engine.block.getFill(namedPlaceholder)
        engine.block.setUri(
            block = replacementFill,
            property = "fill/image/imageFileURI",
            value = replacementImageUri,
        )
        engine.block.resetCrop(namedPlaceholder)
        // highlight-android-replace-image

        val replacedImageUri = engine.block.getUri(
            block = replacementFill,
            property = "fill/image/imageFileURI",
        )

        // highlight-android-direct-text-replacement
        val subtitleBlock = engine.block.findByName(name = "campaign-subtitle").first()
        engine.block.replaceText(subtitleBlock, text = "Built for Android audiences")
        // highlight-android-direct-text-replacement

        val directSubtitleText = engine.block.getString(subtitleBlock, property = "text/text")

        // highlight-android-data-driven
        val records = listOf(
            mapOf(
                "first_name" to "Alex",
                "city" to "Berlin",
                "image_uri" to "https://img.ly/static/ubq_samples/sample_2.jpg",
            ),
            mapOf(
                "first_name" to "Jordan",
                "city" to "Tokyo",
                "image_uri" to "https://img.ly/static/ubq_samples/sample_3.jpg",
            ),
        )

        val generatedDesigns = mutableListOf<GeneratedTemplateDesign>()
        for (record in records) {
            engine.scene.load(scene = templateSceneString)

            engine.variable.set(key = "first_name", value = record.getValue("first_name"))
            engine.variable.set(key = "city", value = record.getValue("city"))

            val imageBlock = engine.block.findByName(name = "campaign-image").first()
            val fill = engine.block.getFill(imageBlock)
            engine.block.setUri(
                block = fill,
                property = "fill/image/imageFileURI",
                value = Uri.parse(record.getValue("image_uri")),
            )
            engine.block.resetCrop(imageBlock)

            val exportPage = engine.scene.getPages().first()
            // Preload resources so remote image fills and text glyphs are ready before export.
            engine.block.forceLoadResources(blocks = listOf(exportPage))
            val pngBuffer = engine.block.export(exportPage, mimeType = MimeType.PNG)
            generatedDesigns += GeneratedTemplateDesign(
                label = record.getValue("first_name"),
                pngBuffer = pngBuffer,
            )
        }
        // highlight-android-data-driven

        ReplaceContentResult(
            namedPlaceholder = namedPlaceholderName,
            placeholderNames = placeholderNames,
            imagePlaceholderSupportsBehavior = imagePlaceholderSupportsBehavior,
            imagePlaceholderEnabled = imagePlaceholderEnabled,
            firstNameVariable = firstName,
            variableNamesAfterSet = variableNames,
            campaignTagRemoved = campaignTagRemoved,
            replacedImageUri = replacedImageUri,
            directSubtitleText = directSubtitleText,
            generatedDesigns = generatedDesigns,
        )
    } finally {
        engine.variable.findAll().forEach { key ->
            runCatching { engine.variable.remove(key) }
        }
        previousVariables.forEach(engine.variable::set)
    }
}
