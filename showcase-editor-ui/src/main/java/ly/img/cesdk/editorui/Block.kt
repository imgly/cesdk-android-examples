package ly.img.cesdk.editorui

import ly.img.cesdk.components.VectorIcon
import ly.img.cesdk.core.engine.BlockKind
import ly.img.cesdk.core.engine.BlockType
import ly.img.engine.DesignBlockType
import ly.img.engine.FillType
import ly.img.cesdk.core.engine.Scope
import ly.img.engine.ShapeType
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
import ly.img.cesdk.core.ui.tab_item.TabIcon
import ly.img.cesdk.engine.getFillInfo
import ly.img.cesdk.engine.getFillType
import ly.img.cesdk.core.iconpack.Adjustments
import ly.img.cesdk.core.iconpack.Blur
import ly.img.cesdk.core.iconpack.Effect
import ly.img.cesdk.core.iconpack.Filter
import ly.img.cesdk.dock.options.fillstroke.FillStrokeUiState
import ly.img.cesdk.engine.getStrokeColor
import ly.img.cesdk.engine.isDeleteAllowed
import ly.img.cesdk.engine.isDuplicateAllowed
import ly.img.cesdk.engine.isGrouped
import ly.img.cesdk.engine.isMoveAllowed
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.cesdk.dock.OptionType.*

data class Block(
    val designBlock: DesignBlock,
    val type: BlockType,
    val options: List<OptionItemData>
)

private val shapesWithOptions = arrayOf(ShapeType.Star, ShapeType.Polygon, ShapeType.Line)
internal fun createBlock(designBlock: DesignBlock, engine: Engine): Block {
    val type = DesignBlockType.getOrNull(engine.block.getType(designBlock))
    val isGraphicBlock = type == DesignBlockType.Graphic
    val kind = if (isGraphicBlock) engine.block.getKindEnum(designBlock) else null
    val shapeType = if (kind == BlockKind.Shape) ShapeType.get(engine.block.getType(engine.block.getShape(designBlock))) else null

    val optionItems = mutableListOf<OptionItemData>()

    /* Check if an operation is allowed by scope */
    fun isAllowedByScope(scope: String) = engine.block.isAllowedByScope(designBlock, scope)
    /* Add options entry based on optionType type */
    infix fun OptionType.addIf(enabled: Boolean) {
        if (enabled) {
            fun addOption(titleRes: Int, icon: TabIcon) {
                optionItems += OptionItemData(this, titleRes, icon)
            }
            when (this) {
                Crop -> addOption(R.string.cesdk_crop, VectorIcon(IconPack.Croprotate))
                Edit -> addOption(R.string.cesdk_edit, VectorIcon(IconPack.Keyboard))
                Layer -> addOption(R.string.cesdk_layer, VectorIcon(IconPack.Layersoutline))
                Filter -> addOption(R.string.cesdk_filter, VectorIcon(IconPack.Filter))
                Effect -> addOption(R.string.cesdk_effect, VectorIcon(IconPack.Effect))
                Blur -> addOption(R.string.cesdk_blur, VectorIcon(IconPack.Blur))
                Format -> addOption(R.string.cesdk_format, VectorIcon(IconPack.Typeface))
                Replace -> addOption(R.string.cesdk_replace, VectorIcon(IconPack.Replace))
                EnterGroup -> addOption(R.string.cesdk_enter_group, VectorIcon(IconPack.Groupenter))
                FillStroke -> {
                    val hasStroke = engine.block.hasStroke(designBlock)
                    val fillType = engine.block.getFillType(designBlock)
                    val hasSolidOrGradientFill = (
                        fillType == FillType.Color
                            || fillType == FillType.LinearGradient
                            || fillType == FillType.RadialGradient
                            || fillType == FillType.ConicalGradient
                        )
                    if (hasSolidOrGradientFill || hasStroke) {
                        addOption(
                            FillStrokeUiState.getFillStrokeTitleRes(hasSolidOrGradientFill, hasStroke),
                            FillStrokeIcon(
                                fill = engine.block.getFillInfo(designBlock)?.takeIf { engine.block.isFillEnabled(designBlock) },
                                hasFill = hasSolidOrGradientFill,
                                hasStroke = hasStroke,
                                strokeColor = engine.getStrokeColor(designBlock)?.takeIf { engine.block.isStrokeEnabled(designBlock) },
                            )
                        )
                    }
                }
                SelectGroup -> addOption(R.string.cesdk_select_group, VectorIcon(IconPack.Selectgroup))
                Adjustments -> addOption(R.string.cesdk_adjustment, VectorIcon(IconPack.Adjustments))
                ShapeOptions -> addOption(R.string.cesdk_options, VectorIcon(IconPack.Tunevariant))
            }
        }
    }

    val hasLayerOption =
        type != DesignBlockType.Page
            && (isAllowedByScope(Scope.LayerBlendMode)
            || isAllowedByScope(Scope.LayerOpacity)
            || engine.isMoveAllowed(designBlock)
            || engine.isDeleteAllowed(designBlock)
            || engine.isDuplicateAllowed(designBlock)
            )

    /* Add options entry based on block type and kind */
    when (kind) {
        BlockKind.Image -> {
            Replace addIf isAllowedByScope(Scope.FillChange)
            Crop addIf (engine.block.hasCrop(designBlock) && isAllowedByScope(Scope.LayerCrop))
            FillStroke addIf isAllowedByScope(Scope.StrokeChange)
            Adjustments addIf isAllowedByScope(Scope.AppearanceAdjustment)
            Filter addIf isAllowedByScope(Scope.AppearanceFilter)
            Effect addIf isAllowedByScope(Scope.AppearanceEffect)
            Blur addIf isAllowedByScope(Scope.AppearanceBlur)
            Layer addIf hasLayerOption
            SelectGroup addIf engine.isGrouped(designBlock)
        }

        BlockKind.Sticker -> {
            Replace addIf isAllowedByScope(Scope.FillChange)
            Layer addIf hasLayerOption
            SelectGroup addIf engine.isGrouped(designBlock)
        }

        BlockKind.Shape -> {
            ShapeOptions addIf (shapeType in shapesWithOptions && isAllowedByScope(Scope.ShapeChange))
            FillStroke addIf (isAllowedByScope(Scope.FillChange) || isAllowedByScope(Scope.StrokeChange))
            Layer addIf hasLayerOption
            SelectGroup addIf engine.isGrouped(designBlock)
        }
        null -> null // Using null instead of else, to force a compile error if a new kind is added.
    } ?: when(type) {
        DesignBlockType.Text -> {
            Edit addIf isAllowedByScope(Scope.TextEdit)
            Format addIf isAllowedByScope(Scope.TextCharacter)
            FillStroke addIf (isAllowedByScope(Scope.FillChange) || isAllowedByScope(Scope.StrokeChange))
            Layer addIf hasLayerOption
            SelectGroup addIf engine.isGrouped(designBlock)
        }

        DesignBlockType.Group -> {
            EnterGroup addIf true
            Layer addIf hasLayerOption
            SelectGroup addIf engine.isGrouped(designBlock)
        }

        DesignBlockType.Page -> {
            FillStroke addIf (isAllowedByScope(Scope.FillChange) || isAllowedByScope(Scope.StrokeChange))
            SelectGroup addIf engine.isGrouped(designBlock)
        }

        else -> throw UnsupportedOperationException()
    }

    return Block(
        designBlock = designBlock,
        type = when (kind) {
            BlockKind.Image -> BlockType.Image
            BlockKind.Shape -> BlockType.Shape
            BlockKind.Sticker -> BlockType.Sticker
            null -> null // Using null instead of else, to force a compile error if a new kind is added.
        } ?: when (type) {
            DesignBlockType.Text -> BlockType.Text
            DesignBlockType.Page -> BlockType.Page
            DesignBlockType.Group -> BlockType.Group
            else -> throw UnsupportedOperationException()
        },
        options = optionItems
    )
}