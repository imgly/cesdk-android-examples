package ly.img.editor.core.component.data

import androidx.compose.runtime.Stable
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.FillType

/**
 * A class containing information on the current selection in the editor.
 *
 * @param designBlock the design block that is currently selected.
 * @param parentDesignBlock the parent design block of the [designBlock].
 * @param type the type of the [designBlock].
 * @param fillType the optional fill type of the [designBlock].
 * @param kind the kind of the [designBlock].
 */
@Stable
data class Selection(
    val designBlock: DesignBlock,
    val parentDesignBlock: DesignBlock?,
    val type: DesignBlockType,
    val fillType: FillType?,
    val kind: String?,
)
