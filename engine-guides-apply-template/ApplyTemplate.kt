import android.net.Uri
import ly.img.engine.DesignBlockType
import ly.img.engine.DesignUnit
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FontUnit
import ly.img.engine.MimeType
import ly.img.engine.SizeMode
import java.io.File
import java.nio.ByteBuffer
import kotlin.math.abs

data class ApplyTemplate(
    val uriWidth: Float,
    val uriHeight: Float,
    val switchedWidth: Float,
    val switchedHeight: Float,
    val stringWidth: Float,
    val stringHeight: Float,
    val serializedTemplateLength: Int,
    val previewPngData: ByteBuffer,
)

suspend fun applyTemplate(engine: Engine): ApplyTemplate {
    // highlight-android-template-inputs
    val firstTemplateString = createTemplateString(engine = engine, headlineText = "Spring Sale")
    val secondTemplateString = createTemplateString(engine = engine, headlineText = "New Arrivals")
    val firstTemplateFile = File.createTempFile("spring-sale-template", ".scene").apply {
        writeText(firstTemplateString)
    }
    val secondTemplateFile = File.createTempFile("new-arrivals-template", ".scene").apply {
        writeText(secondTemplateString)
    }
    // highlight-android-template-inputs

    try {
        return applyTemplates(
            engine = engine,
            firstTemplateFile = firstTemplateFile,
            secondTemplateFile = secondTemplateFile,
            firstTemplateString = firstTemplateString,
        )
    } finally {
        firstTemplateFile.delete()
        secondTemplateFile.delete()
    }
}

private suspend fun applyTemplates(
    engine: Engine,
    firstTemplateFile: File,
    secondTemplateFile: File,
    firstTemplateString: String,
): ApplyTemplate {
    // highlight-android-setup
    val scene = engine.scene.create(designUnit = DesignUnit.PIXEL, fontSizeUnit = FontUnit.PIXEL)
    val page = engine.scene.getPages().firstOrNull() ?: engine.block.create(DesignBlockType.Page).also {
        engine.block.appendChild(parent = scene, child = it)
    }

    val targetWidth = 1080F
    val targetHeight = 1920F
    engine.block.setFloat(
        block = scene,
        property = "scene/pageDimensions/width",
        value = targetWidth,
    )
    engine.block.setFloat(
        block = scene,
        property = "scene/pageDimensions/height",
        value = targetHeight,
    )
    engine.block.setWidthMode(block = page, mode = SizeMode.ABSOLUTE)
    engine.block.setHeightMode(block = page, mode = SizeMode.ABSOLUTE)
    engine.block.setWidth(block = page, value = targetWidth)
    engine.block.setHeight(block = page, value = targetHeight)
    // highlight-android-setup

    // highlight-android-apply-from-uri
    val templateUri = Uri.fromFile(firstTemplateFile)
    engine.scene.applyTemplate(templateUri = templateUri)
    // highlight-android-apply-from-uri

    // highlight-android-verify-dimensions
    val appliedScene = requireNotNull(engine.scene.get()) { "No scene loaded after applying the template." }
    val appliedWidth = engine.block.getFloat(
        block = appliedScene,
        property = "scene/pageDimensions/width",
    )
    val appliedHeight = engine.block.getFloat(
        block = appliedScene,
        property = "scene/pageDimensions/height",
    )

    check(abs(appliedWidth - targetWidth) < 0.001F) {
        "Expected applied width $targetWidth but was $appliedWidth."
    }
    check(abs(appliedHeight - targetHeight) < 0.001F) {
        "Expected applied height $targetHeight but was $appliedHeight."
    }
    // highlight-android-verify-dimensions

    // highlight-android-template-switching
    val alternativeTemplateUri = Uri.fromFile(secondTemplateFile)
    engine.scene.applyTemplate(templateUri = alternativeTemplateUri)
    // highlight-android-template-switching

    val switchedScene = requireNotNull(engine.scene.get()) { "No scene loaded after switching templates." }
    val switchedWidth = engine.block.getFloat(
        block = switchedScene,
        property = "scene/pageDimensions/width",
    )
    val switchedHeight = engine.block.getFloat(
        block = switchedScene,
        property = "scene/pageDimensions/height",
    )
    check(abs(switchedWidth - targetWidth) < 0.001F) {
        "Expected switched width $targetWidth but was $switchedWidth."
    }
    check(abs(switchedHeight - targetHeight) < 0.001F) {
        "Expected switched height $targetHeight but was $switchedHeight."
    }

    // highlight-android-apply-from-string
    val templateString = firstTemplateString
    engine.scene.applyTemplate(template = templateString)
    // highlight-android-apply-from-string

    val stringScene = requireNotNull(engine.scene.get()) { "No scene loaded after applying the template string." }
    val stringWidth = engine.block.getFloat(
        block = stringScene,
        property = "scene/pageDimensions/width",
    )
    val stringHeight = engine.block.getFloat(
        block = stringScene,
        property = "scene/pageDimensions/height",
    )
    check(abs(stringWidth - targetWidth) < 0.001F) {
        "Expected string-applied width $targetWidth but was $stringWidth."
    }
    check(abs(stringHeight - targetHeight) < 0.001F) {
        "Expected string-applied height $targetHeight but was $stringHeight."
    }

    val previewPage = engine.scene.getPages().first()
    val previewPngData = engine.block.export(
        block = previewPage,
        mimeType = MimeType.PNG,
        options = ExportOptions(targetWidth = 360F, targetHeight = 640F),
    )

    return ApplyTemplate(
        uriWidth = appliedWidth,
        uriHeight = appliedHeight,
        switchedWidth = switchedWidth,
        switchedHeight = switchedHeight,
        stringWidth = stringWidth,
        stringHeight = stringHeight,
        serializedTemplateLength = firstTemplateString.length,
        previewPngData = previewPngData,
    )
}

// highlight-android-create-template
private suspend fun createTemplateString(
    engine: Engine,
    headlineText: String,
): String {
    val scene = engine.scene.create(designUnit = DesignUnit.PIXEL, fontSizeUnit = FontUnit.PIXEL)
    val page = engine.scene.getPages().firstOrNull() ?: engine.block.create(DesignBlockType.Page).also {
        engine.block.appendChild(parent = scene, child = it)
    }
    engine.block.setFloat(
        block = scene,
        property = "scene/pageDimensions/width",
        value = 600F,
    )
    engine.block.setFloat(
        block = scene,
        property = "scene/pageDimensions/height",
        value = 600F,
    )
    engine.block.setWidthMode(block = page, mode = SizeMode.ABSOLUTE)
    engine.block.setHeightMode(block = page, mode = SizeMode.ABSOLUTE)
    engine.block.setWidth(block = page, value = 600F)
    engine.block.setHeight(block = page, value = 600F)

    val headline = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(block = headline, text = headlineText)
    engine.block.setPositionX(block = headline, value = 72F)
    engine.block.setPositionY(block = headline, value = 96F)
    engine.block.setWidth(block = headline, value = 456F)
    engine.block.setHeight(block = headline, value = 120F)
    engine.block.appendChild(parent = page, child = headline)

    return engine.scene.saveToString(scene = scene)
}
// highlight-android-create-template
