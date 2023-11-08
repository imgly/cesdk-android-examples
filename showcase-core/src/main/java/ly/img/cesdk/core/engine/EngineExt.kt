package ly.img.cesdk.core.engine

import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

fun Engine.deselectAllBlocks() {
    block.findAllSelected().forEach {
        block.setSelected(it, false)
    }
}

fun Engine.getPage(index: Int): DesignBlock {
    val pages = getSortedPages()
    return pages[index]
}

fun Engine.getSortedPages(): List<DesignBlock> {
    return block.getChildren(getStack())
}

fun Engine.getStack(): DesignBlock {
    return block.findByType(DesignBlockType.STACK).first()
}