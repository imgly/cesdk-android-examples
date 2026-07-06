import ly.img.engine.DesignUnit
import java.nio.ByteBuffer

data class ImportedTemplateSummary(
    val sceneUrlPageCount: Int,
    val stringPageCount: Int,
    val archivePageCount: Int,
    val appliedPageCount: Int,
    val appliedPageWidth: Float,
    val appliedPageHeight: Float,
    val designUnit: DesignUnit,
    val previewPngData: ByteBuffer,
)
