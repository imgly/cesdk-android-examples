import ly.img.engine.DesignBlock
import java.nio.ByteBuffer

data class TemplateFromScratchResult(
    val scene: DesignBlock,
    val page: DesignBlock,
    val titleBlock: DesignBlock,
    val imageBlock: DesignBlock,
    val templateString: String,
    val templateArchive: ByteBuffer,
    val previewPng: ByteBuffer,
    val variables: List<String>,
    val placeholderBehaviorEnabled: Boolean,
    val placeholderEnabled: Boolean,
    val overlayEnabled: Boolean,
    val buttonEnabled: Boolean,
    val imageCanMove: Boolean,
    val imageCanResize: Boolean,
    val imageFillCanChange: Boolean,
)
