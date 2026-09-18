import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

@Suppress("UNUSED_VARIABLE")
suspend fun architecture(engine: Engine) = withContext(engine.dispatcher) {
    var subscription: Job? = null
    val variableKey = "username"
    val hadPreviousVariable = variableKey in engine.variable.findAll()
    val previousVariable = if (hadPreviousVariable) engine.variable.get(variableKey) else null

    try {
        // highlight-android-architecture-apis
        // The engine exposes six API namespaces:
        engine.scene // Scene API — content hierarchy
        engine.block // Block API — create and modify blocks
        engine.asset // Asset API — manage asset sources
        engine.editor // Editor API — edit modes, undo/redo, roles
        engine.event // Event API — subscribe to changes
        engine.variable // Variable API — template variables
        // highlight-android-architecture-apis

        // highlight-android-architecture-hierarchy
        // Create a scene with a page and a graphic block.
        val scene = engine.scene.create()
        val page = engine.block.create(DesignBlockType.Page)
        engine.block.appendChild(parent = scene, child = page)

        val block = engine.block.create(DesignBlockType.Graphic)
        engine.block.setShape(block, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setFill(block, fill = engine.block.createFill(FillType.Color))
        engine.block.appendChild(parent = page, child = block)

        // Traverse the hierarchy.
        val pages = engine.scene.getPages()
        val children = engine.block.getChildren(block = pages.first())
        // highlight-android-architecture-hierarchy

        // highlight-android-architecture-scene-modes
        // Scenes use the same hierarchy for static and time-based experiences.
        val contentScene = engine.scene.create()
        val contentPage = engine.block.create(DesignBlockType.Page)
        engine.block.appendChild(parent = contentScene, child = contentPage)
        // highlight-android-architecture-scene-modes

        // highlight-android-architecture-events
        // Subscribe to block changes using Flow.
        subscription =
            engine.event.subscribe(blocks = listOf(scene))
                .onEach { events ->
                    events.forEach { event ->
                        println("Block ${event.block} had event: ${event.type}")
                    }
                }
                // `this` is the surrounding coroutine scope.
                .launchIn(this)
        // highlight-android-architecture-events

        // highlight-android-architecture-variables
        // Set and retrieve template variables.
        engine.variable.set(key = "username", value = "Jane")
        val username = engine.variable.get(key = "username")
        // highlight-android-architecture-variables
    } finally {
        subscription?.cancel()
        if (hadPreviousVariable) {
            engine.variable.set(key = variableKey, value = checkNotNull(previousVariable))
        } else if (variableKey in engine.variable.findAll()) {
            engine.variable.remove(variableKey)
        }
    }
}
