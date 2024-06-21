package ly.img.editor.base.ui

import ly.img.editor.base.R
import ly.img.editor.base.components.VectorIcon
import ly.img.editor.base.dock.FillStrokeIcon
import ly.img.editor.base.dock.OptionItemData
import ly.img.editor.base.dock.OptionType
import ly.img.editor.base.dock.OptionType.Adjustments
import ly.img.editor.base.dock.OptionType.Blur
import ly.img.editor.base.dock.OptionType.Crop
import ly.img.editor.base.dock.OptionType.Delete
import ly.img.editor.base.dock.OptionType.Duplicate
import ly.img.editor.base.dock.OptionType.Edit
import ly.img.editor.base.dock.OptionType.Effect
import ly.img.editor.base.dock.OptionType.EnterGroup
import ly.img.editor.base.dock.OptionType.FillStroke
import ly.img.editor.base.dock.OptionType.Filter
import ly.img.editor.base.dock.OptionType.Format
import ly.img.editor.base.dock.OptionType.Layer
import ly.img.editor.base.dock.OptionType.Replace
import ly.img.editor.base.dock.OptionType.SelectGroup
import ly.img.editor.base.dock.OptionType.ShapeOptions
import ly.img.editor.base.dock.options.fillstroke.FillStrokeUiState
import ly.img.editor.base.engine.getFillType
import ly.img.editor.base.engine.isDeleteAllowed
import ly.img.editor.base.engine.isDuplicateAllowed
import ly.img.editor.base.engine.isFillStrokeSupported
import ly.img.editor.base.engine.isGrouped
import ly.img.editor.base.engine.isMoveAllowed
import ly.img.editor.core.ui.ColorSchemeKeyToken
import ly.img.editor.core.ui.engine.BlockKind
import ly.img.editor.core.ui.engine.BlockType
import ly.img.editor.core.ui.engine.Scope
import ly.img.editor.core.ui.engine.getKindEnum
import ly.img.editor.core.ui.iconpack.Adjustments
import ly.img.editor.core.ui.iconpack.Blur
import ly.img.editor.core.ui.iconpack.Croprotate
import ly.img.editor.core.ui.iconpack.Delete
import ly.img.editor.core.ui.iconpack.Duplicate
import ly.img.editor.core.ui.iconpack.Effect
import ly.img.editor.core.ui.iconpack.Filter
import ly.img.editor.core.ui.iconpack.Groupenter
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Keyboard
import ly.img.editor.core.ui.iconpack.Layersoutline
import ly.img.editor.core.ui.iconpack.Replace
import ly.img.editor.core.ui.iconpack.Selectgroup
import ly.img.editor.core.ui.iconpack.Tunevariant
import ly.img.editor.core.ui.iconpack.Typeface
import ly.img.editor.core.ui.tab_item.TabIcon
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

data class Block(
    val designBlock: DesignBlock,
    val type: BlockType,
    val options: List<OptionItemData> = emptyList(),
)

private val shapesWithOptions = arrayOf(ShapeType.Star, ShapeType.Polygon, ShapeType.Line)

internal fun createBlock(
    designBlock: DesignBlock,
    engine: Engine,
): Block {
    val type = DesignBlockType.getOrNull(engine.block.getType(designBlock))
    val isGraphicBlock = type == DesignBlockType.Graphic
    val kind = if (isGraphicBlock) engine.block.getKindEnum(designBlock) else null
    val fillType = engine.block.getFillType(designBlock)
    val shapeType =
        if (engine.block.hasShape(designBlock)) {
            ShapeType.get(engine.block.getType(engine.block.getShape(designBlock)))
        } else {
            null
        }

    val optionItems = mutableListOf<OptionItemData>()
    val blockType: BlockType

    // Check if an operation is allowed by scope
    fun isAllowedByScope(scope: String) = engine.block.isAllowedByScope(designBlock, scope)

    // Add options entry based on optionType type
    infix fun OptionType.addIf(enabled: Boolean) {
        if (enabled) {
            fun addOption(
                titleRes: Int,
                icon: TabIcon,
                textColor: ColorSchemeKeyToken? = null,
            ) {
                optionItems += OptionItemData(this, titleRes, icon, textColor)
            }
            when (this) {
                Crop -> addOption(R.string.ly_img_editor_crop, VectorIcon(IconPack.Croprotate))
                Edit -> addOption(R.string.ly_img_editor_edit, VectorIcon(IconPack.Keyboard))
                Layer -> addOption(R.string.ly_img_editor_layer, VectorIcon(IconPack.Layersoutline))
                Filter -> addOption(R.string.ly_img_editor_filter, VectorIcon(IconPack.Filter))
                Effect -> addOption(R.string.ly_img_editor_effect, VectorIcon(IconPack.Effect))
                Blur -> addOption(R.string.ly_img_editor_blur, VectorIcon(IconPack.Blur))
                Format -> addOption(R.string.ly_img_editor_format, VectorIcon(IconPack.Typeface))
                Replace -> addOption(R.string.ly_img_editor_replace, VectorIcon(IconPack.Replace))
                EnterGroup -> addOption(R.string.ly_img_editor_enter_group, VectorIcon(IconPack.Groupenter))
                FillStroke -> {
                    val (isFillSupported, isStrokeSupported) = engine.block.isFillStrokeSupported(designBlock)
                    if (isFillSupported || isStrokeSupported) {
                        addOption(
                            FillStrokeUiState.getFillStrokeTitleRes(isFillSupported, isStrokeSupported),
                            FillStrokeIcon.create(engine, designBlock),
                        )
                    }
                }

                SelectGroup -> addOption(R.string.ly_img_editor_select_group, VectorIcon(IconPack.Selectgroup))
                Adjustments -> addOption(R.string.ly_img_editor_adjustment, VectorIcon(IconPack.Adjustments))
                ShapeOptions -> addOption(R.string.ly_img_editor_options, VectorIcon(IconPack.Tunevariant))
                Duplicate -> addOption(R.string.ly_img_editor_duplicate, VectorIcon(IconPack.Duplicate))
                Delete ->
                    addOption(
                        R.string.ly_img_editor_delete,
                        VectorIcon(IconPack.Delete, tint = ColorSchemeKeyToken.Error),
                        textColor = ColorSchemeKeyToken.Error,
                    )
            }
        }
    }

    val hasLayerOption =
        type != DesignBlockType.Page && (
            isAllowedByScope(Scope.LayerBlendMode) ||
                isAllowedByScope(Scope.LayerOpacity) ||
                engine.isMoveAllowed(designBlock) ||
                engine.isDeleteAllowed(designBlock) ||
                engine.isDuplicateAllowed(designBlock)
        )

    when (type) {
        DesignBlockType.Text -> {
            blockType = BlockType.Text
            Edit addIf isAllowedByScope(Scope.TextEdit)
            Format addIf isAllowedByScope(Scope.TextCharacter)
            FillStroke addIf (isAllowedByScope(Scope.FillChange) || isAllowedByScope(Scope.StrokeChange))
            Duplicate addIf engine.isDuplicateAllowed(designBlock)
            Layer addIf hasLayerOption
            SelectGroup addIf engine.isGrouped(designBlock)
        }

        DesignBlockType.Group -> {
            blockType = BlockType.Group
            EnterGroup addIf true
            Layer addIf hasLayerOption
            SelectGroup addIf engine.isGrouped(designBlock)
        }

        DesignBlockType.Page -> {
            blockType = BlockType.Page
            FillStroke addIf (isAllowedByScope(Scope.FillChange) || isAllowedByScope(Scope.StrokeChange))
            SelectGroup addIf engine.isGrouped(designBlock)
        }

        DesignBlockType.Graphic -> {
            when (fillType) {
                FillType.Image -> {
                    if (kind == BlockKind.Sticker) {
                        blockType = BlockType.Sticker
                        FillStroke addIf (isAllowedByScope(Scope.FillChange) || isAllowedByScope(Scope.StrokeChange))
                        ShapeOptions addIf (shapeType in shapesWithOptions && isAllowedByScope(Scope.ShapeChange))
                        Duplicate addIf engine.isDuplicateAllowed(designBlock)
                        Layer addIf hasLayerOption
                        Replace addIf isAllowedByScope(Scope.FillChange)
                        SelectGroup addIf engine.isGrouped(designBlock)
                    } else {
                        blockType = BlockType.Image
                        Adjustments addIf isAllowedByScope(Scope.AppearanceAdjustment)
                        Filter addIf isAllowedByScope(Scope.AppearanceFilter)
                        Effect addIf isAllowedByScope(Scope.AppearanceEffect)
                        Blur addIf isAllowedByScope(Scope.AppearanceBlur)
                        Crop addIf (engine.block.hasCrop(designBlock) && isAllowedByScope(Scope.LayerCrop))
                        Duplicate addIf engine.isDuplicateAllowed(designBlock)
                        Layer addIf hasLayerOption
                        FillStroke addIf isAllowedByScope(Scope.StrokeChange)
                        Replace addIf isAllowedByScope(Scope.FillChange)
                        SelectGroup addIf engine.isGrouped(designBlock)
                    }
                }

                else -> {
                    blockType = BlockType.Shape
                    FillStroke addIf (isAllowedByScope(Scope.FillChange) || isAllowedByScope(Scope.StrokeChange))
                    ShapeOptions addIf (shapeType in shapesWithOptions && isAllowedByScope(Scope.ShapeChange))
                    Duplicate addIf engine.isDuplicateAllowed(designBlock)
                    Layer addIf hasLayerOption
                    SelectGroup addIf engine.isGrouped(designBlock)
                }
            }
        }

        else -> throw UnsupportedOperationException()
    }

    if (type != DesignBlockType.Page) {
        Delete addIf engine.isDeleteAllowed(designBlock)
    }

    return Block(
        designBlock = designBlock,
        type = blockType,
        options = optionItems,
    )
}
