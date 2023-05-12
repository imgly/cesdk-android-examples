package ly.img.cesdk.bottomsheet.message_size

import androidx.compose.ui.graphics.vector.ImageVector
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.iconpack.Sizel
import ly.img.cesdk.core.iconpack.Sizelcircled
import ly.img.cesdk.core.iconpack.Sizem
import ly.img.cesdk.core.iconpack.Sizemcircled
import ly.img.cesdk.core.iconpack.Sizes
import ly.img.cesdk.core.iconpack.Sizescircled
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

enum class MessageSize(val size: Float, val circledIcon: ImageVector, val icon: ImageVector) {
    Small(14f, IconPack.Sizescircled, IconPack.Sizes),
    Medium(18f, IconPack.Sizemcircled, IconPack.Sizem),
    Large(22f, IconPack.Sizelcircled, IconPack.Sizel);

    companion object {
        fun get(engine: Engine, block: DesignBlock): MessageSize {
            val size = engine.block.getFloat(block, "text/fontSize")
            return MessageSize.values().firstOrNull { it.size == size } ?: Large
        }
    }
}