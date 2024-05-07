package ly.img.editor.base.ui.handler

import android.net.Uri
import ly.img.editor.base.dock.options.format.HorizontalAlignment
import ly.img.editor.base.dock.options.format.VerticalAlignment
import ly.img.editor.base.ui.BlockEvent.OnBoldToggle
import ly.img.editor.base.ui.BlockEvent.OnChangeFont
import ly.img.editor.base.ui.BlockEvent.OnChangeFontSize
import ly.img.editor.base.ui.BlockEvent.OnChangeHorizontalAlignment
import ly.img.editor.base.ui.BlockEvent.OnChangeLetterSpacing
import ly.img.editor.base.ui.BlockEvent.OnChangeLineHeight
import ly.img.editor.base.ui.BlockEvent.OnChangeLineWidth
import ly.img.editor.base.ui.BlockEvent.OnChangeSizeMode
import ly.img.editor.base.ui.BlockEvent.OnChangeVerticalAlignment
import ly.img.editor.base.ui.BlockEvent.OnItalicToggle
import ly.img.editor.core.ui.EventsHandler
import ly.img.editor.core.ui.inject
import ly.img.editor.core.ui.register
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.engine.SizeMode
import ly.img.engine.Typeface

/**
 * Register all events related to text block.
 * @param engine Lambda returning the engine instance
 * @param block Lambda returning the block instance
 */
@Suppress("NAME_SHADOWING")
fun EventsHandler.textBlockEvents(
    engine: () -> Engine,
    block: () -> DesignBlock,
) {
    // Inject the dependencies
    val engine by inject(engine)
    val block by inject(block)

    fun onChangeFont(
        fontUri: Uri,
        typeface: Typeface,
    ) {
        engine.block.setFont(
            block = block,
            fontFileUri = fontUri,
            typeface = typeface,
        )
        engine.editor.addUndoStep()
    }

    fun onBoldToggle() {
        engine.block.toggleBoldFont(block = block)
        engine.editor.addUndoStep()
    }

    fun onItalicToggle() {
        engine.block.toggleItalicFont(block = block)
        engine.editor.addUndoStep()
    }

    register<OnChangeLineWidth> {
        engine.block.setWidth(block, engine.block.getFrameWidth(block))
        engine.block.setHeight(block, it.width)
    }

    register<OnChangeLineWidth> {
        engine.block.setWidth(block, engine.block.getFrameWidth(block))
        engine.block.setHeight(block, it.width)
    }

    register<OnBoldToggle> {
        onBoldToggle()
    }

    register<OnItalicToggle> {
        onItalicToggle()
    }

    register<OnChangeFont> {
        onChangeFont(it.fontUri, it.typeface)
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
