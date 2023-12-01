package ly.img.cesdk.editorui.handler

import ly.img.cesdk.core.data.AssetSource
import ly.img.cesdk.core.data.getMeta
import ly.img.cesdk.core.data.getUri
import ly.img.cesdk.core.ui.EventsHandler
import ly.img.cesdk.core.ui.inject
import ly.img.cesdk.core.ui.register
import ly.img.cesdk.editorui.BlockEvent.*
import ly.img.cesdk.engine.AdjustmentsValueType
import ly.img.cesdk.engine.EffectGroup
import ly.img.cesdk.engine.getEffectOrCreateAndAppend
import ly.img.cesdk.engine.getGroup
import ly.img.cesdk.engine.removeEffectByType
import ly.img.cesdk.engine.setBlurType
import ly.img.engine.Asset
import ly.img.engine.BlockApi
import ly.img.engine.BlurType
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.EffectType
import ly.img.engine.Engine
import kotlin.math.roundToInt

/**
 * Register all events related to appearance, like adding/removing filters, fx effects, blur effects etc.
 * @param engine Lambda returning the engine instance
 * @param block Lambda returning the block instance
 * @param fontFamilyMap Lambda returning the font family map
 */
@Suppress("NAME_SHADOWING")
fun EventsHandler.appearanceEvents(
    engine: () -> Engine,
    block: () -> DesignBlock
) {
    val engine by inject(engine)
    val block by inject(block)

    register<OnReplaceColorFilter> {
        replaceFilter(engine.block, it.designBlock, it.assetSource, it.asset)
        engine.editor.addUndoStep()
    }

    register<OnReplaceFxEffect> {
        replaceFxEffect(engine.block, it.designBlock, it.effect)
        engine.editor.addUndoStep()
    }

    register<OnReplaceBlurEffect> {
        engine.block.setBlurType(it.designBlock, it.effect)
        engine.editor.addUndoStep()
    }

    register<OnChangeEffectSettings> {
        val filter = when (it.adjustment.type) {
            is BlurType -> {
                engine.block.getBlur(block)
            }

            is EffectType -> {
                engine.block.getEffectOrCreateAndAppend(block, it.adjustment.type)
            }

            else -> throw IllegalArgumentException("Unsupported adjustment type: ${it.adjustment.type}")
        }
        when (it.adjustment.propertyType) {
            AdjustmentsValueType.INT -> engine.block.setInt(
                block = filter,
                property = it.adjustment.propertyPath,
                value = it.value.roundToInt()
            )
            AdjustmentsValueType.FLOAT -> engine.block.setFloat(
                block = filter,
                property = it.adjustment.propertyPath,
                value = it.value
            )
        }
    }
}

private fun replaceFxEffect(blockApi: BlockApi, block: DesignBlock, effect: EffectType?) {
    EffectType.values().forEach {
        if (effect != it && it.getGroup() == EffectGroup.FxEffect) {
            blockApi.removeEffectByType(block, it)
        }
    }

    if (effect != null) {
        blockApi.getEffectOrCreateAndAppend(block, effect)
    }
}

private fun replaceFilter(blockApi: BlockApi, block: DesignBlock, assetSource: AssetSource?, asset: Asset?) {
    EffectType.values().filter {
        when (it) {
            EffectType.LutFilter -> assetSource !is AssetSource.LutFilter
            EffectType.DuoToneFilter -> assetSource !is AssetSource.DuotoneFilter
            else -> it.getGroup() == EffectGroup.Filter
        }
    }.forEach {
        blockApi.removeEffectByType(block, it)
    }

    asset ?: return

    if (assetSource is AssetSource.DuotoneFilter) {
        val path = EffectType.DuoToneFilter.key
        blockApi.getEffectOrCreateAndAppend(block, EffectType.DuoToneFilter).also { effect ->
            blockApi.setColor(effect, "$path/darkColor", Color.fromHex(asset.getMeta("darkColor")!!))
            blockApi.setFloat(effect, "$path/intensity", asset.getMeta("intensity", "0").toFloat())
            blockApi.setColor(effect, "$path/lightColor", Color.fromHex(asset.getMeta("lightColor")!!))
        }
    } else if (assetSource is AssetSource.LutFilter) {
        val path = EffectType.LutFilter.key
        blockApi.getEffectOrCreateAndAppend(block, EffectType.LutFilter).also { effect ->
            val verticalTileCountMeta = asset.getMeta("verticalTileCount")?.toInt()
            val horizontalTileCountMeta = asset.getMeta("horizontalTileCount")?.toInt()

            val verticalTileCount = verticalTileCountMeta ?: horizontalTileCountMeta ?: 5
            val horizontalTileCount = horizontalTileCountMeta ?: verticalTileCountMeta ?: 5

            blockApi.setFloat(effect, "$path/intensity", asset.getMeta("intensity", "1").toFloat())
            blockApi.setString(effect, "$path/lutFileURI", asset.getUri())
            blockApi.setInt(effect, "$path/horizontalTileCount", horizontalTileCount)
            blockApi.setInt(effect, "$path/verticalTileCount", verticalTileCount)
        }
    }
}
