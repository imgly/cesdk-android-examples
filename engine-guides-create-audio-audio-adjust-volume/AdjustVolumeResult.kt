data class AdjustVolumeResult(
    val foregroundVolume: Float,
    val backgroundVolume: Float,
    val videoFillVolume: Float,
    val mutedVolume: Float,
    val muted: Boolean,
    val isMutedAfterUnmute: Boolean,
    val sliderVolume: Float,
    val sliderPercent: Int,
    val toggledMuted: Boolean,
    val forceMuted: Boolean,
)
