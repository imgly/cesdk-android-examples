package ly.img.editor.base.dock.options.effect

import ly.img.editor.base.R
import ly.img.editor.base.engine.AdjustmentState
import ly.img.editor.base.engine.EffectAndBlurOptions
import ly.img.editor.base.engine.EffectGroup
import ly.img.editor.base.engine.NoneDesignBlock
import ly.img.editor.base.engine.getBlurOrEffectType
import ly.img.editor.base.engine.getGroup
import ly.img.editor.base.ui.Block
import ly.img.editor.base.ui.BlockEvent
import ly.img.editor.base.ui.Event
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.ui.library.getMeta
import ly.img.editor.core.ui.library.getUri
import ly.img.editor.core.ui.library.state.WrappedAsset
import ly.img.engine.BlurType
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.EffectType
import ly.img.engine.Engine

data class EffectUiState(
    val engine: Engine,
    val designBlock: DesignBlock,
    var appliedEffectId: String?,
    val libraryCategory: LibraryCategory,
    var adjustments: List<AdjustmentState>
) {

    val titleRes = when (libraryCategory) {
        LibraryCategory.FxEffects -> R.string.cesdk_effect
        LibraryCategory.Filters -> R.string.cesdk_filter
        LibraryCategory.Blur -> R.string.cesdk_blur
        else -> throw IllegalArgumentException("Unsupported library category: $libraryCategory")
    }

    var selectedAsset: WrappedAsset? = null
    companion object Factory {

        private fun getEffectBlock(engine: Engine, designBlock: DesignBlock, libraryCategory: LibraryCategory) =
            if (libraryCategory == LibraryCategory.Blur) {
                if (engine.block.isBlurEnabled(designBlock)) {
                    engine.block.getBlur(designBlock).takeIf { it != NoneDesignBlock }
                } else null
            } else {
                val neededGroup = if (libraryCategory == LibraryCategory.Filters) {
                    EffectGroup.Filter
                } else {
                    EffectGroup.FxEffect
                }
                engine.block.getEffects(designBlock).find { effect ->
                    val effectType = engine.block.getType(effect)
                    EffectType.getOrNull(effectType)?.getGroup() == neededGroup
                }
            }

        private fun getEffectUri(engine: Engine, effect: DesignBlock?) : String? {
            effect ?: return null
            return when(val filterType = engine.block.getBlurOrEffectType(effect)) {
                EffectType.DuoToneFilter -> {
                    val darkColor = engine.block.getColor(effect, "${filterType.key}/darkColor")
                    val lightColor = engine.block.getColor(effect, "${filterType.key}/lightColor")
                    buildDuotoneUri(darkColor, lightColor)
                }
                EffectType.LutFilter -> {
                    engine.block.getString(effect, "${filterType.key}/lutFileURI")
                }
                else -> filterType?.key
            }
        }
        private fun buildDuotoneUri(darkColor: Color, lightColor: Color) : String {
            return "DuotoneFilter:${darkColor}:${lightColor}"
        }

        /**
         * Create a EffectUiState instance based on the given Block and Engine.
         */
        fun create(block: Block, engine: Engine, libraryCategory: LibraryCategory): EffectUiState {
            val activeEffect = getEffectBlock(engine, block.designBlock, libraryCategory)
            return EffectUiState(
                engine,
                block.designBlock,
                getEffectUri(engine, activeEffect),
                libraryCategory,
                EffectAndBlurOptions.getEffectAdjustments(engine, activeEffect)
            )
        }
    }

    fun checkSelection(asset: WrappedAsset?) : Boolean {
        return when (asset?.assetSourceType) {
            AssetSourceType.LutFilter -> {
                appliedEffectId == asset.asset.getUri()
            }
            AssetSourceType.DuoToneFilter -> {
                appliedEffectId == buildDuotoneUri(Color.fromHex(asset.asset.getMeta("darkColor")!!), Color.fromHex(asset.asset.getMeta("lightColor")!!))
            }
            AssetSourceType.FxEffect -> {
                val appliedEffectName = appliedEffectId ?: return false
                return asset.asset.getMeta("effectType")?.endsWith(appliedEffectName) == true
            }
            AssetSourceType.Blur -> {
                val appliedEffectName = appliedEffectId ?: return false
                return asset.asset.getMeta("blurType")?.endsWith(appliedEffectName) == true
            }
            else -> appliedEffectId == null && asset == null
        }
    }


    /**
     * Update the filter selection without creating a new UiState instance.
     */
    fun updateEffectSelection(onEvent: (Event) -> Unit,  wrappedAsset: WrappedAsset?) {
        if (selectedAsset != wrappedAsset || wrappedAsset == null) {
            selectedAsset?.setSelected(false)
            wrappedAsset?.setSelected(true)
            selectedAsset = wrappedAsset

            when (libraryCategory) {
                LibraryCategory.Filters -> {
                    onEvent(BlockEvent.OnReplaceColorFilter(designBlock, wrappedAsset?.assetSourceType, wrappedAsset?.asset))
                }

                LibraryCategory.FxEffects -> {
                    val effectType = wrappedAsset?.asset?.getMeta("effectType")
                    onEvent(BlockEvent.OnReplaceFxEffect(designBlock, effectType?.let { EffectType.getOrNull(it) }))
                }

                LibraryCategory.Blur -> {
                    val blurType = wrappedAsset?.asset?.getMeta("blurType")
                    onEvent(BlockEvent.OnReplaceBlurEffect(designBlock, blurType?.let { BlurType.getOrNull(it) }))
                }

                else -> throw IllegalArgumentException("Unsupported library category: $libraryCategory")
            }

            val activeEffect = getEffectBlock(engine, designBlock, libraryCategory)
            this.appliedEffectId = getEffectUri(engine, activeEffect)
            this.adjustments = EffectAndBlurOptions.getEffectAdjustments(engine, activeEffect)
        }
    }
}



