import android.util.Log
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.EngineErrorCode
import ly.img.engine.EngineException
import ly.img.engine.ListStyle
import ly.img.engine.SizeMode

private const val TAG = "CopyPaste"

data class CopyPaste(
    val copiedHtml: String,
    val source: DesignBlock,
    val target: DesignBlock,
    val headline: DesignBlock,
    val pasted: DesignBlock,
    val note: DesignBlock,
)

fun copyPaste(engine: Engine): CopyPaste {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 800F)
    engine.block.setHeight(block = page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    // Five auto-sized text blocks, one below the other
    val (source, target, headline, pasted, note) = listOf(
        "Summer Sale\nNew colors\nFree shipping" to 40F,
        "" to 200F,
        "Today: " to 360F,
        "" to 440F,
        "Caption" to 520F,
    ).map { (text, y) ->
        val block = engine.block.create(DesignBlockType.Text)
        engine.block.appendChild(parent = page, child = block)
        engine.block.setWidthMode(block = block, mode = SizeMode.AUTO)
        engine.block.setHeightMode(block = block, mode = SizeMode.AUTO)
        engine.block.setPositionX(block = block, value = 40F)
        engine.block.setPositionY(block = block, value = y)
        engine.block.replaceText(block = block, text = text)
        block
    }

    // The source block carries a red title, an underlined word and a bulleted list.
    engine.block.setTextColor(
        block = source,
        color = Color.fromRGBA(r = 1F, g = 0F, b = 0F, a = 1F),
        from = 0,
        to = 11,
    )
    engine.block.toggleTextDecorationUnderline(block = source, from = 12, to = 22)
    engine.block.setTextListStyle(block = source, listStyle = ListStyle.UNORDERED, paragraphIndex = 1)
    engine.block.setTextListStyle(block = source, listStyle = ListStyle.UNORDERED, paragraphIndex = 2)

    // highlight-android-copy-text
    // Copy the whole text with every run and paragraph format
    val copiedHtml = engine.block.getTextAsHTML(block = source)
    // highlight-android-copy-text

    // highlight-android-paste-text
    // Replace the whole text of the target block with the copied text
    engine.block.replaceTextWithHTML(block = target, html = copiedHtml)
    // highlight-android-paste-text

    // highlight-android-copy-range
    // Copy only "Summer Sale" and insert it at the end of the headline
    val titleHtml = engine.block.getTextAsHTML(block = source, from = 0, to = 11)
    engine.block.replaceTextWithHTML(block = headline, html = titleHtml, from = 7, to = 7)
    // highlight-android-copy-range

    // highlight-android-paste-html
    // HTML as a browser or a document app puts it on the clipboard, with its font size and line height
    val clipboardHtml =
        """<p style="line-height:1.5"><b style="font-size:24px">Bold</b> and <span style="color:#ff0000">red</span></p>""" +
            """<ul><li>item</li></ul>"""
    engine.block.replaceTextWithHTML(block = pasted, html = clipboardHtml)
    // highlight-android-paste-html

    // highlight-android-fallback
    // A copied image puts HTML without text on the clipboard
    val imageHtml = """<p><img src="https://example.com/photo.png"></p>"""
    val imagePlainText = "Beach photo"
    try {
        engine.block.replaceTextWithHTML(block = note, html = imageHtml)
    } catch (error: EngineException) {
        if (error.code != EngineErrorCode.BLOCK_TEXT_HTML_HAS_NO_TEXT) throw error
        // The block is unchanged, so insert the plain text of the clipboard instead
        engine.block.replaceText(block = note, text = imagePlainText)
    }
    // highlight-android-fallback

    Log.i(TAG, "Copied HTML: $copiedHtml")

    return CopyPaste(
        copiedHtml = copiedHtml,
        source = source,
        target = target,
        headline = headline,
        pasted = pasted,
        note = note,
    )
}
