package ly.img.cesdk.dock.options.effect

import ly.img.cesdk.core.data.AssetSource
import ly.img.cesdk.core.data.WrappedAsset
import ly.img.cesdk.core.data.getMeta
import ly.img.cesdk.core.data.getUri
import ly.img.cesdk.core.library.state.LibraryCategory
import ly.img.cesdk.editorui.Block
import ly.img.cesdk.editorui.BlockEvent
import ly.img.cesdk.editorui.Event
import ly.img.cesdk.editorui.R
import ly.img.cesdk.engine.AdjustmentState
import ly.img.cesdk.engine.EffectAndBlurOptions
import ly.img.cesdk.engine.EffectGroup
import ly.img.cesdk.engine.NoneDesignBlock
import ly.img.cesdk.engine.getBlurOrEffectType
import ly.img.cesdk.engine.getGroup
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
        return when (asset?.assetSource) {
            AssetSource.LutFilter -> {
                appliedEffectId == asset.asset.getUri()
            }
            AssetSource.DuotoneFilter -> {
                appliedEffectId == buildDuotoneUri(Color.fromHex(asset.asset.getMeta("darkColor")!!), Color.fromHex(asset.asset.getMeta("lightColor")!!))
            }
            AssetSource.FxEffect -> {
                val appliedEffectName = appliedEffectId ?: return false
                return asset.asset.getMeta("effectType")?.endsWith(appliedEffectName) == true
            }
            AssetSource.Blur -> {
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
                    onEvent(BlockEvent.OnReplaceColorFilter(designBlock, wrappedAsset?.assetSource, wrappedAsset?.asset))
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



