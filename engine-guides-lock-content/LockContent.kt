import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.GlobalScope
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode

private data class LockContentBlocks(
    val headline: DesignBlock,
    val mediaPlaceholder: DesignBlock,
    val movableBadge: DesignBlock,
)

data class LockContentResult(
    val canEditHeadline: Boolean,
    val canRestyleHeadline: Boolean,
    val canMoveHeadline: Boolean,
    val canResizeHeadline: Boolean,
    val canReplaceMedia: Boolean,
    val canChangeMediaFillType: Boolean,
    val canMoveMedia: Boolean,
    val canResizeMedia: Boolean,
    val canReplaceBadge: Boolean,
    val canChangeBadgeFillType: Boolean,
    val canMoveBadge: Boolean,
    val canResizeBadge: Boolean,
    val headlineTextScopeEnabled: Boolean,
    val textEditGlobalScope: GlobalScope,
)

data class TemporaryLockContentResult(
    val temporarilyLockedTextEditing: Boolean,
    val temporarilyEnabledSelection: Boolean,
    val globalScopesRestored: Boolean,
    val blockScopesRestored: Boolean,
)

suspend fun lockContent(
    engine: Engine,
    restoreGlobalScopes: Boolean = false,
): LockContentResult {
    val blocks = createLockContentScene(engine)

    // highlight-android-find-all-scopes
    val scopes = engine.editor.findAllScopes()
    // highlight-android-find-all-scopes
    check("editor/select" in scopes)
    check("text/edit" in scopes)
    check("fill/change" in scopes)
    check("fill/changeType" in scopes)
    check("layer/move" in scopes)

    val originalGlobalScopes = if (restoreGlobalScopes) {
        scopes.associateWith { scope -> engine.editor.getGlobalScope(key = scope) }
    } else {
        emptyMap()
    }

    try {
        // highlight-android-lock-all
        scopes.forEach { scope ->
            engine.editor.setGlobalScope(key = scope, globalScope = GlobalScope.DENY)
        }
        // highlight-android-lock-all

        // highlight-android-enable-selection
        engine.editor.setGlobalScope(key = "editor/select", globalScope = GlobalScope.DEFER)
        engine.block.setScopeEnabled(blocks.headline, key = "editor/select", enabled = true)
        engine.block.setScopeEnabled(blocks.mediaPlaceholder, key = "editor/select", enabled = true)
        engine.block.setScopeEnabled(blocks.movableBadge, key = "editor/select", enabled = true)
        // highlight-android-enable-selection

        // highlight-android-text-editing
        engine.editor.setGlobalScope(key = "text/edit", globalScope = GlobalScope.DEFER)
        engine.editor.setGlobalScope(key = "text/character", globalScope = GlobalScope.DEFER)
        engine.block.setScopeEnabled(blocks.headline, key = "text/edit", enabled = true)
        engine.block.setScopeEnabled(blocks.headline, key = "text/character", enabled = true)
        // highlight-android-text-editing

        // highlight-android-image-replacement
        engine.editor.setGlobalScope(key = "fill/change", globalScope = GlobalScope.DEFER)
        engine.editor.setGlobalScope(key = "fill/changeType", globalScope = GlobalScope.DEFER)
        engine.block.setScopeEnabled(blocks.mediaPlaceholder, key = "fill/change", enabled = true)
        engine.block.setScopeEnabled(blocks.mediaPlaceholder, key = "fill/changeType", enabled = true)
        engine.block.setScopeEnabled(blocks.movableBadge, key = "fill/change", enabled = false)
        engine.block.setScopeEnabled(blocks.movableBadge, key = "fill/changeType", enabled = false)
        // highlight-android-image-replacement

        // highlight-android-position-adjustments
        engine.editor.setGlobalScope(key = "layer/move", globalScope = GlobalScope.DEFER)
        engine.editor.setGlobalScope(key = "layer/resize", globalScope = GlobalScope.DEFER)
        listOf(blocks.headline, blocks.mediaPlaceholder).forEach { block ->
            engine.block.setScopeEnabled(block, key = "layer/move", enabled = false)
            engine.block.setScopeEnabled(block, key = "layer/resize", enabled = false)
        }
        engine.block.setScopeEnabled(blocks.movableBadge, key = "layer/move", enabled = true)
        engine.block.setScopeEnabled(blocks.movableBadge, key = "layer/resize", enabled = true)
        // highlight-android-position-adjustments

        // highlight-android-check-permissions
        val result = LockContentResult(
            canEditHeadline = engine.block.isAllowedByScope(blocks.headline, key = "text/edit"),
            canRestyleHeadline = engine.block.isAllowedByScope(blocks.headline, key = "text/character"),
            canMoveHeadline = engine.block.isAllowedByScope(blocks.headline, key = "layer/move"),
            canResizeHeadline = engine.block.isAllowedByScope(blocks.headline, key = "layer/resize"),
            canReplaceMedia = engine.block.isAllowedByScope(blocks.mediaPlaceholder, key = "fill/change"),
            canChangeMediaFillType = engine.block.isAllowedByScope(blocks.mediaPlaceholder, key = "fill/changeType"),
            canMoveMedia = engine.block.isAllowedByScope(blocks.mediaPlaceholder, key = "layer/move"),
            canResizeMedia = engine.block.isAllowedByScope(blocks.mediaPlaceholder, key = "layer/resize"),
            canReplaceBadge = engine.block.isAllowedByScope(blocks.movableBadge, key = "fill/change"),
            canChangeBadgeFillType = engine.block.isAllowedByScope(blocks.movableBadge, key = "fill/changeType"),
            canMoveBadge = engine.block.isAllowedByScope(blocks.movableBadge, key = "layer/move"),
            canResizeBadge = engine.block.isAllowedByScope(blocks.movableBadge, key = "layer/resize"),
            headlineTextScopeEnabled = engine.block.isScopeEnabled(blocks.headline, key = "text/edit"),
            textEditGlobalScope = engine.editor.getGlobalScope(key = "text/edit"),
        )

        check(result.canEditHeadline)
        check(result.canRestyleHeadline)
        check(!result.canMoveHeadline)
        check(!result.canResizeHeadline)
        check(result.canReplaceMedia)
        check(result.canChangeMediaFillType)
        check(!result.canMoveMedia)
        check(!result.canResizeMedia)
        check(!result.canReplaceBadge)
        check(!result.canChangeBadgeFillType)
        check(result.canMoveBadge)
        check(result.canResizeBadge)
        check(result.headlineTextScopeEnabled)
        check(result.textEditGlobalScope == GlobalScope.DEFER)
        // highlight-android-check-permissions

        return result
    } finally {
        originalGlobalScopes.forEach { (scope, globalScope) ->
            engine.editor.setGlobalScope(key = scope, globalScope = globalScope)
        }
    }
}

suspend fun temporarilyLockContent(engine: Engine): TemporaryLockContentResult {
    val blocks = createLockContentScene(engine)

    // highlight-android-save-scopes
    val blockScopesToRestore = listOf(
        blocks.headline to "editor/select",
        blocks.mediaPlaceholder to "editor/select",
        blocks.movableBadge to "editor/select",
        blocks.headline to "text/edit",
        blocks.headline to "text/character",
        blocks.mediaPlaceholder to "fill/change",
        blocks.mediaPlaceholder to "fill/changeType",
        blocks.movableBadge to "fill/change",
        blocks.movableBadge to "fill/changeType",
        blocks.headline to "layer/move",
        blocks.headline to "layer/resize",
        blocks.mediaPlaceholder to "layer/move",
        blocks.mediaPlaceholder to "layer/resize",
        blocks.movableBadge to "layer/move",
        blocks.movableBadge to "layer/resize",
    )

    val scopes = engine.editor.findAllScopes()
    val originalGlobalScopes = scopes.associateWith { scope ->
        engine.editor.getGlobalScope(key = scope)
    }
    val originalBlockScopes = blockScopesToRestore.map { (block, scope) ->
        Triple(
            first = block,
            second = scope,
            third = engine.block.isScopeEnabled(block, key = scope),
        )
    }
    // highlight-android-save-scopes

    val temporarilyLockedTextEditing: Boolean
    val temporarilyEnabledSelection: Boolean
    try {
        scopes.forEach { scope ->
            engine.editor.setGlobalScope(key = scope, globalScope = GlobalScope.DENY)
        }
        engine.editor.setGlobalScope(key = "editor/select", globalScope = GlobalScope.DEFER)
        engine.block.setScopeEnabled(blocks.headline, key = "editor/select", enabled = true)
        temporarilyLockedTextEditing = !engine.block.isAllowedByScope(blocks.headline, key = "text/edit")
        temporarilyEnabledSelection = engine.block.isAllowedByScope(blocks.headline, key = "editor/select")
    } finally {
        // highlight-android-restore-scopes
        originalBlockScopes.forEach { (block, scope, enabled) ->
            engine.block.setScopeEnabled(block, key = scope, enabled = enabled)
        }
        originalGlobalScopes.forEach { (scope, globalScope) ->
            engine.editor.setGlobalScope(key = scope, globalScope = globalScope)
        }
        // highlight-android-restore-scopes
    }

    return TemporaryLockContentResult(
        temporarilyLockedTextEditing = temporarilyLockedTextEditing,
        temporarilyEnabledSelection = temporarilyEnabledSelection,
        globalScopesRestored = originalGlobalScopes.all { (scope, globalScope) ->
            engine.editor.getGlobalScope(key = scope) == globalScope
        },
        blockScopesRestored = originalBlockScopes.all { (block, scope, enabled) ->
            engine.block.isScopeEnabled(block, key = scope) == enabled
        },
    )
}

private fun createLockContentScene(engine: Engine): LockContentBlocks {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val headline = engine.block.create(DesignBlockType.Text)
    engine.block.setName(headline, "Editable headline")
    engine.block.setWidthMode(headline, mode = SizeMode.AUTO)
    engine.block.setHeightMode(headline, mode = SizeMode.AUTO)
    engine.block.setPositionX(headline, value = 64F)
    engine.block.setPositionY(headline, value = 64F)
    engine.block.replaceText(headline, text = "Summer campaign")
    engine.block.setTextColor(headline, color = Color.fromHex("#FF182133"))
    engine.block.appendChild(parent = page, child = headline)

    val mediaPlaceholder = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(mediaPlaceholder, "Replaceable image placeholder")
    engine.block.setShape(mediaPlaceholder, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(mediaPlaceholder, value = 280F)
    engine.block.setHeight(mediaPlaceholder, value = 180F)
    engine.block.setPositionX(mediaPlaceholder, value = 64F)
    engine.block.setPositionY(mediaPlaceholder, value = 180F)
    engine.block.setFill(mediaPlaceholder, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(mediaPlaceholder, color = Color.fromHex("#FFE6EEF8"))
    engine.block.appendChild(parent = page, child = mediaPlaceholder)

    val movableBadge = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(movableBadge, "Movable badge")
    engine.block.setShape(movableBadge, shape = engine.block.createShape(ShapeType.Ellipse))
    engine.block.setWidth(movableBadge, value = 140F)
    engine.block.setHeight(movableBadge, value = 140F)
    engine.block.setPositionX(movableBadge, value = 420F)
    engine.block.setPositionY(movableBadge, value = 220F)
    engine.block.setFill(movableBadge, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(movableBadge, color = Color.fromHex("#FFFFC857"))
    engine.block.appendChild(parent = page, child = movableBadge)

    return LockContentBlocks(
        headline = headline,
        mediaPlaceholder = mediaPlaceholder,
        movableBadge = movableBadge,
    )
}
