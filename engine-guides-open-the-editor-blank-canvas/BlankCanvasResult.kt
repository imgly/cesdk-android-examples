package ly.img.editor.showcase

import ly.img.engine.DesignBlock
import java.nio.ByteBuffer

data class BlankCanvasResult(
    val page: DesignBlock,
    val block: DesignBlock,
    val previewPng: ByteBuffer,
)
