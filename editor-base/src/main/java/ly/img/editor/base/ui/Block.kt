package ly.img.editor.base.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ly.img.editor.base.R
import ly.img.editor.base.dock.OptionItemData
import ly.img.editor.base.dock.OptionType
import ly.img.editor.base.dock.OptionType.Adjustments
import ly.img.editor.base.dock.OptionType.AttachBackground
import ly.img.editor.base.dock.OptionType.Blur
import ly.img.editor.base.dock.OptionType.Crop
import ly.img.editor.base.dock.OptionType.Delete
import ly.img.editor.base.dock.OptionType.DetachBackground
import ly.img.editor.base.dock.OptionType.Duplicate
import ly.img.editor.base.dock.OptionType.Edit
import ly.img.editor.base.dock.OptionType.Effect
import ly.img.editor.base.dock.OptionType.EnterGroup
import ly.img.editor.base.dock.OptionType.FillStroke
import ly.img.editor.base.dock.OptionType.Filter
import ly.img.editor.base.dock.OptionType.Format
import ly.img.editor.base.dock.OptionType.Layer
import ly.img.editor.base.dock.OptionType.Reorder
import ly.img.editor.base.dock.OptionType.Replace
import ly.img.editor.base.dock.OptionType.SelectGroup
import ly.img.editor.base.dock.OptionType.ShapeOptions
import ly.img.editor.base.dock.OptionType.Split
import ly.img.editor.base.dock.OptionType.Volume
import ly.img.editor.base.dock.options.fillstroke.FillStrokeUiState
import ly.img.editor.base.engine.isDeleteAllowed
import ly.img.editor.base.engine.isDuplicateAllowed
import ly.img.editor.base.engine.isGrouped
import ly.img.editor.base.engine.isMoveAllowed
import ly.img.editor.base.engine.isParentBackgroundTrack
import ly.img.editor.core.component.data.EditorIcon
import ly.img.editor.core.iconpack.Adjustments
import ly.img.editor.core.iconpack.Blur
import ly.img.editor.core.iconpack.CropRotate
import ly.img.editor.core.iconpack.Delete
import ly.img.editor.core.iconpack.Effect
import ly.img.editor.core.iconpack.Filter
import ly.img.editor.core.iconpack.ReorderHorizontally
import ly.img.editor.core.ui.engine.BlockKind
import ly.img.editor.core.ui.engine.BlockType
import ly.img.editor.core.ui.engine.Scope
import ly.img.editor.core.ui.engine.getBackgroundTrack
import ly.img.editor.core.ui.engine.getFill
import ly.img.editor.core.ui.engine.getFillType
import ly.img.editor.core.ui.engine.getKindEnum
import ly.img.editor.core.ui.engine.getStrokeColor
import ly.img.editor.core.ui.engine.hasColorOrGradientFill
import ly.img.editor.core.ui.engine.isSceneModeVideo
import ly.img.editor.core.ui.iconpack.Asclip
import ly.img.editor.core.ui.iconpack.Asoverlay
import ly.img.editor.core.ui.iconpack.Duplicate
import ly.img.editor.core.ui.iconpack.Groupenter
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Keyboard
import ly.img.editor.core.ui.iconpack.Layersoutline
import ly.img.editor.core.ui.iconpack.Replace
import ly.img.editor.core.ui.iconpack.Selectgroup
import ly.img.editor.core.ui.iconpack.ShapeIcon
import ly.img.editor.core.ui.iconpack.Split
import ly.img.editor.core.ui.iconpack.Typeface
import ly.img.editor.core.ui.iconpack.Volumehigh
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.editor.core.R as CoreR
import ly.img.editor.core.iconpack.IconPack as CoreIconPack

data class Block(
    val designBlock: DesignBlock,
    val type: BlockType,
    val options: List<OptionItemData> = emptyList(),
)

private val shapesWithOptions = arrayOf(ShapeType.Star, ShapeType.Polygon, ShapeType.Line, ShapeType.Rect)

internal fun createBlock(
    designBlock: DesignBlock,
    engine: Engine,
): Block {
    val type = DesignBlockType.getOrNull(engine.block.getType(designBlock))
    val isGraphicBlock = type == DesignBlockType.Graphic
    val kind = if (isGraphicBlock) engine.block.getKindEnum(designBlock) else null
    val fillType = engine.block.getFillType(designBlock)
    val shapeType =
        if (engine.block.supportsShape(designBlock)) {
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
                icon: EditorIcon,
                textColor: (@Composable () -> Color)? = null,
            ) {
                optionItems += OptionItemData(this, titleRes, icon, textColor)
            }
            when (this) {
                Crop -> addOption(CoreR.string.ly_img_editor_crop, EditorIcon.Vector(CoreIconPack.CropRotate))
                Reorder -> addOption(CoreR.string.ly_img_editor_reorder, EditorIcon.Vector(CoreIconPack.ReorderHorizontally))
                Edit -> addOption(R.string.ly_img_editor_edit, EditorIcon.Vector(IconPack.Keyboard))
                Layer -> addOption(R.string.ly_img_editor_layer, EditorIcon.Vector(IconPack.Layersoutline))
                Filter -> addOption(CoreR.string.ly_img_editor_filter, EditorIcon.Vector(CoreIconPack.Filter))
                Effect -> addOption(CoreR.string.ly_img_editor_effect, EditorIcon.Vector(CoreIconPack.Effect))
                Blur -> addOption(CoreR.string.ly_img_editor_blur, EditorIcon.Vector(CoreIconPack.Blur))
                Format -> addOption(R.string.ly_img_editor_format, EditorIcon.Vector(IconPack.Typeface))
                Replace -> addOption(R.string.ly_img_editor_replace, EditorIcon.Vector(IconPack.Replace))
                EnterGroup -> addOption(R.string.ly_img_editor_enter_group, EditorIcon.Vector(IconPack.Groupenter))
                FillStroke -> {
                    val icon = getFillStrokeIcon(engine, designBlock)
                    if (icon.showFill || icon.showStroke) {
                        addOption(
                            FillStrokeUiState.getFillStrokeTitleRes(icon.showFill, icon.showStroke),
                            icon,
                        )
                    }
                }

                SelectGroup -> addOption(R.string.ly_img_editor_select_group, EditorIcon.Vector(IconPack.Selectgroup))
                Adjustments -> addOption(CoreR.string.ly_img_editor_adjustments, EditorIcon.Vector(CoreIconPack.Adjustments))
                ShapeOptions -> addOption(R.string.ly_img_editor_options, EditorIcon.Vector(IconPack.ShapeIcon))
                Volume -> addOption(R.string.ly_img_editor_volume, EditorIcon.Vector(IconPack.Volumehigh))
                Split -> addOption(R.string.ly_img_editor_split, EditorIcon.Vector(IconPack.Split))
                Duplicate -> addOption(R.string.ly_img_editor_duplicate, EditorIcon.Vector(IconPack.Duplicate))
                Delete ->
                    addOption(
                        CoreR.string.ly_img_editor_delete,
                        EditorIcon.Vector(CoreIconPack.Delete, tint = { MaterialTheme.colorScheme.error }),
                        textColor = { MaterialTheme.colorScheme.error },
                    )

                AttachBackground -> addOption(R.string.ly_img_editor_attach_background, EditorIcon.Vector(IconPack.Asclip))
                DetachBackground -> addOption(R.string.ly_img_editor_detach_background, EditorIcon.Vector(IconPack.Asoverlay))
            }
        }
    }

    val supportsLayerOption =
        type != DesignBlockType.Page && (
            isAllowedByScope(Scope.LayerBlendMode) ||
                isAllowedByScope(Scope.LayerOpacity) ||
                engine.isMoveAllowed(designBlock) ||
                engine.isDeleteAllowed(designBlock) ||
                engine.isDuplicateAllowed(designBlock)
        )

    val shouldShowReorder = {
        val backgroundTrack = engine.block.getBackgroundTrack()
        engine.isSceneModeVideo && engine.block.isParentBackgroundTrack(designBlock) &&
            backgroundTrack != null && engine.block.getChildren(backgroundTrack).size >= 2
    }

    when (type) {
        DesignBlockType.Text -> {
            blockType = BlockType.Text
            Edit addIf isAllowedByScope(Scope.TextEdit)
            Format addIf isAllowedByScope(Scope.TextCharacter)
            FillStroke addIf true
            Duplicate addIf engine.isDuplicateAllowed(designBlock)
            Layer addIf supportsLayerOption
            Split addIf (engine.isSceneModeVideo && isAllowedByScope(Scope.LifecycleDuplicate))
            AttachBackground addIf (engine.isSceneModeVideo && !engine.block.isParentBackgroundTrack(designBlock))
            DetachBackground addIf (engine.isSceneModeVideo && engine.block.isParentBackgroundTrack(designBlock))
            Reorder addIf shouldShowReorder()
            SelectGroup addIf engine.isGrouped(designBlock)
        }

        DesignBlockType.Group -> {
            blockType = BlockType.Group
            EnterGroup addIf true
            Layer addIf supportsLayerOption
            SelectGroup addIf engine.isGrouped(designBlock)
        }

        DesignBlockType.Page -> {
            blockType = BlockType.Page
            FillStroke addIf (isAllowedByScope(Scope.FillChange) || isAllowedByScope(Scope.StrokeChange))
            SelectGroup addIf engine.isGrouped(designBlock)
        }

        DesignBlockType.Audio -> {
            blockType = BlockType.Audio
            Volume addIf isAllowedByScope(Scope.FillChange)
            Duplicate addIf engine.isDuplicateAllowed(designBlock)
            Split addIf isAllowedByScope(Scope.LifecycleDuplicate)
            Replace addIf isAllowedByScope(Scope.FillChange)
        }

        DesignBlockType.Graphic -> {
            when (fillType) {
                FillType.Image -> {
                    if (kind == BlockKind.Sticker) {
                        blockType = BlockType.Sticker
                        Duplicate addIf engine.isDuplicateAllowed(designBlock)
                        Layer addIf supportsLayerOption
                        Split addIf (engine.isSceneModeVideo && isAllowedByScope(Scope.LifecycleDuplicate))
                        AttachBackground addIf (engine.isSceneModeVideo && !engine.block.isParentBackgroundTrack(designBlock))
                        DetachBackground addIf (engine.isSceneModeVideo && engine.block.isParentBackgroundTrack(designBlock))
                        Replace addIf isAllowedByScope(Scope.FillChange)
                        Reorder addIf shouldShowReorder()
                        SelectGroup addIf engine.isGrouped(designBlock)
                    } else {
                        blockType = BlockType.Image
                        Adjustments addIf isAllowedByScope(Scope.AppearanceAdjustment)
                        Filter addIf isAllowedByScope(Scope.AppearanceFilter)
                        Effect addIf isAllowedByScope(Scope.AppearanceEffect)
                        Blur addIf isAllowedByScope(Scope.AppearanceBlur)
                        Crop addIf (engine.block.supportsCrop(designBlock) && isAllowedByScope(Scope.LayerCrop))
                        Duplicate addIf engine.isDuplicateAllowed(designBlock)
                        Layer addIf supportsLayerOption
                        Split addIf (engine.isSceneModeVideo && isAllowedByScope(Scope.LifecycleDuplicate))
                        FillStroke addIf isAllowedByScope(Scope.StrokeChange)
                        AttachBackground addIf (engine.isSceneModeVideo && !engine.block.isParentBackgroundTrack(designBlock))
                        DetachBackground addIf (engine.isSceneModeVideo && engine.block.isParentBackgroundTrack(designBlock))
                        Replace addIf isAllowedByScope(Scope.FillChange)
                        Reorder addIf shouldShowReorder()
                        SelectGroup addIf engine.isGrouped(designBlock)
                    }
                }

                FillType.Video -> {
                    blockType = BlockType.Video
                    Adjustments addIf isAllowedByScope(Scope.AppearanceAdjustment)
                    Filter addIf isAllowedByScope(Scope.AppearanceFilter)
                    Effect addIf isAllowedByScope(Scope.AppearanceEffect)
                    Blur addIf isAllowedByScope(Scope.AppearanceBlur)
                    Volume addIf isAllowedByScope(Scope.FillChange)
                    Crop addIf (engine.block.supportsCrop(designBlock) && isAllowedByScope(Scope.LayerCrop))
                    Duplicate addIf engine.isDuplicateAllowed(designBlock)
                    Layer addIf supportsLayerOption
                    Split addIf (engine.isSceneModeVideo && isAllowedByScope(Scope.LifecycleDuplicate))
                    FillStroke addIf isAllowedByScope(Scope.StrokeChange)
                    AttachBackground addIf (engine.isSceneModeVideo && !engine.block.isParentBackgroundTrack(designBlock))
                    DetachBackground addIf (engine.isSceneModeVideo && engine.block.isParentBackgroundTrack(designBlock))
                    Replace addIf isAllowedByScope(Scope.FillChange)
                    Reorder addIf shouldShowReorder()
                    SelectGroup addIf engine.isGrouped(designBlock)
                }

                else -> {
                    blockType = BlockType.Shape
                    FillStroke addIf (isAllowedByScope(Scope.FillChange) || isAllowedByScope(Scope.StrokeChange))
                    ShapeOptions addIf (shapeType in shapesWithOptions && isAllowedByScope(Scope.ShapeChange))
                    Duplicate addIf engine.isDuplicateAllowed(designBlock)
                    Layer addIf supportsLayerOption
                    Split addIf (engine.isSceneModeVideo && isAllowedByScope(Scope.LifecycleDuplicate))
                    AttachBackground addIf (engine.isSceneModeVideo && !engine.block.isParentBackgroundTrack(designBlock))
                    DetachBackground addIf (engine.isSceneModeVideo && engine.block.isParentBackgroundTrack(designBlock))
                    Reorder addIf shouldShowReorder()
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

/**
 * Returns a fill stroke icon for the [designBlock] based on the current engine state.
 *
 * @param engine the engine of the current editor.
 * @param designBlock the design block that is queried.
 * @return a fill stroke icon for the queried design block.
 */
// todo temporary location of this function. Reconsider in inspector bar PR.
private fun getFillStrokeIcon(
    engine: Engine,
    designBlock: DesignBlock,
): EditorIcon.FillStroke {
    val showFill =
        engine.block.supportsFill(designBlock) &&
            engine.block.hasColorOrGradientFill(designBlock) &&
            engine.block.isAllowedByScope(designBlock, "fill/change")
    val showStroke =
        engine.block.supportsStroke(designBlock) &&
            engine.block.isAllowedByScope(designBlock, "stroke/change")
    return EditorIcon.FillStroke(
        showFill = showFill,
        showStroke = showStroke,
        fill =
            if (showFill && engine.block.isFillEnabled(designBlock)) {
                engine.getFill(designBlock)
            } else {
                null
            },
        stroke =
            if (showStroke && engine.block.isStrokeEnabled(designBlock)) {
                engine.getStrokeColor(designBlock)
            } else {
                null
            },
    )
}
