import ly.img.engine.DesignBlock
import java.nio.ByteBuffer

data class DefaultAssetsResult(
    val loadedDefaultSourceIds: List<String>,
    val loadedDemoSourceIds: List<String>,
    val jsonSourceId: String,
    val filteredStickerCount: Int,
    val createdBlocks: List<DesignBlock>,
    val exportedPng: ByteBuffer,
)
