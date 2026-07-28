import java.nio.ByteBuffer

data class DuotoneResult(
    val presetCount: Int,
    val presetEffectsCount: Int,
    val presetIntensity: Float,
    val disabledPresetEffectEnabled: Boolean,
    val restoredPresetEffectEnabled: Boolean,
    val customEffectsBeforeRemoval: Int,
    val customEffectsAfterRemoval: Int,
    val combinedEffectsCount: Int,
    val previewPng: ByteBuffer,
)
