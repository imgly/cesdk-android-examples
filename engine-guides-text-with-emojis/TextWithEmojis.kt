import android.net.Uri
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import java.nio.ByteBuffer

suspend fun textWithEmojis(
    engine: Engine,
    appAssetBaseUri: Uri,
): TextWithEmojisResult {
    val originalEmojiFontUri = engine.editor.getSettingString(keypath = "defaultEmojiFontFileUri")
    val originalForceSystemEmojis = engine.editor.getSettingBoolean(keypath = "forceSystemEmojis")

    try {
        // highlight-android-default-emoji-font
        val configuredEmojiFontUri = engine.editor.getSettingString(
            keypath = "defaultEmojiFontFileUri",
        )
        val assetBaseUri = engine.editor
            .getSettingString(keypath = "basePath")
            .trimEnd('/')
        val defaultEmojiFontUri = "$assetBaseUri/emoji/NotoColorEmoji.ttf"
        // highlight-android-default-emoji-font

        // highlight-android-configure-emoji-font
        val emojiFontUri = appAssetBaseUri
            .buildUpon()
            .appendPath("emoji")
            .appendPath("NotoColorEmoji.ttf")
            .build()
            .toString()

        engine.editor.setSettingString(
            keypath = "defaultEmojiFontFileUri",
            value = emojiFontUri,
        )
        // highlight-android-configure-emoji-font

        // highlight-android-force-system-emojis
        engine.editor.setSettingBoolean(
            keypath = "forceSystemEmojis",
            value = true,
        )
        val forceSystemEmojis = engine.editor.getSettingBoolean(
            keypath = "forceSystemEmojis",
        )
        // highlight-android-force-system-emojis

        val scene = engine.scene.create()

        val page = engine.block.create(DesignBlockType.Page)
        engine.block.setWidth(page, value = 1000F)
        engine.block.setHeight(page, value = 640F)
        engine.block.appendChild(parent = scene, child = page)

        val background = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(background, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setWidth(background, value = 1000F)
        engine.block.setHeight(background, value = 640F)
        engine.block.setFill(background, fill = engine.block.createFill(FillType.Color))
        engine.block.setFillSolidColor(
            block = background,
            color = Color.fromHex("#FFF8FAFC"),
        )
        engine.block.appendChild(parent = page, child = background)

        // highlight-android-create-text-with-emojis
        val primaryText = "Emoji text 🎉✨"
        val emojiTextBlock = engine.block.create(DesignBlockType.Text)
        engine.block.appendChild(parent = page, child = emojiTextBlock)
        engine.block.replaceText(block = emojiTextBlock, text = primaryText)
        engine.block.setTextFontSize(block = emojiTextBlock, fontSize = 16F)
        engine.block.setWidth(block = emojiTextBlock, value = 880F)
        engine.block.setWidthMode(block = emojiTextBlock, mode = SizeMode.ABSOLUTE)
        engine.block.setHeightMode(block = emojiTextBlock, mode = SizeMode.AUTO)
        engine.block.setPositionX(block = emojiTextBlock, value = 60F)
        engine.block.setPositionY(block = emojiTextBlock, value = 64F)
        // highlight-android-create-text-with-emojis

        // highlight-android-emoji-examples
        val emojiExamples = listOf(
            "Single: 😀 🎉",
            "Flags: 🇩🇪 🇺🇸",
            "Family: 👨‍👩‍👧",
            "Tone: 👋🏽 👍🏾",
        )

        emojiExamples.forEachIndexed { index, text ->
            val textBlock = engine.block.create(DesignBlockType.Text)
            engine.block.appendChild(parent = page, child = textBlock)
            engine.block.replaceText(block = textBlock, text = text)
            engine.block.setTextFontSize(block = textBlock, fontSize = 12F)
            engine.block.setWidthMode(block = textBlock, mode = SizeMode.AUTO)
            engine.block.setHeightMode(block = textBlock, mode = SizeMode.AUTO)
            engine.block.setPositionX(block = textBlock, value = 60F)
            engine.block.setPositionY(block = textBlock, value = 190F + index * 72F)
        }
        // highlight-android-emoji-examples

        val pngData = engine.block.export(block = page, mimeType = MimeType.PNG)
        check(pngData.hasRemaining()) { "Text with emojis PNG export is empty." }
        val pngBytes = ByteArray(pngData.remaining())
        pngData.asReadOnlyBuffer().get(pngBytes)

        return TextWithEmojisResult(
            configuredEmojiFontUri = configuredEmojiFontUri,
            defaultEmojiFontUri = defaultEmojiFontUri,
            activeEmojiFontUri = engine.editor.getSettingString(keypath = "defaultEmojiFontFileUri"),
            forceSystemEmojis = forceSystemEmojis,
            primaryText = primaryText,
            emojiExamples = emojiExamples,
            exportedPng = ByteBuffer.wrap(pngBytes).asReadOnlyBuffer(),
        )
    } finally {
        engine.editor.setSettingString(
            keypath = "defaultEmojiFontFileUri",
            value = originalEmojiFontUri,
        )
        engine.editor.setSettingBoolean(
            keypath = "forceSystemEmojis",
            value = originalForceSystemEmojis,
        )
    }
}
