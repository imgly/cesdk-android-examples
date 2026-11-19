import java.nio.ByteBuffer

data class PhotobookSpreadsGuideSummary(
    val pageNamesBySpread: List<List<String>>,
    val photoOnLeftPage: Boolean,
    val copiedCaptionCount: Int,
    val coverVisibleAfterExport: Boolean,
    val shownPageNames: List<String>,
    val coverPdf: ByteBuffer,
    val insidePdf: ByteBuffer,
)
