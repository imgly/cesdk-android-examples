package ly.img.cesdk.editorui.handler

import android.net.Uri
import ly.img.cesdk.core.data.font.FontData
import ly.img.cesdk.core.data.font.FontFamilyData
import ly.img.cesdk.core.engine.FONT_BASE_PATH
import ly.img.cesdk.dock.options.format.HorizontalAlignment
import ly.img.cesdk.dock.options.format.VerticalAlignment
import ly.img.cesdk.core.ui.EventsHandler
import ly.img.cesdk.core.ui.inject
import ly.img.cesdk.core.ui.register
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.engine.SizeMode
import ly.img.cesdk.editorui.BlockEvent.*

/**
 * Register all events related to text block.
 * @param engine Lambda returning the engine instance
 * @param block Lambda returning the block instance
 * @param fontFamilyMap Lambda returning the font family map
 */
@Suppress("NAME_SHADOWING")
fun EventsHandler.textBlockEvents(
    engine: () -> Engine,
    block: () -> DesignBlock,
    fontFamilyMap: () -> Map<String, FontFamilyData>,
) {
    // Inject the dependencies
    val engine by inject(engine)
    val block by inject(block)
    val fontFamilyMap by inject(fontFamilyMap)

    fun onChangeFont(font: FontData) {
        engine.block.setString(block, "text/fontFileUri", Uri.parse("$FONT_BASE_PATH/${font.fontPath}").toString())
        engine.editor.addUndoStep()
    }
    fun onChangeFontStyle(fontFamily: String, bold: Boolean? = null, italicize: Boolean? = null) {
        val fontFamilyData = checkNotNull(fontFamilyMap[fontFamily])
        val currentFontData = requireNotNull(fontFamilyData.getFontData(engine.block.getString(block, "text/fontFileUri")))
        val font = fontFamilyData.getFontData(bold ?: currentFontData.isBold(), italicize ?: currentFontData.isItalic())
        onChangeFont(font)
    }

    register<OnChangeLineWidth> {
        engine.block.setWidth(block, engine.block.getFrameWidth(block))
        engine.block.setHeight(block, it.width)
    }

    register<OnChangeLineWidth> {
        engine.block.setWidth(block, engine.block.getFrameWidth(block))
        engine.block.setHeight(block, it.width)
    }

    register<OnBold> {
        onChangeFontStyle(it.fontFamily, bold = it.bold)
    }

    register<OnItalicize> {
        onChangeFontStyle(it.fontFamily, bold = it.italicize)
    }

    register<OnChangeFont> {
        onChangeFont(it.font)
    }

    register<OnChangeFontSize> {
        engine.block.setFloat(block, "text/fontSize", it.fontSize)
    }

    register<OnChangeHorizontalAlignment> {
        if (HorizontalAlignment.valueOf(engine.block.getEnum(block, "text/horizontalAlignment")) != it.alignment) {
            engine.block.setEnum(block, "text/horizontalAlignment", it.alignment.name)
            engine.editor.addUndoStep()
        }
    }

    register<OnChangeVerticalAlignment> {
        if (VerticalAlignment.valueOf(engine.block.getEnum(block, "text/verticalAlignment")) != it.alignment) {
            engine.block.setEnum(block, "text/verticalAlignment", it.alignment.name)
            engine.editor.addUndoStep()
        }
    }

    register<OnChangeLetterSpacing> {
        engine.block.setFloat(block, "text/letterSpacing", it.spacing)
    }

    register<OnChangeLineHeight> {
        engine.block.setFloat(block, "text/lineHeight", it.height)
    }

    register<OnChangeSizeMode> {
        val changedSizeMode = SizeMode.valueOf(it.sizeMode)
        if (engine.block.getHeightMode(block) != changedSizeMode) {
            when (changedSizeMode) {
                SizeMode.ABSOLUTE -> {
                    val width = engine.block.getFrameWidth(block)
                    val height = engine.block.getFrameHeight(block)
                    engine.block.setWidth(block, width)
                    engine.block.setHeight(block, height)
                }

                SizeMode.AUTO -> {
                    val width = engine.block.getFrameWidth(block)
                    engine.block.setWidth(block, width)
                }

                SizeMode.PERCENT -> throw UnsupportedOperationException()
            }
            engine.block.setHeightMode(block, changedSizeMode)
            engine.editor.addUndoStep()
        }
    }
}