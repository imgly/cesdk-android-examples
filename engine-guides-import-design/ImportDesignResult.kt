import ly.img.engine.DesignBlock
import java.nio.ByteBuffer

data class ImportDesignResult(
    val remoteScene: DesignBlock,
    val stringScene: DesignBlock,
    val archiveScene: DesignBlock,
    val imageScene: DesignBlock,
    val videoScene: DesignBlock,
    val textOpacity: Float,
    val pageCount: Int,
    val previewPngData: ByteBuffer,
)
