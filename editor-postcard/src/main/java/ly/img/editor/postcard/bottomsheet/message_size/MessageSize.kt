package ly.img.editor.postcard.bottomsheet.message_size

import androidx.compose.ui.graphics.vector.ImageVector
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Sizel
import ly.img.editor.core.ui.iconpack.Sizelcircled
import ly.img.editor.core.ui.iconpack.Sizem
import ly.img.editor.core.ui.iconpack.Sizemcircled
import ly.img.editor.core.ui.iconpack.Sizes
import ly.img.editor.core.ui.iconpack.Sizescircled
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

enum class MessageSize(val size: Float, val circledIcon: ImageVector, val icon: ImageVector) {
    Small(14f, IconPack.Sizescircled, IconPack.Sizes),
    Medium(18f, IconPack.Sizemcircled, IconPack.Sizem),
    Large(22f, IconPack.Sizelcircled, IconPack.Sizel),
    ;

    companion object {
        fun get(
            engine: Engine,
            block: DesignBlock,
        ): MessageSize {
            val size = engine.block.getFloat(block, "text/fontSize")
            return MessageSize.values().firstOrNull { it.size == size } ?: Large
        }
    }
}
