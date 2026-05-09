// highlight-movement-constraints
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.MovementConstraintRule
import ly.img.engine.MovementConstraintScope

fun movementConstraints(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    // highlight-movement-constraint-setup
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val block = engine.block.create(DesignBlockType.Graphic)
    engine.block.appendChild(parent = page, child = block)
    // highlight-movement-constraint-setup

    // highlight-movement-constraint-scene-wide
    // Allow every block in the scene to overshoot by 20% of its own size.
    engine.editor.setMovementConstraint(MovementConstraintRule(overshoot = 0.2F))
    // highlight-movement-constraint-scene-wide

    // highlight-movement-constraint-per-type
    // Pin all text and caption blocks fully inside the page.
    engine.editor.setMovementConstraint(
        listOf(
            MovementConstraintRule(overshoot = 0F, scope = MovementConstraintScope.BlockType("text")),
            MovementConstraintRule(overshoot = 0F, scope = MovementConstraintScope.BlockType("caption")),
        ),
    )
    // highlight-movement-constraint-per-type

    // highlight-movement-constraint-per-page
    // Override the scene-wide default for blocks on this page.
    engine.editor.setMovementConstraint(
        MovementConstraintRule(overshoot = 0.1F, scope = MovementConstraintScope.Block(page)),
    )
    // highlight-movement-constraint-per-page

    // highlight-movement-constraint-per-block
    // Override every other level for one specific block.
    engine.editor.setMovementConstraint(
        MovementConstraintRule(overshoot = 0F, scope = MovementConstraintScope.Block(block)),
    )
    // highlight-movement-constraint-per-block

    // highlight-movement-constraint-read
    // Read the resolved constraint, walking the priority chain:
    // block > parent page > blockType > scene-wide.
    val active = engine.editor.getMovementConstraint(block)
    // highlight-movement-constraint-read

    // highlight-movement-constraint-remove
    // Clear a scope by passing the matching descriptor. Use no argument to remove
    // the scene-wide default.
    engine.editor.removeMovementConstraint(MovementConstraintScope.Block(block)) // per-block
    engine.editor.removeMovementConstraint(MovementConstraintScope.BlockType("text")) // per-type
    engine.editor.removeMovementConstraint(MovementConstraintScope.Block(page)) // per-page
    engine.editor.removeMovementConstraint() // scene-wide default
    // highlight-movement-constraint-remove

    engine.stop()
}

// highlight-movement-constraints
