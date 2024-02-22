package ly.img.editor.core.ui.engine

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

fun Engine.getScene(): DesignBlock {
    return block.findByType(DesignBlockType.Scene).first()
}

fun Engine.getSortedPages(): List<DesignBlock> {
    return block.getChildren(getStack())
}

fun Engine.getStack(): DesignBlock {
    return block.findByType(DesignBlockType.Stack).first()
}

fun Engine.getCamera(): DesignBlock {
    return block.findByType(DesignBlockType.Camera).first()
}

fun Engine.dpToCanvasUnit(dp: Float): Float {
    val sceneUnit = block.getEnum(getScene(), "scene/designUnit")
    val sceneDpi = block.getFloat(getScene(), "scene/dpi")
    val densityFactor =
        when (sceneUnit) {
            "Millimeter" -> sceneDpi / 25.4f
            "Inch" -> sceneDpi
            else -> 1f
        }
    val zoomLevel = scene.getZoomLevel()
    return dp * (block.getFloat(getCamera(), "camera/pixelRatio")) / (densityFactor * zoomLevel)
}

fun Engine.overrideAndRestore(
    designBlock: DesignBlock,
    vararg scopes: String,
    action: (DesignBlock) -> Unit,
) {
    val disabledScopes = getDisabledScopes(designBlock, *scopes)
    action(designBlock)
    restoreScopes(designBlock, disabledScopes)
}

suspend fun Engine.overrideAndRestoreAsync(
    designBlock: DesignBlock,
    vararg scopes: String,
    action: suspend (DesignBlock) -> Unit,
) {
    val disabledScopes = getDisabledScopes(designBlock, *scopes)
    action(designBlock)
    restoreScopes(designBlock, disabledScopes)
}

private fun Engine.getDisabledScopes(
    designBlock: DesignBlock,
    vararg scopes: String,
): Set<String> {
    val disabledScopes = hashSetOf<String>()
    scopes.forEach {
        val wasEnabled = block.isScopeEnabled(designBlock, it)
        if (!wasEnabled) {
            block.setScopeEnabled(designBlock, it, true)
            disabledScopes.add(it)
        }
    }
    return disabledScopes
}

private fun Engine.restoreScopes(
    designBlock: DesignBlock,
    scopes: Set<String>,
) {
    scopes.forEach {
        block.setScopeEnabled(designBlock, it, false)
    }
}
