package ly.img.editor.postcard.util

import ly.img.editor.base.engine.setFillType
import ly.img.editor.base.engine.toEngineColor
import ly.img.editor.core.component.data.GradientFill
import ly.img.editor.core.component.data.SolidFill
import ly.img.editor.core.ui.engine.Scope
import ly.img.editor.core.ui.engine.getFill
import ly.img.editor.core.ui.engine.getPage
import ly.img.editor.core.ui.engine.overrideAndRestore
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.RGBAColor
import java.util.TreeMap

enum class ColorType {
    Fill,
    Stroke,
}

class NamedColor(val name: String, val color: RGBAColor) {
    val colorTypeBlocksMapping = hashMapOf<ColorType, HashSet<DesignBlock>>()
}

class SelectionColors {
    private val namedColors: TreeMap<String, NamedColor> = TreeMap()

    fun add(
        designBlock: DesignBlock,
        name: String,
        color: RGBAColor,
        colorType: ColorType,
    ) {
        val namedColor = namedColors.getOrPut(name) { NamedColor(name, color) }
        val blockSet = namedColor.colorTypeBlocksMapping.getOrPut(colorType) { HashSet() }
        blockSet.add(designBlock)
    }

    fun getColors(): List<NamedColor> {
        return namedColors.values.toList()
    }

    fun getNamedColor(name: String) = requireNotNull(namedColors[name])
}

private fun Engine.getSelectionColors(
    designBlock: DesignBlock,
    includeUnnamed: Boolean,
    includeDisabled: Boolean,
    setDisabled: Boolean,
    ignoreScope: Boolean,
    selectionColors: SelectionColors,
) {
    if (!(
            block.isScopeEnabled(designBlock, Scope.FillChange) ||
                block.isScopeEnabled(designBlock, Scope.StrokeChange)
        ) &&
        !ignoreScope
    ) {
        return
    }
    val name = block.getName(designBlock)
    if (name.isEmpty() && !includeUnnamed) {
        return
    }

    val supportsFill = block.supportsFill(designBlock)
    val supportsStroke = block.supportsStroke(designBlock)

    fun addColor(
        colorType: ColorType,
        includeDisabled: Boolean = false,
    ): RGBAColor? {
        val color =
            when (colorType) {
                ColorType.Fill ->
                    if ((block.isFillEnabled(designBlock) || includeDisabled)) {
                        when (val fillInfo = getFill(designBlock)) {
                            is SolidFill, is GradientFill -> fillInfo.mainColor.toEngineColor()
                            else -> null
                        }
                    } else {
                        null
                    }

                ColorType.Stroke ->
                    if (block.isStrokeEnabled(designBlock) || includeDisabled) {
                        block.getStrokeColor(designBlock) as RGBAColor
                    } else {
                        null
                    }
            } ?: return null

        selectionColors.add(designBlock, name, color, colorType)
        return color
    }

    if (supportsFill && supportsStroke) {
        // Assign enabled color to disabled color to ease template creation.
        val fillColor = addColor(ColorType.Fill)
        val strokeColor = addColor(ColorType.Stroke)

        fun setAndAddColor(
            colorType: ColorType,
            color: RGBAColor,
        ) {
            val propertyColor =
                when (colorType) {
                    ColorType.Fill -> getFill(designBlock)?.mainColor?.toEngineColor()
                    ColorType.Stroke -> block.getStrokeColor(designBlock)
                }
            if (setDisabled && propertyColor != color) {
                when (colorType) {
                    ColorType.Fill -> {
                        overrideAndRestore(designBlock, Scope.FillChange) {
                            block.setFillType(designBlock, FillType.Color)
                            block.setFillSolidColor(designBlock, color)
                        }
                    }

                    ColorType.Stroke ->
                        overrideAndRestore(designBlock, Scope.StrokeChange) {
                            block.setStrokeColor(designBlock, color)
                        }
                }
            }
            addColor(colorType, includeDisabled)
        }

        if (fillColor == null && strokeColor != null) {
            setAndAddColor(ColorType.Fill, strokeColor)
        } else if (strokeColor == null && fillColor != null) {
            setAndAddColor(ColorType.Stroke, fillColor)
        } else {
            addColor(ColorType.Fill, includeDisabled)
            addColor(ColorType.Stroke, includeDisabled)
        }
    } else if (supportsFill) {
        addColor(ColorType.Fill)
    } else if (supportsStroke) {
        addColor(ColorType.Stroke)
    }
}

/**
 * Traverse design block hierarchy and collect used colors.
 *
 * @param designBlock Parent block to start traversal.
 * @param includeUnnamed Include colors of unnamed blocks.
 * @param includeDisabled Include currently invisible colors of disabled properties.
 * @param setDisabled Assign colors of enabled properties to colors of disabled properties of the same block to ease scene template creation.
 * @param ignoreScope Ignore scope when collecting colors.
 */
private fun Engine.getBlockSelectionColors(
    designBlock: DesignBlock,
    includeUnnamed: Boolean,
    includeDisabled: Boolean,
    setDisabled: Boolean,
    ignoreScope: Boolean,
): SelectionColors {
    fun traverse(
        designBlock: DesignBlock,
        selectionColors: SelectionColors,
    ) {
        getSelectionColors(
            designBlock = designBlock,
            includeUnnamed = includeUnnamed,
            includeDisabled = includeDisabled,
            setDisabled = setDisabled,
            ignoreScope = ignoreScope,
            selectionColors = selectionColors,
        )
        val children = block.getChildren(designBlock)
        children.forEach { traverse(it, selectionColors) }
    }

    val selectionColors = SelectionColors()
    traverse(designBlock, selectionColors)
    return selectionColors
}

internal fun Engine.getPageSelectionColors(
    forPage: Int,
    includeUnnamed: Boolean = false,
    includeDisabled: Boolean = false,
    setDisabled: Boolean = false,
    ignoreScope: Boolean = false,
): SelectionColors {
    return getBlockSelectionColors(
        designBlock = getPage(forPage),
        includeUnnamed = includeUnnamed,
        includeDisabled = includeDisabled,
        setDisabled = setDisabled,
        ignoreScope = ignoreScope,
    )
}
