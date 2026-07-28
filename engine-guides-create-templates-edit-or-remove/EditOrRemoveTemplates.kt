import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.AssetDefinition
import ly.img.engine.Color
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import java.io.File
import java.nio.ByteBuffer

suspend fun editOrRemoveTemplates(engine: Engine): TemplateManagementSummary {
    val templateSourceId = "android-guide-templates"
    val templateContentKey = "template/content"
    val storedThumbnailFiles = mutableListOf<File>()

    if (templateSourceId in engine.asset.findAllSources()) {
        engine.asset.removeSource(sourceId = templateSourceId)
    }

    // highlight-android-write-thumbnail-file
    suspend fun writeTemplateThumbnailFile(
        prefix: String,
        pngData: ByteBuffer,
    ): File {
        val thumbnailFile = withContext(Dispatchers.IO) {
            File.createTempFile(prefix, ".png").apply {
                outputStream().use { output ->
                    val thumbnailBuffer = pngData.asReadOnlyBuffer()
                    while (thumbnailBuffer.hasRemaining()) {
                        output.channel.write(thumbnailBuffer)
                    }
                }
            }
        }
        return thumbnailFile
    }
    // highlight-android-write-thumbnail-file

    try {
        // highlight-android-create-source
        val templateMimeType = MimeType.BINARY.key

        engine.asset.addLocalSource(
            sourceId = templateSourceId,
            supportedMimeTypes = listOf(templateMimeType),
            applyAsset = { asset ->
                val templateContent = requireNotNull(asset.meta?.get(templateContentKey))
                engine.scene.applyTemplate(template = templateContent)
                null
            },
        )
        // highlight-android-create-source

        // highlight-android-create-template
        val scene = engine.scene.create()
        val page = engine.block.create(DesignBlockType.Page)
        engine.block.setWidth(page, value = 1200F)
        engine.block.setHeight(page, value = 600F)
        engine.block.appendChild(parent = scene, child = page)

        val backgroundBlock = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(backgroundBlock, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setFill(backgroundBlock, fill = engine.block.createFill(FillType.Color))
        engine.block.setFillSolidColor(backgroundBlock, color = Color.fromHex("#FFF4F0EA"))
        engine.block.setWidth(backgroundBlock, value = 1200F)
        engine.block.setHeight(backgroundBlock, value = 600F)
        engine.block.appendChild(parent = page, child = backgroundBlock)

        val imageBlock = engine.block.create(DesignBlockType.Graphic)
        engine.block.setName(imageBlock, name = "template-image")
        engine.block.setShape(imageBlock, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setWidth(imageBlock, value = 360F)
        engine.block.setHeight(imageBlock, value = 360F)
        engine.block.setPositionX(imageBlock, value = 744F)
        engine.block.setPositionY(imageBlock, value = 120F)
        val imageFill = engine.block.createFill(FillType.Image)
        engine.block.setUri(
            block = imageFill,
            property = "fill/image/imageFileURI",
            value = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg"),
        )
        engine.block.setFill(block = imageBlock, fill = imageFill)
        engine.block.setContentFillMode(block = imageBlock, mode = ContentFillMode.COVER)
        engine.block.setPlaceholderEnabled(block = imageBlock, enabled = true)
        engine.block.appendChild(parent = page, child = imageBlock)

        val titleBlock = engine.block.create(DesignBlockType.Text)
        engine.block.setName(titleBlock, name = "template-title")
        engine.block.replaceText(titleBlock, text = "Original Template")
        engine.block.setTextFontSize(titleBlock, fontSize = 18F)
        engine.block.setTextColor(titleBlock, color = Color.fromHex("#FF23201D"))
        engine.block.setWidth(titleBlock, value = 960F)
        engine.block.setWidthMode(titleBlock, mode = SizeMode.ABSOLUTE)
        engine.block.setHeightMode(titleBlock, mode = SizeMode.AUTO)
        engine.block.setBoolean(titleBlock, property = "text/clipLinesOutsideOfFrame", value = false)
        engine.block.setPositionX(titleBlock, value = 96F)
        engine.block.setPositionY(titleBlock, value = 176F)
        engine.block.appendChild(parent = page, child = titleBlock)

        val subtitleBlock = engine.block.create(DesignBlockType.Text)
        engine.block.setName(subtitleBlock, name = "template-subtitle")
        engine.block.replaceText(subtitleBlock, text = "Stored in a local template source")
        engine.block.setTextFontSize(subtitleBlock, fontSize = 9F)
        engine.block.setTextColor(subtitleBlock, color = Color.fromHex("#FF5F5953"))
        engine.block.setWidth(subtitleBlock, value = 960F)
        engine.block.setWidthMode(subtitleBlock, mode = SizeMode.ABSOLUTE)
        engine.block.setHeightMode(subtitleBlock, mode = SizeMode.AUTO)
        engine.block.setBoolean(subtitleBlock, property = "text/clipLinesOutsideOfFrame", value = false)
        engine.block.setPositionX(subtitleBlock, value = 96F)
        engine.block.setPositionY(subtitleBlock, value = 270F)
        engine.block.appendChild(parent = page, child = subtitleBlock)
        // highlight-android-create-template

        // highlight-android-add-to-source
        val originalTemplate = engine.scene.saveToString(scene = scene)
        val originalTemplateThumbnail = engine.block.export(
            block = page,
            mimeType = MimeType.PNG,
            options = ExportOptions(targetWidth = 1200F, targetHeight = 600F),
        )
        val originalTemplateThumbnailFile = writeTemplateThumbnailFile(
            prefix = "original-template-thumbnail",
            pngData = originalTemplateThumbnail,
        )
        val originalAsset = AssetDefinition(
            id = "template-original",
            label = mapOf("en" to "Original Template"),
            tags = mapOf("en" to listOf("template", "brand")),
            groups = listOf("brand"),
            meta = mapOf(
                templateContentKey to originalTemplate,
                "thumbUri" to originalTemplateThumbnailFile.toURI().toString(),
                "mimeType" to templateMimeType,
            ),
        )
        engine.asset.addAsset(sourceId = templateSourceId, asset = originalAsset)
        // highlight-android-add-to-source
        storedThumbnailFiles += originalTemplateThumbnailFile

        val originalResults = engine.asset.findAssets(
            sourceId = templateSourceId,
            query = FindAssetsQuery(perPage = 20, page = 0, locale = "en"),
        )
        check(originalResults.assets.any { it.id == "template-original" })

        // highlight-android-edit-template
        val storedTemplate = requireNotNull(
            engine.asset.fetchAsset(
                sourceId = templateSourceId,
                assetId = "template-original",
            ),
        )
        val editableTemplate = requireNotNull(storedTemplate.meta?.get(templateContentKey))

        engine.scene.load(scene = editableTemplate, waitForResources = true)

        val editableTitle = engine.block.findByName("template-title").first()
        val editableSubtitle = engine.block.findByName("template-subtitle").first()
        engine.block.replaceText(editableTitle, text = "Updated Template")
        engine.block.replaceText(editableSubtitle, text = "Campaign: {{campaign}}")
        // highlight-android-edit-template

        // highlight-android-edit-media-and-placeholders
        val editableImage = engine.block.findByName("template-image").first()
        val editableImageFill = engine.block.getFill(editableImage)
        engine.block.setUri(
            block = editableImageFill,
            property = "fill/image/imageFileURI",
            value = Uri.parse("https://img.ly/static/ubq_samples/sample_2.jpg"),
        )
        engine.block.setPlaceholderEnabled(block = editableImage, enabled = false)
        engine.block.setPositionX(block = editableImage, value = 720F)

        val editableBackground = engine.block.getChildren(engine.scene.getPages().first()).first()
        engine.block.setFillSolidColor(editableBackground, color = Color.fromHex("#FFEAF4F8"))

        engine.variable.set(key = "campaign", value = "Spring Launch")
        // highlight-android-edit-media-and-placeholders

        // highlight-android-validate-template
        val requiredBlocks = listOf("template-title", "template-subtitle", "template-image")
        val templateHasRequiredBlocks = requiredBlocks.all { name ->
            engine.block.findByName(name).isNotEmpty()
        }
        val templateHasCampaignVariable = "campaign" in engine.variable.findAll()
        val templateHasCampaignText = engine.block
            .getString(block = editableSubtitle, property = "text/text")
            .contains("{{campaign}}")

        check(templateHasRequiredBlocks)
        check(templateHasCampaignVariable)
        check(templateHasCampaignText)
        check(!engine.block.isPlaceholderEnabled(editableImage))
        // highlight-android-validate-template

        // highlight-android-save-options
        val updatedScene = requireNotNull(engine.scene.get())
        val updatedTemplate = engine.scene.saveToString(scene = updatedScene)
        val updatedTemplateArchive = engine.scene.saveToArchive(scene = updatedScene)
        // Archive APIs load from a URI, so write the buffer to app-owned storage first.
        val updatedTemplateArchiveFile = File.createTempFile("updated-template", ".zip")
        withContext(Dispatchers.IO) {
            updatedTemplateArchiveFile.outputStream().use { output ->
                val archiveBuffer = updatedTemplateArchive.asReadOnlyBuffer()
                while (archiveBuffer.hasRemaining()) {
                    output.channel.write(archiveBuffer)
                }
            }
        }
        val archivedTemplateScene = engine.scene.loadArchive(
            archiveUri = Uri.fromFile(updatedTemplateArchiveFile),
            waitForResources = true,
        )
        val archiveLoaded = archivedTemplateScene == engine.scene.get()
        // highlight-android-save-options
        runCatching { updatedTemplateArchiveFile.delete() }

        // highlight-android-update-metadata
        val updatedTemplateThumbnail = engine.block.export(
            block = engine.scene.getPages().first(),
            mimeType = MimeType.PNG,
            options = ExportOptions(targetWidth = 1200F, targetHeight = 600F),
        )
        val updatedTemplateThumbnailFile = writeTemplateThumbnailFile(
            prefix = "updated-template-thumbnail",
            pngData = updatedTemplateThumbnail,
        )
        engine.asset.addAsset(
            sourceId = templateSourceId,
            asset = AssetDefinition(
                id = "template-updated",
                label = mapOf("en" to "Spring Launch Template"),
                tags = mapOf("en" to listOf("template", "spring", "updated")),
                groups = listOf("brand"),
                meta = mapOf(
                    templateContentKey to updatedTemplate,
                    "thumbUri" to updatedTemplateThumbnailFile.toURI().toString(),
                    "mimeType" to templateMimeType,
                ),
            ),
        )
        // highlight-android-update-metadata
        storedThumbnailFiles += updatedTemplateThumbnailFile

        check(updatedTemplate.isNotBlank())
        check(updatedTemplateArchive.remaining() > 0)

        engine.asset.addAsset(
            sourceId = templateSourceId,
            asset = originalAsset.copy(
                id = "template-temporary",
                label = mapOf("en" to "Temporary Template"),
            ),
        )

        // highlight-android-remove-template
        engine.asset.removeAsset(
            sourceId = templateSourceId,
            assetId = "template-temporary",
        )
        // highlight-android-remove-template

        val afterRemoval = engine.asset.findAssets(
            sourceId = templateSourceId,
            query = FindAssetsQuery(perPage = 20, page = 0, locale = "en"),
        )
        val temporaryTemplateRemoved = afterRemoval.assets.none { it.id == "template-temporary" }
        check(temporaryTemplateRemoved)

        // highlight-android-update-in-source
        engine.scene.load(scene = updatedTemplate, waitForResources = true)
        val finalSubtitle = engine.block.findByName("template-subtitle").first()
        engine.block.replaceText(finalSubtitle, text = "Updated again with new content")

        val refreshedScene = requireNotNull(engine.scene.get())
        val refreshedTemplate = engine.scene.saveToString(scene = refreshedScene)
        val refreshedPage = engine.scene.getPages().first()
        val refreshedTemplatePreview = engine.block.export(
            block = refreshedPage,
            mimeType = MimeType.PNG,
            options = ExportOptions(targetWidth = 1200F, targetHeight = 600F),
        )
        val refreshedTemplateThumbnailFile = writeTemplateThumbnailFile(
            prefix = "refreshed-template-thumbnail",
            pngData = refreshedTemplatePreview,
        )

        engine.asset.removeAsset(sourceId = templateSourceId, assetId = "template-updated")
        engine.asset.addAsset(
            sourceId = templateSourceId,
            asset = AssetDefinition(
                id = "template-updated",
                label = mapOf("en" to "Spring Launch Template"),
                tags = mapOf("en" to listOf("template", "spring", "updated")),
                groups = listOf("brand"),
                meta = mapOf(
                    templateContentKey to refreshedTemplate,
                    "thumbUri" to refreshedTemplateThumbnailFile.toURI().toString(),
                    "mimeType" to templateMimeType,
                ),
            ),
        )
        // highlight-android-update-in-source
        storedThumbnailFiles += refreshedTemplateThumbnailFile

        val finalResults = engine.asset.findAssets(
            sourceId = templateSourceId,
            query = FindAssetsQuery(perPage = 20, page = 0, locale = "en"),
        )
        val refreshedAsset = finalResults.assets.first { it.id == "template-updated" }
        val appliedTemplateBlock = engine.asset.applyAssetSourceAsset(
            sourceId = templateSourceId,
            asset = refreshedAsset,
        )
        val templateAppliedFromSource = appliedTemplateBlock == null &&
            engine.block.findByName("template-subtitle").isNotEmpty()

        return TemplateManagementSummary(
            sourceId = templateSourceId,
            originalTemplateCount = originalResults.assets.size,
            finalTemplateCount = finalResults.assets.size,
            updatedTemplateLabel = refreshedAsset.label.orEmpty(),
            temporaryTemplateRemoved = temporaryTemplateRemoved,
            templateAppliedFromSource = templateAppliedFromSource,
            refreshedTemplateContent = requireNotNull(refreshedAsset.meta?.get(templateContentKey)),
            refreshedTemplateContentKey = templateContentKey,
            refreshedTemplatePreview = refreshedTemplatePreview,
            archiveByteCount = updatedTemplateArchive.remaining(),
            archiveLoaded = archiveLoaded,
            validatedBeforeSaving = templateHasRequiredBlocks && templateHasCampaignVariable && templateHasCampaignText,
        )
    } finally {
        if (templateSourceId in engine.asset.findAllSources()) {
            engine.asset.removeSource(sourceId = templateSourceId)
        }
        storedThumbnailFiles.forEach { thumbnailFile ->
            runCatching { thumbnailFile.delete() }
        }
    }
}
