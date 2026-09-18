import android.util.Log
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.GlobalScope
import ly.img.engine.ShapeType

private const val MOVE_SCOPE = "layer/move"
private const val RESIZE_SCOPE = "layer/resize"
private const val DESTROY_SCOPE = "lifecycle/destroy"
private const val DUPLICATE_SCOPE = "lifecycle/duplicate"

suspend fun setEditingConstraints(engine: Engine): SetEditingConstraintsResult {
    // Demo scaffolding: a scene and page to hold the constrained blocks.
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 1200F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    // highlight-android-global-scopes
    // Keep these scopes deferred so the returned scene uses block-level constraints.
    engine.editor.setGlobalScope(key = "layer/move", globalScope = GlobalScope.DEFER)
    engine.editor.setGlobalScope(key = "layer/resize", globalScope = GlobalScope.DEFER)
    engine.editor.setGlobalScope(key = "lifecycle/destroy", globalScope = GlobalScope.DEFER)
    engine.editor.setGlobalScope(key = "lifecycle/duplicate", globalScope = GlobalScope.DEFER)
    // highlight-android-global-scopes

    // Demo scaffolding: two renderable graphic blocks, constrained independently.
    val positionLocked = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(positionLocked, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(positionLocked, fill = engine.block.createFill(FillType.Color))
    engine.block.setWidth(positionLocked, value = 200F)
    engine.block.setHeight(positionLocked, value = 200F)
    engine.block.appendChild(parent = page, child = positionLocked)

    val deletionLocked = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(deletionLocked, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(deletionLocked, fill = engine.block.createFill(FillType.Color))
    engine.block.setWidth(deletionLocked, value = 200F)
    engine.block.setHeight(deletionLocked, value = 200F)
    engine.block.appendChild(parent = page, child = deletionLocked)

    engine.block.setScopeEnabled(positionLocked, key = "lifecycle/destroy", enabled = true)
    engine.block.setScopeEnabled(positionLocked, key = "lifecycle/duplicate", enabled = true)

    // highlight-android-lock-position
    engine.block.setScopeEnabled(positionLocked, key = "layer/resize", enabled = true)
    engine.block.setScopeEnabled(positionLocked, key = "layer/move", enabled = false)
    // highlight-android-lock-position

    // highlight-android-prevent-deletion
    engine.block.setScopeEnabled(deletionLocked, key = "layer/move", enabled = true)
    engine.block.setScopeEnabled(deletionLocked, key = "layer/resize", enabled = true)
    engine.block.setScopeEnabled(deletionLocked, key = "lifecycle/destroy", enabled = false)
    engine.block.setScopeEnabled(deletionLocked, key = "lifecycle/duplicate", enabled = false)
    // highlight-android-prevent-deletion

    // highlight-android-check-scope
    val moveScopeEnabled = engine.block.isScopeEnabled(positionLocked, key = "layer/move")
    Log.i("SetEditingConstraints", "layer/move enabled at block level: $moveScopeEnabled")
    // highlight-android-check-scope

    // highlight-android-check-allowed
    val moveAllowed = engine.block.isAllowedByScope(positionLocked, key = "layer/move")
    Log.i("SetEditingConstraints", "layer/move allowed: $moveAllowed")
    // highlight-android-check-allowed

    val resizeAllowed = engine.block.isAllowedByScope(positionLocked, key = RESIZE_SCOPE)
    val positionLockedDestroyAllowed = engine.block.isAllowedByScope(positionLocked, key = DESTROY_SCOPE)
    val positionLockedDuplicateAllowed = engine.block.isAllowedByScope(positionLocked, key = DUPLICATE_SCOPE)
    val destroyAllowed = engine.block.isAllowedByScope(deletionLocked, key = DESTROY_SCOPE)
    val duplicateAllowed = engine.block.isAllowedByScope(deletionLocked, key = DUPLICATE_SCOPE)
    val deletionLockedMoveAllowed = engine.block.isAllowedByScope(deletionLocked, key = MOVE_SCOPE)

    return SetEditingConstraintsResult(
        availableScopes = engine.editor.findAllScopes(),
        moveScopeEnabled = moveScopeEnabled,
        moveAllowed = moveAllowed,
        resizeAllowed = resizeAllowed,
        positionLockedDestroyAllowed = positionLockedDestroyAllowed,
        positionLockedDuplicateAllowed = positionLockedDuplicateAllowed,
        destroyAllowed = destroyAllowed,
        duplicateAllowed = duplicateAllowed,
        deletionLockedMoveAllowed = deletionLockedMoveAllowed,
    )
}
