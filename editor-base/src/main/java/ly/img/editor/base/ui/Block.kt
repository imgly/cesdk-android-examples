package ly.img.editor.base.ui

import ly.img.editor.core.ui.engine.BlockKind
import ly.img.editor.core.ui.engine.BlockType
import ly.img.editor.core.ui.engine.getFillType
import ly.img.editor.core.ui.engine.getKindEnum
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType

data class Block(
    val designBlock: DesignBlock,
    val type: BlockType,
)

internal fun createBlock(
    designBlock: DesignBlock,
    engine: Engine,
): Block {
    val type = DesignBlockType.getOrNull(engine.block.getType(designBlock))
    val blockType =
        when (type) {
            DesignBlockType.Text -> BlockType.Text
            DesignBlockType.Group -> BlockType.Group
            DesignBlockType.Page -> BlockType.Page
            DesignBlockType.Audio -> BlockType.Audio
            DesignBlockType.Graphic -> {
                when (engine.block.getFillType(designBlock)) {
                    FillType.Image -> {
                        val kind = engine.block.getKindEnum(designBlock)
                        if (kind == BlockKind.Sticker) BlockType.Sticker else BlockType.Image
                    }
                    FillType.Video -> BlockType.Video
                    else -> BlockType.Shape
                }
            }
            else -> throw UnsupportedOperationException()
        }
    return Block(
        designBlock = designBlock,
        type = blockType,
    )
}
