import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ly.img.engine.AssetColorProperty
import ly.img.engine.AssetDefinition
import ly.img.engine.AssetPayload
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FindAssetsQuery
import ly.img.engine.SizeMode
import org.json.JSONObject
import java.io.File
import android.graphics.Color as AndroidColor

private const val CaptionPresetSourceId = "ly.img.caption.presets"
private const val CaptionPresetAssetId = "ly.img.caption.presets.neon-glow"
private const val ExistingCaptionPresetAssetId = "ly.img.caption.presets.existing"

private val CaptionPresetContentJson = """
    {
      "version": "3.0.0",
      "id": "ly.img.caption.presets",
      "assets": [
        {
          "id": "ly.img.caption.presets.neon-glow",
          "label": { "en": "Neon Glow" },
          "meta": {
            "uri": "{{base_url}}/ly.img.caption.presets/presets/neon-glow.preset",
            "thumbUri": "{{base_url}}/ly.img.caption.presets/thumbnails/neon-glow.png",
            "mimeType": "application/ubq-blocks-string"
          },
          "payload": {
            "properties": [
              {
                "type": "Color",
                "property": "fill/solid/color",
                "value": { "r": 0.0, "g": 1.0, "b": 1.0, "a": 1.0 },
                "defaultValue": { "r": 0.0, "g": 1.0, "b": 1.0, "a": 1.0 }
              },
              {
                "type": "Color",
                "property": "dropShadow/color",
                "value": { "r": 0.0, "g": 1.0, "b": 1.0, "a": 0.8 },
                "defaultValue": { "r": 0.0, "g": 1.0, "b": 1.0, "a": 0.8 }
              },
              {
                "type": "Color",
                "property": "backgroundColor/color",
                "value": { "r": 0.0, "g": 0.0, "b": 0.1, "a": 0.7 },
                "defaultValue": { "r": 0.0, "g": 0.0, "b": 0.1, "a": 0.7 }
              }
            ]
          }
        }
      ]
    }
""".trimIndent()

data class CaptionPresetSummary(
    val serializedPresetLength: Int,
    val presetFileUri: String,
    val presetFileLength: Long,
    val loadedPresetCount: Int,
    val loadedPresetId: String,
    val loadedPresetUri: String,
)

fun updateCaptionPresets(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
): Job = CoroutineScope(Dispatchers.Main).launch {
    runUpdateCaptionPresets(license, userId)
}

suspend fun runUpdateCaptionPresets(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
): CaptionPresetSummary {
    val engine = Engine.getInstance(id = "ly.img.engine.update-caption-presets")

    try {
        engine.start(license = license, userId = userId)
        engine.bindOffscreen(width = 1280, height = 720)

        // highlight-android-create-text-block
        val scene = engine.scene.createForVideo()
        val page = engine.block.create(DesignBlockType.Page)
        engine.block.appendChild(parent = scene, child = page)
        engine.block.setWidth(page, value = 1280F)
        engine.block.setHeight(page, value = 720F)

        val textBlock = engine.block.create(DesignBlockType.Text)
        engine.block.appendChild(parent = page, child = textBlock)

        engine.block.replaceText(textBlock, text = "NEON GLOW")
        engine.block.setPositionX(textBlock, value = 50F)
        engine.block.setPositionY(textBlock, value = 200F)
        engine.block.setWidth(textBlock, value = 600F)
        engine.block.setHeightMode(textBlock, mode = SizeMode.AUTO)
        // highlight-android-create-text-block

        // highlight-android-style-text-color
        val neonCyan = Color.fromRGBA(r = 0F, g = 1F, b = 1F, a = 1F)
        engine.block.setTextColor(block = textBlock, color = neonCyan)
        engine.block.setFillSolidColor(block = textBlock, color = neonCyan)
        // highlight-android-style-text-color

        // highlight-android-style-font
        engine.block.setTextFontSize(block = textBlock, fontSize = 48F)
        // highlight-android-style-font

        // highlight-android-style-drop-shadow
        engine.block.setDropShadowEnabled(block = textBlock, enabled = true)
        engine.block.setDropShadowColor(
            block = textBlock,
            color = Color.fromRGBA(r = 0F, g = 1F, b = 1F, a = 0.8F),
        )
        engine.block.setDropShadowBlurRadiusX(block = textBlock, blurRadiusX = 20F)
        engine.block.setDropShadowBlurRadiusY(block = textBlock, blurRadiusY = 20F)
        engine.block.setDropShadowOffsetX(block = textBlock, offsetX = 0F)
        engine.block.setDropShadowOffsetY(block = textBlock, offsetY = 0F)
        // highlight-android-style-drop-shadow

        // highlight-android-style-background
        engine.block.setBackgroundColorEnabled(block = textBlock, enabled = true)
        engine.block.setBackgroundColor(
            block = textBlock,
            color = Color.fromRGBA(r = 0F, g = 0F, b = 0.1F, a = 0.7F),
        )
        // highlight-android-style-background

        // highlight-android-serialize-preset
        val serializedPreset = engine.block.saveToString(
            blocks = listOf(textBlock),
            allowedResourceSchemes = listOf("bundle", "file", "http", "https"),
        )
        check(serializedPreset.isNotBlank()) { "Serialized caption preset was empty." }
        // highlight-android-serialize-preset

        val localPresetSource = createLocalCaptionPresetSource(serializedPreset)
        check(localPresetSource.presetFile.exists()) {
            "Caption preset file was not written."
        }
        check(localPresetSource.presetFile.length() > 0) {
            "Caption preset file was empty."
        }
        check(localPresetSource.presetFile.readText() == serializedPreset) {
            "Caption preset file did not match the serialized preset."
        }

        seedExistingCaptionPresetSource(engine, localPresetSource.baseUri)
        val loadedPresets = loadCaptionPresetSource(
            engine = engine,
            assetsBaseUri = localPresetSource.baseUri,
        )
        check(loadedPresets.loadedPresetId == CaptionPresetAssetId)
        val loadedPresetFile = File(
            checkNotNull(Uri.parse(loadedPresets.loadedPresetUri).path) {
                "Loaded caption preset URI did not resolve to a file path."
            },
        )
        check(loadedPresetFile.canonicalFile == localPresetSource.presetFile.canonicalFile) {
            "Loaded caption preset URI did not point to the generated preset file."
        }
        check(loadedPresetFile.exists() && loadedPresetFile.length() > 0) {
            "Loaded caption preset file was missing or empty."
        }
        val existingPreset = engine.asset.fetchAsset(
            sourceId = CaptionPresetSourceId,
            assetId = ExistingCaptionPresetAssetId,
        )
        check(existingPreset?.id == ExistingCaptionPresetAssetId) {
            "Existing caption preset was removed while loading a custom preset."
        }

        return CaptionPresetSummary(
            serializedPresetLength = serializedPreset.length,
            presetFileUri = localPresetSource.presetUri.toString(),
            presetFileLength = localPresetSource.presetFile.length(),
            loadedPresetCount = loadedPresets.loadedPresetCount,
            loadedPresetId = loadedPresets.loadedPresetId,
            loadedPresetUri = loadedPresets.loadedPresetUri,
        )
    } finally {
        engine.stop()
    }
}

// highlight-android-manual-color-properties
fun createCaptionPresetAssetDefinitionWithProperties(
    presetUri: String,
    thumbnailUri: String,
): AssetDefinition = AssetDefinition(
    id = CaptionPresetAssetId,
    label = mapOf("en" to "Neon Glow"),
    meta = mapOf(
        "uri" to presetUri,
        "thumbUri" to thumbnailUri,
        "mimeType" to "application/ubq-blocks-string",
    ),
    payload = AssetPayload(
        properties = listOf(
            AssetColorProperty(
                property = "fill/solid/color",
                value = Color.fromRGBA(r = 0F, g = 1F, b = 1F, a = 1F),
                defaultValue = Color.fromRGBA(r = 0F, g = 1F, b = 1F, a = 1F),
            ),
            AssetColorProperty(
                property = "backgroundColor/color",
                value = Color.fromRGBA(r = 0F, g = 0F, b = 0.1F, a = 0.7F),
                defaultValue = Color.fromRGBA(r = 0F, g = 0F, b = 0.1F, a = 0.7F),
            ),
            AssetColorProperty(
                property = "dropShadow/color",
                value = Color.fromRGBA(r = 0F, g = 1F, b = 1F, a = 0.8F),
                defaultValue = Color.fromRGBA(r = 0F, g = 1F, b = 1F, a = 0.8F),
            ),
        ),
    ),
)
// highlight-android-manual-color-properties

private data class LocalCaptionPresetSource(
    val baseUri: Uri,
    val presetFile: File,
    val presetUri: Uri,
)

private fun createLocalCaptionPresetSource(serializedPreset: String): LocalCaptionPresetSource {
    val baseDirectory = File.createTempFile("caption-presets", "").apply {
        delete()
        mkdirs()
    }
    val sourceDirectory = File(baseDirectory, CaptionPresetSourceId).apply { mkdirs() }
    File(sourceDirectory, "content.json").writeText(CaptionPresetContentJson)

    val presetsDirectory = File(sourceDirectory, "presets").apply { mkdirs() }
    val presetFile = File(presetsDirectory, "neon-glow.preset")
    presetFile.writeText(serializedPreset)

    val thumbnailsDirectory = File(sourceDirectory, "thumbnails").apply { mkdirs() }
    writeCaptionPresetThumbnail(File(thumbnailsDirectory, "neon-glow.png"))

    return LocalCaptionPresetSource(
        baseUri = Uri.fromFile(baseDirectory),
        presetFile = presetFile,
        presetUri = Uri.fromFile(presetFile),
    )
}

private fun seedExistingCaptionPresetSource(
    engine: Engine,
    assetsBaseUri: Uri,
) {
    if (engine.asset.findAllSources().contains(CaptionPresetSourceId)) {
        engine.asset.removeAsset(
            sourceId = CaptionPresetSourceId,
            assetId = ExistingCaptionPresetAssetId,
        )
    } else {
        engine.asset.addLocalSource(CaptionPresetSourceId, emptyList())
    }

    val sourceBaseUri = assetsBaseUri
        .buildUpon()
        .appendPath(CaptionPresetSourceId)
        .build()

    engine.asset.addAsset(
        sourceId = CaptionPresetSourceId,
        asset = AssetDefinition(
            id = ExistingCaptionPresetAssetId,
            label = mapOf("en" to "Existing Caption Preset"),
            meta = mapOf(
                "uri" to sourceBaseUri
                    .buildUpon()
                    .appendPath("presets")
                    .appendPath("neon-glow.preset")
                    .build()
                    .toString(),
                "thumbUri" to sourceBaseUri
                    .buildUpon()
                    .appendPath("thumbnails")
                    .appendPath("neon-glow.png")
                    .build()
                    .toString(),
                "mimeType" to "application/ubq-blocks-string",
            ),
        ),
    )
}

private fun writeCaptionPresetThumbnail(outputFile: File) {
    val bitmap = Bitmap.createBitmap(320, 180, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(AndroidColor.rgb(4, 8, 20))

    val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.CYAN
        textAlign = Paint.Align.CENTER
        textSize = 48F
        setShadowLayer(18F, 0F, 0F, AndroidColor.CYAN)
    }
    val baseline = (bitmap.height - glowPaint.descent() - glowPaint.ascent()) / 2F
    canvas.drawText("NEON GLOW", bitmap.width / 2F, baseline, glowPaint)

    outputFile.outputStream().use { stream ->
        check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
            "Failed to write caption preset thumbnail."
        }
    }
    check(outputFile.length() > 0) { "Caption preset thumbnail was empty." }
}

data class LoadedCaptionPresets(
    val loadedPresetCount: Int,
    val loadedPresetId: String,
    val loadedPresetUri: String,
)

suspend fun loadCaptionPresetSource(
    engine: Engine,
    assetsBaseUri: Uri,
): LoadedCaptionPresets {
    // highlight-android-load-custom-presets
    val contentJsonUri = assetsBaseUri
        .buildUpon()
        .appendPath(CaptionPresetSourceId)
        .appendPath("content.json")
        .build()
    val customPresetIds = JSONObject(CaptionPresetContentJson)
        .getJSONArray("assets")
        .let { assets ->
            (0 until assets.length()).map { index ->
                assets.getJSONObject(index).getString("id")
            }
        }

    val sourceExists = engine.asset.findAllSources().contains(CaptionPresetSourceId)
    if (sourceExists) {
        // Remove every preset defined by this manifest before reloading it.
        // Other presets in the same source stay available.
        customPresetIds.forEach { assetId ->
            engine.asset.removeAsset(
                sourceId = CaptionPresetSourceId,
                assetId = assetId,
            )
        }
    }

    engine.asset.addLocalSourceFromJSON(contentUri = contentJsonUri)

    val presets = engine.asset.findAssets(
        sourceId = CaptionPresetSourceId,
        query = FindAssetsQuery(page = 0, perPage = 10),
    )
    val loadedPresetAssets = customPresetIds.map { assetId ->
        checkNotNull(
            engine.asset.fetchAsset(
                sourceId = CaptionPresetSourceId,
                assetId = assetId,
            ),
        ) { "Caption preset $assetId was not loaded." }
    }
    // highlight-android-load-custom-presets
    val loadedPreset = loadedPresetAssets.first()
    val loadedPresetUri = checkNotNull(loadedPreset.meta?.get("uri")) {
        "Loaded caption preset was missing a preset URI."
    }

    return LoadedCaptionPresets(
        loadedPresetCount = presets.assets.size,
        loadedPresetId = loadedPreset.id,
        loadedPresetUri = loadedPresetUri,
    )
}
