package ly.img.cesdk.editorui

import ly.img.cesdk.components.VectorIcon
import ly.img.cesdk.core.engine.BlockKind
import ly.img.cesdk.core.engine.BlockType
import ly.img.cesdk.core.engine.getKindEnum
import ly.img.cesdk.core.iconpack.Croprotate
import ly.img.cesdk.core.iconpack.Groupenter
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.iconpack.Keyboard
import ly.img.cesdk.core.iconpack.Layersoutline
import ly.img.cesdk.core.iconpack.Replace
import ly.img.cesdk.core.iconpack.Selectgroup
import ly.img.cesdk.core.iconpack.Tunevariant
import ly.img.cesdk.core.iconpack.Typeface
import ly.img.cesdk.dock.FillStrokeIcon
import ly.img.cesdk.dock.OptionItemData
import ly.img.cesdk.dock.OptionType
import ly.img.cesdk.dock.options.fillstroke.getFillStrokeTitleRes
import ly.img.cesdk.core.engine.Scope
import ly.img.cesdk.engine.ShapeType
import ly.img.cesdk.engine.getFillInfo
import ly.img.cesdk.engine.getFillType
import ly.img.cesdk.engine.getShapeType
import ly.img.cesdk.engine.getStrokeColor
import ly.img.cesdk.engine.isDeleteAllowed
import ly.img.cesdk.engine.isDuplicateAllowed
import ly.img.cesdk.engine.isGrouped
import ly.img.cesdk.engine.isMoveAllowed
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

data class Block(
    val designBlock: DesignBlock,
    val type: BlockType,
    val options: List<OptionItemData>
)

internal fun createBlock(designBlock: DesignBlock, engine: Engine): Block {
    val type = engine.block.getType(designBlock)

    fun isGraphicBlock() = type == DesignBlockType.GRAPHIC.key
    val kind = if (isGraphicBlock()) engine.block.getKindEnum(designBlock) else null

    fun isImage() = isGraphicBlock() && kind == BlockKind.Image
    fun isShape() = isGraphicBlock() && kind == BlockKind.Shape
    fun isSticker() = isGraphicBlock() && kind == BlockKind.Sticker

    val shapeType = if (isShape()) engine.block.getShapeType(designBlock) else null

    val optionItems = mutableListOf<OptionItemData>()

    if ((isImage() || isSticker()) && engine.block.isAllowedByScope(designBlock, Scope.FillChange)) {
        optionItems += OptionItemData(OptionType.Replace, R.string.cesdk_replace, VectorIcon(IconPack.Replace))
    }

    if (isImage() && engine.block.hasCrop(designBlock) && engine.block.isAllowedByScope(designBlock, Scope.LayerCrop)) {
        optionItems += OptionItemData(OptionType.Crop, R.string.cesdk_crop, VectorIcon(IconPack.Croprotate))
    }

    if (type == DesignBlockType.TEXT.key) {
        if (engine.block.isAllowedByScope(designBlock, Scope.TextEdit))
            optionItems += OptionItemData(OptionType.Edit, R.string.cesdk_edit, VectorIcon(IconPack.Keyboard))
        if (engine.block.isAllowedByScope(designBlock, Scope.TextCharacter))
            optionItems += OptionItemData(OptionType.Format, R.string.cesdk_format, VectorIcon(IconPack.Typeface))
    }

    if ((shapeType == ShapeType.Star || shapeType == ShapeType.Polygon || shapeType == ShapeType.Line) &&
        engine.block.isAllowedByScope(designBlock, Scope.ShapeChange)
    ) {
        optionItems += OptionItemData(OptionType.ShapeOptions, R.string.cesdk_options, VectorIcon(IconPack.Tunevariant))
    }

    if (type == DesignBlockType.TEXT.key || type == DesignBlockType.PAGE.key || isShape() || isImage()) {
        val isStrokeChangeAllowed = engine.block.hasStroke(designBlock) &&
            engine.block.isAllowedByScope(designBlock, Scope.StrokeChange)
        val fillType = engine.block.getFillType(designBlock)
        val isFillChangeAllowed =
            (fillType == DesignBlockType.COLOR_FILL || fillType == DesignBlockType.LINEAR_GRADIENT_FILL || fillType == DesignBlockType.RADIAL_GRADIENT_FILL || fillType == DesignBlockType.CONICAL_GRADIENT_FILL) &&
                engine.block.isAllowedByScope(designBlock, Scope.FillChange)
        if (isFillChangeAllowed || isStrokeChangeAllowed) {
            val fill = engine.block.getFillInfo(designBlock)?.takeIf { engine.block.isFillEnabled(designBlock) }
            val strokeColor = engine.getStrokeColor(designBlock)?.takeIf { engine.block.isStrokeEnabled(designBlock) }
            optionItems += OptionItemData(
                OptionType.FillStroke,
                getFillStrokeTitleRes(isFillChangeAllowed, isStrokeChangeAllowed),
                FillStrokeIcon(
                    hasFill = isFillChangeAllowed,
                    fill = fill,
                    hasStroke = isStrokeChangeAllowed,
                    strokeColor = strokeColor
                )
            )
        }
    }

    if (type != DesignBlockType.PAGE.key &&
        (engine.block.isAllowedByScope(designBlock, Scope.LayerBlendMode) ||
            engine.block.isAllowedByScope(designBlock, Scope.LayerOpacity) ||
            engine.isMoveAllowed(designBlock) || engine.isDeleteAllowed(designBlock) || engine.isDuplicateAllowed(designBlock))
    ) {
        optionItems += OptionItemData(OptionType.Layer, R.string.cesdk_layer, VectorIcon(IconPack.Layersoutline))
    }

    if (type == DesignBlockType.GROUP.key) {
        optionItems += OptionItemData(OptionType.EnterGroup, R.string.cesdk_enter_group, VectorIcon(IconPack.Groupenter))
    }

    if (engine.isGrouped(designBlock)) {
        optionItems += OptionItemData(
            OptionType.SelectGroup,
            R.string.cesdk_select_group,
            VectorIcon(IconPack.Selectgroup)
        )
    }

    return Block(
        designBlock = designBlock,
        type = when {
            isImage() -> BlockType.Image
            isSticker() -> BlockType.Sticker
            type == DesignBlockType.TEXT.key -> BlockType.Text
            isShape() -> BlockType.Shape
            type == DesignBlockType.GROUP.key -> BlockType.Group
            type == DesignBlockType.PAGE.key -> BlockType.Page
            else -> throw UnsupportedOperationException()
        },
        options = optionItems
    )
}