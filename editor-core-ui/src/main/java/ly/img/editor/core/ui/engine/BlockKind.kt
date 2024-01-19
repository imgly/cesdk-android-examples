package ly.img.editor.core.ui.engine

import ly.img.engine.BlockApi
import ly.img.engine.DesignBlock

enum class BlockKind(val key: String) {
    Image("image"),
    Sticker("sticker"),
    Shape("shape")
}

fun BlockApi.getKindEnum(designBlock: DesignBlock): BlockKind {
    val kind = this.getKind(designBlock)
    for (blockKind in BlockKind.values()) {
        if (blockKind.key == kind) return blockKind
    }
    throw UnsupportedOperationException("$kind is not supported yet")
}