import ly.img.engine.GradientColorStop

data class GradientFillsResult(
    val sceneSupportsFill: Boolean,
    val pageSupportsFill: Boolean,
    val graphicSupportsFill: Boolean,
    val textSupportsFill: Boolean,
    val textRejectsGradientFill: Boolean,
    val pageFillType: String,
    val linearFillType: String,
    val radialFillType: String,
    val conicalFillType: String,
    val linearStops: List<GradientColorStop>,
    val colorSpaceStops: List<GradientColorStop>,
    val linearStartX: Float,
    val linearStartY: Float,
    val linearEndX: Float,
    val linearEndY: Float,
    val radialRadius: Float,
    val conicalCenterX: Float,
    val sharedStops: List<GradientColorStop>,
    val duplicateFillIsIndependent: Boolean,
)
