data class TextAnimationSummary(
    val writingStyleOptions: List<String>,
    val easingOptions: List<String>,
    val blockWritingStyle: String,
    val lineWritingStyle: String,
    val wordWritingStyle: String,
    val characterWritingStyle: String,
    val sequentialOverlap: Float,
    val cascadingOverlap: Float,
    val combinedDuration: Double,
)
