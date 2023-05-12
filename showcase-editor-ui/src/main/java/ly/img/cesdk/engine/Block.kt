package ly.img.cesdk.engine

import androidx.annotation.StringRes
import ly.img.cesdk.core.components.VectorIcon
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
import ly.img.cesdk.editorui.R
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

data class Block(
    val designBlock: DesignBlock,
    val type: BlockType,
    val options: List<OptionItemData>
)

enum class BlockType(@StringRes val titleRes: Int) {
    Image(R.string.cesdk_image),
    Sticker(R.string.cesdk_sticker),
    Text(R.string.cesdk_text),
    Shape(R.string.cesdk_shape),
    Group(R.string.cesdk_group)
}

internal fun createBlock(designBlock: DesignBlock, engine: Engine): Block {
    val type = engine.block.getType(designBlock)
    val optionItems = mutableListOf<OptionItemData>()

    if ((type == DesignBlockType.IMAGE.key || type == DesignBlockType.STICKER.key) &&
        engine.block.isAllowedByScope(designBlock, "content/replace")
    ) {
        optionItems += OptionItemData(OptionType.Replace, R.string.cesdk_replace, VectorIcon(IconPack.Replace))
    }

    if (type == DesignBlockType.TEXT.key) {
        if (engine.block.isAllowedByScope(designBlock, "content/replace"))
            optionItems += OptionItemData(OptionType.Edit, R.string.cesdk_edit, VectorIcon(IconPack.Keyboard))
        if (engine.isStylingAllowed(designBlock))
            optionItems += OptionItemData(OptionType.Format, R.string.cesdk_format, VectorIcon(IconPack.Typeface))
    }

    if ((type == DesignBlockType.STAR_SHAPE.key || type == DesignBlockType.POLYGON_SHAPE.key || type == DesignBlockType.LINE_SHAPE.key) &&
        engine.isStylingAllowed(designBlock)
    ) {
        optionItems += OptionItemData(OptionType.ShapeOptions, R.string.cesdk_options, VectorIcon(IconPack.Tunevariant))
    }

    if ((type == DesignBlockType.TEXT.key || type == DesignBlockType.IMAGE.key || type.startsWith(SHAPES_PREFIX) ||
            type == DesignBlockType.VECTOR_PATH.key) && engine.isStylingAllowed(designBlock)
    ) {
        val hasStroke = engine.block.hasStroke(designBlock)
        val hasFill = engine.block.hasFill(designBlock)
        if (hasFill || hasStroke) {
            val fillColor = engine.getFillColor(designBlock)?.takeIf { engine.block.isFillEnabled(designBlock) }
            val strokeColor = engine.getStrokeColor(designBlock)?.takeIf { engine.block.isStrokeEnabled(designBlock) }
            optionItems += OptionItemData(
                OptionType.FillStroke,
                getFillStrokeTitleRes(hasFill, hasStroke),
                FillStrokeIcon(
                    hasFill = hasFill,
                    fillColor = fillColor,
                    hasStroke = hasStroke,
                    strokeColor = strokeColor
                )
            )
        }
    }

    if (engine.isStylingAllowed(designBlock) || engine.isMoveAllowed(designBlock) ||
        engine.isDeleteAllowed(designBlock) || engine.isDuplicateAllowed(designBlock)
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
            type == DesignBlockType.IMAGE.key -> BlockType.Image
            type == DesignBlockType.STICKER.key -> BlockType.Sticker
            type == DesignBlockType.TEXT.key -> BlockType.Text
            type.startsWith(SHAPES_PREFIX) || type == DesignBlockType.VECTOR_PATH.key -> BlockType.Shape
            type == DesignBlockType.GROUP.key -> BlockType.Group
            else -> throw UnsupportedOperationException()
        },
        options = optionItems
    )
}