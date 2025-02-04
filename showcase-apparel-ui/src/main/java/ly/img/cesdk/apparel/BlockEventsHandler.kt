package ly.img.cesdk.apparel

import android.net.Uri
import androidx.compose.ui.graphics.Color
import ly.img.cesdk.dock.options.format.HorizontalAlignment
import ly.img.cesdk.dock.options.format.VerticalAlignment
import ly.img.cesdk.engine.FONT_BASE_PATH
import ly.img.cesdk.engine.bringForward
import ly.img.cesdk.engine.bringToTop
import ly.img.cesdk.engine.delete
import ly.img.cesdk.engine.duplicate
import ly.img.cesdk.engine.sendBackward
import ly.img.cesdk.engine.sendToBottom
import ly.img.cesdk.engine.toEngineColor
import ly.img.cesdk.library.data.font.FontData
import ly.img.cesdk.library.data.font.FontFamilyData
import ly.img.engine.BlendMode
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.engine.SizeMode
import ly.img.engine.StrokeCornerGeometry
import ly.img.engine.StrokePosition
import ly.img.engine.StrokeStyle
import kotlin.math.exp

fun handleBlockEvent(engine: Engine, block: DesignBlock, fontFamilyMap: Map<String, FontFamilyData>, event: BlockEvent) {
    when (event) {
        BlockEvent.OnDelete -> engine.delete(block)
        BlockEvent.OnDown -> engine.sendBackward(block)
        BlockEvent.OnDuplicate -> engine.duplicate(block)
        BlockEvent.OnUp -> engine.bringForward(block)
        BlockEvent.ToBottom -> engine.sendToBottom(block)
        BlockEvent.ToTop -> engine.bringToTop(block)
        BlockEvent.OnChangeFinish -> engine.editor.addUndoStep()
        is BlockEvent.OnChangeBlendMode -> onChangeBlendMode(engine, block, event.blendMode)
        is BlockEvent.OnChangeOpacity -> engine.block.setOpacity(block, event.opacity)
        is BlockEvent.OnChangeFillColor -> onChangeFillColor(engine, block, event.color)
        BlockEvent.OnDisableFill -> onDisableFill(engine, block)
        is BlockEvent.OnChangeStrokeColor -> onChangeStrokeColor(engine, block, event.color)
        BlockEvent.OnDisableStroke -> onDisableStroke(engine, block)
        is BlockEvent.OnChangeStrokeJoin -> onChangeStrokeJoin(engine, block, event.join)
        is BlockEvent.OnChangeStrokePosition -> onChangeStrokePosition(engine, block, event.position)
        is BlockEvent.OnChangeStrokeStyle -> onChangeStrokeStyle(engine, block, event.style)
        is BlockEvent.OnChangeStrokeWidth -> engine.block.setStrokeWidth(block, exp(event.width.toDouble()).toFloat())
        is BlockEvent.OnChangeStarInnerDiameter -> engine.block.setFloat(block, "shapes/star/innerDiameter", event.diameter)
        is BlockEvent.OnChangeStarPoints -> engine.block.setInt(block, "shapes/star/points", event.points.toInt())
        is BlockEvent.OnChangePolygonSides -> engine.block.setInt(block, "shapes/polygon/sides", event.sides.toInt())
        is BlockEvent.OnChangeLineWidth -> onChangeLineWidth(engine, block, event.width)
        is BlockEvent.OnBold -> onBold(engine, block, fontFamilyMap, event.fontFamily, event.bold)
        is BlockEvent.OnItalicize -> onItalicize(engine, block, fontFamilyMap, event.fontFamily, event.italicize)
        is BlockEvent.OnChangeFont -> onChangeFont(engine, block, event.font)
        is BlockEvent.OnChangeFontSize -> onChangeFontSize(engine, block, event.fontSize)
        is BlockEvent.OnChangeHorizontalAlignment -> onChangeHorizontalAlignment(engine, block, event.alignment)
        is BlockEvent.OnChangeVerticalAlignment -> onChangeVerticalAlignment(engine, block, event.alignment)
        is BlockEvent.OnChangeLetterSpacing -> engine.block.setFloat(block, "text/letterSpacing", event.spacing)
        is BlockEvent.OnChangeLineHeight -> engine.block.setFloat(block, "text/lineHeight", event.height)
        is BlockEvent.OnChangeSizeMode -> onChangeSizeMode(engine, block, event.sizeMode)
    }
}

private fun onItalicize(
    engine: Engine,
    block: DesignBlock,
    fontFamilyMap: Map<String, FontFamilyData>,
    fontFamily: String,
    italicize: Boolean
) {
    val fontFamilyData = checkNotNull(fontFamilyMap[fontFamily])
    val currentFontData = requireNotNull(fontFamilyData.getFontData(engine.block.getString(block, "text/fontFileUri")))
    val font = fontFamilyData.getFontData(currentFontData.isBold(), italicize)
    onChangeFont(engine, block, font)
}

private fun onChangeLineWidth(engine: Engine, block: DesignBlock, width: Float) {
    engine.block.setWidth(block, engine.block.getFrameWidth(block))
    engine.block.setHeight(block, width)
}

private fun onBold(engine: Engine, block: DesignBlock, fontFamilyMap: Map<String, FontFamilyData>, fontFamily: String, bold: Boolean) {
    val fontFamilyData = checkNotNull(fontFamilyMap[fontFamily])
    val currentFontData = requireNotNull(fontFamilyData.getFontData(engine.block.getString(block, "text/fontFileUri")))
    val font = fontFamilyData.getFontData(bold, currentFontData.isItalic())
    onChangeFont(engine, block, font)
}

private fun onChangeFont(engine: Engine, block: DesignBlock, font: FontData) {
    engine.block.setString(block, "text/fontFileUri", Uri.parse("$FONT_BASE_PATH/${font.fontPath}").toString())
    engine.editor.addUndoStep()
}

private fun onChangeFontSize(engine: Engine, block: DesignBlock, fontSize: Float) {
    engine.block.setFloat(block, "text/fontSize", fontSize)
}

private fun onChangeVerticalAlignment(engine: Engine, block: DesignBlock, alignment: VerticalAlignment) {
    if (VerticalAlignment.valueOf(engine.block.getEnum(block, "text/verticalAlignment")) == alignment) return
    engine.block.setEnum(block, "text/verticalAlignment", alignment.name)
    engine.editor.addUndoStep()
}

private fun onChangeHorizontalAlignment(engine: Engine, block: DesignBlock, alignment: HorizontalAlignment) {
    if (HorizontalAlignment.valueOf(engine.block.getEnum(block, "text/horizontalAlignment")) == alignment) return
    engine.block.setEnum(block, "text/horizontalAlignment", alignment.name)
    engine.editor.addUndoStep()
}

private fun onChangeSizeMode(engine: Engine, block: DesignBlock, sizeMode: String) {
    val changedSizeMode = SizeMode.valueOf(sizeMode)
    if (engine.block.getHeightMode(block) == changedSizeMode) return
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

private fun onChangeStrokeStyle(engine: Engine, block: DesignBlock, style: String) {
    val strokeStyleEnum = StrokeStyle.valueOf(style)
    if (engine.block.getStrokeStyle(block) == strokeStyleEnum) return
    engine.block.setStrokeStyle(block, strokeStyleEnum)
    engine.editor.addUndoStep()
}

private fun onChangeStrokePosition(engine: Engine, block: DesignBlock, position: String) {
    val strokePositionEnum = StrokePosition.valueOf(position)
    if (engine.block.getStrokePosition(block) == strokePositionEnum) return
    engine.block.setStrokePosition(block, strokePositionEnum)
    engine.editor.addUndoStep()
}

private fun onChangeStrokeJoin(engine: Engine, block: DesignBlock, join: String) {
    val strokeJoinEnum = StrokeCornerGeometry.valueOf(join)
    if (engine.block.getStrokeCornerGeometry(block) == strokeJoinEnum) return
    engine.block.setStrokeCornerGeometry(block, strokeJoinEnum)
    engine.editor.addUndoStep()
}

private fun onDisableStroke(engine: Engine, block: DesignBlock) {
    val isEnabled = engine.block.isStrokeEnabled(block)
    if (!isEnabled) return
    engine.block.setStrokeEnabled(block, false)
    engine.editor.addUndoStep()
}

private fun onChangeStrokeColor(engine: Engine, block: DesignBlock, color: Color) {
    engine.block.setStrokeEnabled(block, true)
    engine.block.setStrokeColor(block, color.toEngineColor())
}

private fun onDisableFill(engine: Engine, block: DesignBlock) {
    val isEnabled = engine.block.isFillEnabled(block)
    if (!isEnabled) return
    engine.block.setFillEnabled(block, false)
    engine.editor.addUndoStep()
}

private fun onChangeFillColor(engine: Engine, block: DesignBlock, color: Color) {
    engine.block.setFillEnabled(block, true)
    engine.block.setFillSolidColor(block, color.toEngineColor())
}

private fun onChangeBlendMode(engine: Engine, designBlock: DesignBlock, blendMode: BlendMode) {
    if (engine.block.getBlendMode(designBlock) == blendMode) return
    engine.block.setBlendMode(designBlock, blendMode)
    engine.editor.addUndoStep()
}