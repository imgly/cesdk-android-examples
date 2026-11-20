import java.nio.ByteBuffer

data class FoldedCardsGuideSummary(
    val sheetNames: List<String>,
    val photoOwnerName: String?,
    val clippedPanelCount: Int,
    val titleOvershoot: Float?,
    val titleOnFront: Boolean,
    val cardSheetWidth: Float,
    val sheetPdfs: List<ByteBuffer>,
)
