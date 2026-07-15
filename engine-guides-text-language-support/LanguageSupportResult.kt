import ly.img.engine.HorizontalAlignment

data class LanguageSupportResult(
    val textValues: List<String>,
    val effectiveRtlAlignment: HorizontalAlignment,
    val paragraphIndices: List<Int>,
    val paragraphOverride: HorizontalAlignment?,
    val clearedParagraphOverride: HorizontalAlignment?,
    val mixedTypefaceNames: List<String>,
    val localizedHeadline: String,
)
