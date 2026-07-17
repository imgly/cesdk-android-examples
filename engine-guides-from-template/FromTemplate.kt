import android.net.Uri
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.Font
import ly.img.engine.FontStyle
import ly.img.engine.FontWeight
import ly.img.engine.MimeType
import ly.img.engine.Typeface
import java.nio.ByteBuffer

data class FromTemplate(
    val pageCount: Int,
    val textBlockCount: Int,
    val customizedText: String?,
    val previewPngData: ByteBuffer,
)

suspend fun fromTemplate(engine: Engine): FromTemplate {
    // highlight-android-load-from-url
    val templateUri = Uri.parse(
        "https://cdn.img.ly/packages/imgly/cesdk-android/1.79.0-rc.0/assets/ly.img.templates/templates/cesdk_business_card_1.scene",
    )
    val scene = engine.scene.load(sceneUri = templateUri, waitForResources = true)
    // highlight-android-load-from-url

    check(scene == engine.scene.get())

    // highlight-android-load-from-string
    val templateString = engine.scene.saveToString(scene = scene)
    engine.scene.load(scene = templateString, waitForResources = true)
    // highlight-android-load-from-string

    // highlight-android-apply-template
    val replacementTemplateUri = Uri.parse(
        "https://cdn.img.ly/packages/imgly/cesdk-android/1.79.0-rc.0/assets/ly.img.templates/templates/cesdk_business_card_1.scene",
    )
    engine.scene.applyTemplate(templateUri = replacementTemplateUri)
    // highlight-android-apply-template

    // highlight-android-modify-content
    val firstTextBlock = engine.block.findByType(DesignBlockType.Text).firstOrNull()
    if (firstTextBlock != null) {
        engine.block.replaceText(block = firstTextBlock, text = "Your Company")
    }
    // highlight-android-modify-content

    val page = engine.block.findByType(DesignBlockType.Page).first()
    val exportTypeface = Typeface(
        name = "Fira Sans",
        fonts = listOf(
            Font(
                uri = Uri.parse("file:///android_asset/imgly-assets/ly.img.typeface/fonts/FiraSans/FiraSans-Regular.ttf"),
                subFamily = "Regular",
                weight = FontWeight.NORMAL,
                style = FontStyle.NORMAL,
            ),
        ),
    )
    val exportFont = exportTypeface.fonts.first()
    engine.block.findByType(DesignBlockType.Text).forEach { textBlock ->
        engine.block.setFont(block = textBlock, fontFileUri = exportFont.uri, typeface = exportTypeface)
    }
    engine.block.forceLoadResources(blocks = listOf(page))
    val pageWidth = engine.block.getWidth(page)
    val pageHeight = engine.block.getHeight(page)
    val previewPngData = engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
        options = ExportOptions(
            targetWidth = 1200F,
            targetHeight = 1200F * pageHeight / pageWidth,
        ),
    )

    return FromTemplate(
        pageCount = engine.block.findByType(DesignBlockType.Page).size,
        textBlockCount = engine.block.findByType(DesignBlockType.Text).size,
        customizedText = firstTextBlock?.let { engine.block.getString(block = it, property = "text/text") },
        previewPngData = previewPngData,
    )
}
