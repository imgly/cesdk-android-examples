import android.net.Uri
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import ly.img.engine.AssetDefinition
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.FindAssetsQuery
import ly.img.engine.MimeType
import ly.img.engine.SizeMode
import java.io.File
import java.nio.ByteBuffer

data class TextDesignsResult(
    val componentBlock: DesignBlock,
    val insertedBlock: DesignBlock,
    val sourceId: String,
    val assetCount: Int,
    val contentJson: String,
    val archiveByteCount: Int,
    val thumbnailByteCount: Int,
    val previewPng: ByteBuffer,
)

suspend fun textDesigns(engine: Engine): TextDesignsResult {
    val sourceId = "ly.img.text.components"

    return try {
        val scene = engine.scene.create()
        val page = engine.block.create(DesignBlockType.Page)
        engine.block.setWidth(page, value = 800F)
        engine.block.setHeight(page, value = 600F)
        engine.block.appendChild(parent = scene, child = page)

        val previewFill = engine.block.createFill(FillType.Color)
        engine.block.setFill(page, fill = previewFill)
        engine.block.setFillSolidColor(block = page, color = Color.fromHex("#FFF1F5F9"))

        // highlight-android-create-text-design
        val textDesign = engine.block.create(DesignBlockType.Text)
        engine.block.replaceText(textDesign, text = "Launch Day")
        engine.block.setTextFontSize(textDesign, fontSize = 96F)
        engine.block.setTextColor(textDesign, color = Color.fromRGBA(r = 0.14F, g = 0.2F, b = 0.28F, a = 1F))
        engine.block.setPositionX(textDesign, value = 60F)
        engine.block.setPositionY(textDesign, value = 180F)
        engine.block.setWidthMode(textDesign, mode = SizeMode.ABSOLUTE)
        engine.block.setHeightMode(textDesign, mode = SizeMode.ABSOLUTE)
        engine.block.setWidth(textDesign, value = 680F)
        engine.block.setHeight(textDesign, value = 180F)
        engine.block.setClipped(block = textDesign, clipped = true)
        engine.block.setBoolean(textDesign, property = "text/clipLinesOutsideOfFrame", value = true)
        engine.block.setBoolean(textDesign, property = "text/automaticFontSizeEnabled", value = true)
        engine.block.setFloat(textDesign, property = "text/minAutomaticFontSize", value = 28F)
        engine.block.setFloat(textDesign, property = "text/maxAutomaticFontSize", value = 96F)
        engine.block.setScopeEnabled(block = textDesign, key = "text/edit", enabled = true)
        engine.block.setScopeEnabled(block = textDesign, key = "layer/resize", enabled = true)
        engine.block.setScopeEnabled(block = textDesign, key = "layer/rotate", enabled = false)
        engine.block.appendChild(parent = page, child = textDesign)
        // highlight-android-create-text-design

        engine.block.forceLoadResources(blocks = listOf(textDesign))

        waitForScheduledEngineUpdate()

        // highlight-android-save-archive
        engine.block.forceLoadResources(blocks = listOf(textDesign))
        val archiveBuffer = engine.block.saveToArchive(blocks = listOf(textDesign))
        val archiveByteCount = archiveBuffer.remaining()
        val archiveFile = File.createTempFile("brand-title", ".zip").apply {
            outputStream().channel.use { channel ->
                val archiveBytes = archiveBuffer.asReadOnlyBuffer()
                while (archiveBytes.hasRemaining()) {
                    channel.write(archiveBytes)
                }
            }
        }
        // highlight-android-save-archive

        // highlight-android-export-thumbnail
        val thumbnailBuffer = engine.block.export(
            block = textDesign,
            mimeType = MimeType.PNG,
            options = ExportOptions(targetWidth = 400F, targetHeight = 320F),
        )
        val thumbnailByteCount = thumbnailBuffer.remaining()
        val thumbnailFile = File.createTempFile("brand-title-thumbnail", ".png").apply {
            outputStream().channel.use { channel ->
                val thumbnailBytes = thumbnailBuffer.asReadOnlyBuffer()
                while (thumbnailBytes.hasRemaining()) {
                    channel.write(thumbnailBytes)
                }
            }
        }
        // highlight-android-export-thumbnail

        // highlight-android-create-content-json
        val contentJson = """
            {
              "version": "5.0.0",
              "id": "$sourceId",
              "assets": [
                {
                  "id": "$sourceId.brand-title",
                  "label": { "en": "Brand Title" },
                  "meta": {
                    "uri": "{{base_url}}/data/brand-title/blocks.blocks",
                    "thumbUri": "{{base_url}}/thumbnails/brand-title.png",
                    "mimeType": "application/ubq-blocks-string"
                  }
                }
              ]
            }
        """.trimIndent()
        // highlight-android-create-content-json

        // highlight-android-register-local-source
        if (sourceId in engine.asset.findAllSources()) {
            engine.asset.removeSource(sourceId = sourceId)
        }

        engine.asset.addLocalSource(
            sourceId = sourceId,
            supportedMimeTypes = emptyList(),
            applyAsset = { asset ->
                val archiveUri = asset.meta?.get("uri")?.let(Uri::parse)
                    ?: error("Text design asset ${asset.id} is missing meta.uri.")
                val loadedBlock = engine.block.loadFromArchive(archiveUri).first()
                val currentPage = engine.scene.getCurrentPage()
                    ?: error("Text design assets can only be inserted when the scene has a current page.")
                engine.block.appendChild(parent = currentPage, child = loadedBlock)
                loadedBlock
            },
        )

        engine.asset.addAsset(
            sourceId = sourceId,
            asset = AssetDefinition(
                id = "$sourceId.brand-title",
                label = mapOf("en" to "Brand Title"),
                meta = mapOf(
                    "uri" to Uri.fromFile(archiveFile).toString(),
                    "thumbUri" to Uri.fromFile(thumbnailFile).toString(),
                    "mimeType" to "application/ubq-blocks-string",
                ),
            ),
        )
        engine.asset.assetSourceContentsChanged(sourceId = sourceId)

        val queryResult = engine.asset.findAssets(
            sourceId = sourceId,
            query = FindAssetsQuery(page = 0, perPage = 10),
        )
        val textDesignAsset = queryResult.assets.first()
        val insertedBlock = engine.asset.applyAssetSourceAsset(
            sourceId = sourceId,
            asset = textDesignAsset,
        ) ?: error("Applying the text design should insert a block.")
        engine.block.setPositionY(insertedBlock, value = 390F)
        // highlight-android-register-local-source

        engine.block.forceLoadResources(blocks = listOf(page, textDesign, insertedBlock))
        val previewPng = engine.block.export(
            block = page,
            mimeType = MimeType.PNG,
            options = ExportOptions(targetWidth = 800F, targetHeight = 600F),
        )

        TextDesignsResult(
            componentBlock = textDesign,
            insertedBlock = insertedBlock,
            sourceId = sourceId,
            assetCount = queryResult.total,
            contentJson = contentJson,
            archiveByteCount = archiveByteCount,
            thumbnailByteCount = thumbnailByteCount,
            previewPng = previewPng,
        )
    } finally {
        if (sourceId in engine.asset.findAllSources()) {
            engine.asset.removeSource(sourceId = sourceId)
        }
    }
}

suspend fun registerHostedTextDesigns(
    engine: Engine,
    sourceId: String,
    contentJson: String,
    basePath: String,
): String {
    // highlight-android-register-hosted-source
    if (sourceId in engine.asset.findAllSources()) {
        engine.asset.removeSource(sourceId = sourceId)
    }

    val registeredSourceId = engine.asset.addLocalSourceFromJSON(contentJSON = contentJson, basePath = basePath)
    // highlight-android-register-hosted-source
    return registeredSourceId
}

private suspend fun waitForScheduledEngineUpdate() {
    // Offscreen text layout is resolved on scheduled engine frames before export.
    repeat(3) {
        yield()
        delay(16)
    }
}
