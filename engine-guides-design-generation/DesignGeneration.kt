import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.DesignUnit
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.FontUnit
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

data class DesignGenerationResult(
    val referencedVariableKeys: Set<String>,
    val populatedVariables: Map<String, String>,
    val namedImageBlockCount: Int,
    val imageFillType: String,
    val storedImageUri: Uri,
    val exportedPng: ByteBuffer,
    val outputFile: File,
)

data class DesignGenerationFixture(
    val templateFile: File,
    val replacementImageUri: Uri,
    val outputFile: File,
)

suspend fun designGeneration(
    engine: Engine,
    templateUri: Uri,
    replacementImageUri: Uri,
    outputFile: File,
): DesignGenerationResult = withContext(engine.dispatcher) {
    // highlight-android-load-template
    engine.scene.load(
        sceneUri = templateUri,
        overrideEditorConfig = true,
        waitForResources = true,
    )
    val page = checkNotNull(engine.scene.getPages().singleOrNull()) {
        "The template must contain exactly one page."
    }
    // highlight-android-load-template

    // highlight-android-map-and-validate-data
    data class DesignRecord(
        val firstName: String,
        val lastName: String,
        val address: String,
        val city: String,
        val imageUri: Uri,
    )

    val record = DesignRecord(
        firstName = "John",
        lastName = "Doe",
        address = "123 Main St.",
        city = "Anytown",
        imageUri = replacementImageUri,
    )
    val requiredVariableKeys = setOf("first_name", "last_name", "address", "city")
    val textBlocks = engine.block.findByType(DesignBlockType.Text)
    check(textBlocks.any { textBlock -> engine.block.referencesAnyVariables(block = textBlock) }) {
        "Template text blocks must reference at least one variable."
    }
    val variableTokenPattern = Regex("""\{\{\s*([^{}]+?)\s*\}\}""")
    val referencedVariableKeys = textBlocks
        .flatMap { textBlock ->
            variableTokenPattern.findAll(engine.block.getString(block = textBlock, property = "text/text"))
                .map { match -> match.groupValues[1].trim() }
                .toList()
        }
        .toSet()
    val missingVariableKeys = requiredVariableKeys - referencedVariableKeys
    check(missingVariableKeys.isEmpty()) {
        "Template text is missing variable references: ${missingVariableKeys.sorted().joinToString()}"
    }

    val imageBlockName = "profile-photo"
    val imageBlocks = engine.block.findByName(name = imageBlockName)
    val imageBlock = checkNotNull(imageBlocks.singleOrNull()) {
        "Template must contain exactly one block named '$imageBlockName'."
    }
    val imageFill = engine.block.getFill(block = imageBlock)
    val imageFillType = engine.block.getType(block = imageFill)
    check(imageFillType == FillType.Image.key) {
        "Block '$imageBlockName' must use an image fill."
    }
    // highlight-android-map-and-validate-data

    // highlight-android-populate-text-variables
    engine.variable.set(key = "first_name", value = record.firstName)
    engine.variable.set(key = "last_name", value = record.lastName)
    engine.variable.set(key = "address", value = record.address)
    engine.variable.set(key = "city", value = record.city)
    // highlight-android-populate-text-variables

    // highlight-android-replace-named-image
    val imageUriProperty = "fill/image/imageFileURI"
    engine.block.setUri(
        block = imageFill,
        property = imageUriProperty,
        value = record.imageUri,
    )
    engine.block.resetCrop(block = imageBlock)
    val storedImageUri = engine.block.getUri(block = imageFill, property = imageUriProperty)
    // highlight-android-replace-named-image

    // highlight-android-export-design
    engine.block.forceLoadResources(blocks = listOf(page))
    val exportedPng = engine.block.export(block = page, mimeType = MimeType.PNG).apply {
        rewind()
    }

    withContext(Dispatchers.IO) {
        FileOutputStream(outputFile).channel.use { channel ->
            val readablePng = exportedPng.asReadOnlyBuffer()
            while (readablePng.hasRemaining()) {
                channel.write(readablePng)
            }
        }
    }
    // highlight-android-export-design

    DesignGenerationResult(
        referencedVariableKeys = referencedVariableKeys,
        populatedVariables = mapOf(
            "first_name" to engine.variable.get(key = "first_name"),
            "last_name" to engine.variable.get(key = "last_name"),
            "address" to engine.variable.get(key = "address"),
            "city" to engine.variable.get(key = "city"),
        ),
        namedImageBlockCount = imageBlocks.size,
        imageFillType = imageFillType,
        storedImageUri = storedImageUri,
        exportedPng = exportedPng.asReadOnlyBuffer(),
        outputFile = outputFile,
    )
}

// Deterministic serialized template used by the Android smoke test.
suspend fun createDesignGenerationFixture(
    engine: Engine,
    directory: File,
): DesignGenerationFixture = withContext(engine.dispatcher) {
    val previousVariables = engine.variable.findAll().associateWith { key ->
        engine.variable.get(key = key)
    }

    val templateString = try {
        previousVariables.keys.forEach { key -> engine.variable.remove(key = key) }

        val scene = engine.scene.create(designUnit = DesignUnit.PIXEL, fontSizeUnit = FontUnit.PIXEL)
        val page = engine.block.create(DesignBlockType.Page)
        engine.block.setWidth(block = page, value = 900F)
        engine.block.setHeight(block = page, value = 600F)
        engine.block.appendChild(parent = scene, child = page)

        engine.block.setFillSolidColor(
            block = page,
            color = Color.fromHex("#F6F7F9"),
        )

        val accent = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block = accent, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setPositionX(block = accent, value = 0F)
        engine.block.setPositionY(block = accent, value = 0F)
        engine.block.setWidth(block = accent, value = 52F)
        engine.block.setHeight(block = accent, value = 600F)
        engine.block.setFill(block = accent, fill = engine.block.createFill(FillType.Color))
        engine.block.setFillSolidColor(
            block = accent,
            color = Color.fromHex("#146C5A"),
        )
        engine.block.appendChild(parent = page, child = accent)

        val name = engine.block.create(DesignBlockType.Text)
        engine.block.replaceText(block = name, text = "{{first_name}} {{last_name}}")
        engine.block.setTextFontSize(block = name, fontSize = 48F)
        engine.block.setTextColor(block = name, color = Color.fromHex("#17212B"))
        engine.block.setPositionX(block = name, value = 96F)
        engine.block.setPositionY(block = name, value = 104F)
        engine.block.setWidth(block = name, value = 380F)
        engine.block.setHeight(block = name, value = 92F)
        engine.block.appendChild(parent = page, child = name)

        val address = engine.block.create(DesignBlockType.Text)
        engine.block.replaceText(block = address, text = "{{address}}\n{{city}}")
        engine.block.setTextFontSize(block = address, fontSize = 28F)
        engine.block.setTextColor(block = address, color = Color.fromHex("#40505F"))
        engine.block.setPositionX(block = address, value = 96F)
        engine.block.setPositionY(block = address, value = 224F)
        engine.block.setWidth(block = address, value = 350F)
        engine.block.setHeight(block = address, value = 140F)
        engine.block.appendChild(parent = page, child = address)

        val imageBlock = engine.block.create(DesignBlockType.Graphic)
        engine.block.setName(block = imageBlock, name = "profile-photo")
        engine.block.setShape(block = imageBlock, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setPositionX(block = imageBlock, value = 520F)
        engine.block.setPositionY(block = imageBlock, value = 72F)
        engine.block.setWidth(block = imageBlock, value = 308F)
        engine.block.setHeight(block = imageBlock, value = 456F)
        engine.block.setContentFillMode(block = imageBlock, mode = ContentFillMode.COVER)
        val imageFill = engine.block.createFill(FillType.Image)
        engine.block.setUri(
            block = imageFill,
            property = "fill/image/imageFileURI",
            value = Uri.parse("file:///android_asset/imgly-assets/ly.img.image/images/sample_1.jpg"),
        )
        engine.block.setFill(block = imageBlock, fill = imageFill)
        engine.block.appendChild(parent = page, child = imageBlock)

        engine.variable.set(key = "first_name", value = "First")
        engine.variable.set(key = "last_name", value = "Last")
        engine.variable.set(key = "address", value = "Address")
        engine.variable.set(key = "city", value = "City")
        engine.block.forceLoadResources(blocks = listOf(page))
        engine.scene.saveToString(scene = scene)
    } finally {
        engine.variable.findAll().forEach { key -> engine.variable.remove(key = key) }
        previousVariables.forEach { (key, value) -> engine.variable.set(key = key, value = value) }
    }

    withContext(Dispatchers.IO) {
        val templateFile = File.createTempFile("design-generation-template-", ".imgly", directory).apply {
            writeText(templateString)
        }
        val outputFile = File.createTempFile("design-generation-output-", ".png", directory)
        DesignGenerationFixture(
            templateFile = templateFile,
            replacementImageUri = Uri.parse(
                "file:///android_asset/imgly-assets/ly.img.image/images/sample_2.jpg",
            ),
            outputFile = outputFile,
        )
    }
}
