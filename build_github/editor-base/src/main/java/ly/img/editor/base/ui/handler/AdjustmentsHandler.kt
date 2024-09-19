package ly.img.editor.base.ui.handler

import ly.img.editor.base.engine.AdjustmentState
import ly.img.editor.base.engine.EffectGroup
import ly.img.editor.base.engine.getEffectOrCreateAndAppend
import ly.img.editor.base.engine.getGroup
import ly.img.editor.base.engine.removeEffectByType
import ly.img.editor.base.engine.setBlurType
import ly.img.editor.base.engine.toEngineColor
import ly.img.editor.base.ui.BlockEvent.*
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.ui.EventsHandler
import ly.img.editor.core.ui.inject
import ly.img.editor.core.ui.library.AppearanceAssetSourceType
import ly.img.editor.core.ui.library.getMeta
import ly.img.editor.core.ui.library.getUri
import ly.img.editor.core.ui.register
import ly.img.engine.Asset
import ly.img.engine.BlockApi
import ly.img.engine.BlurType
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.EffectType
import ly.img.engine.Engine

/**
 * Register all events related to appearance, like adding/removing filters, fx effects, blur effects etc.
 * @param engine Lambda returning the engine instance
 * @param block Lambda returning the block instance
 */
@Suppress("NAME_SHADOWING")
fun EventsHandler.appearanceEvents(
    engine: () -> Engine,
    block: () -> DesignBlock,
) {
    val engine by inject(engine)
    val block by inject(block)

    register<OnReplaceColorFilter> {
        replaceFilter(engine.block, it.designBlock, it.assetSourceType, it.asset)
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
        val filter =
            when (it.adjustment.type) {
                is BlurType -> {
                    engine.block.getBlur(block)
                }

                is EffectType -> {
                    engine.block.getEffectOrCreateAndAppend(block, it.adjustment.type)
                }

                else -> throw IllegalArgumentException(
                    "Unsupported adjustment type: ${it.adjustment.type}",
                )
            }
        when (it.value) {
            is AdjustmentState.Value.Int ->
                engine.block.setInt(
                    block = filter,
                    property = it.adjustment.propertyPath,
                    value = it.value.value,
                )
            is AdjustmentState.Value.Float ->
                engine.block.setFloat(
                    block = filter,
                    property = it.adjustment.propertyPath,
                    value = it.value.value,
                )
            is AdjustmentState.Value.Color ->
                engine.block.setColor(
                    block = filter,
                    property = it.adjustment.propertyPath,
                    value = it.value.value.toEngineColor(),
                )
        }
    }
}

private fun replaceFxEffect(
    blockApi: BlockApi,
    block: DesignBlock,
    effect: EffectType?,
) {
    EffectType.values().forEach {
        if (effect != it && it.getGroup() == EffectGroup.FxEffect) {
            blockApi.removeEffectByType(block, it)
        }
    }

    if (effect != null) {
        blockApi.getEffectOrCreateAndAppend(block, effect)
    }
}

private fun replaceFilter(
    blockApi: BlockApi,
    block: DesignBlock,
    assetSourceType: AssetSourceType?,
    asset: Asset?,
) {
    EffectType.values().filter {
        when (it) {
            EffectType.LutFilter -> assetSourceType !== AppearanceAssetSourceType.LutFilter
            EffectType.DuoToneFilter -> assetSourceType !== AppearanceAssetSourceType.DuoToneFilter
            else -> it.getGroup() == EffectGroup.Filter
        }
    }.forEach {
        blockApi.removeEffectByType(block, it)
    }

    asset ?: return

    if (assetSourceType === AppearanceAssetSourceType.DuoToneFilter) {
        val path = EffectType.DuoToneFilter.key
        blockApi.getEffectOrCreateAndAppend(block, EffectType.DuoToneFilter).also { effect ->
            blockApi.setColor(
                effect,
                "$path/darkColor",
                Color.fromHex(asset.getMeta("darkColor")!!),
            )
            blockApi.setFloat(effect, "$path/intensity", asset.getMeta("intensity", "0").toFloat())
            blockApi.setColor(
                effect,
                "$path/lightColor",
                Color.fromHex(asset.getMeta("lightColor")!!),
            )
        }
    } else if (assetSourceType === AppearanceAssetSourceType.LutFilter) {
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
