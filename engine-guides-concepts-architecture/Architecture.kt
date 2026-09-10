import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

@Suppress("UNUSED_VARIABLE")
fun architecture(
    license: String? = null, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    var subscription: Job? = null
    var engineStarted = false

    try {
        engine.start(license = license, userId = userId)
        engineStarted = true
        engine.bindOffscreen(width = 1080, height = 1920)

        // highlight-architecture-apis
        // The engine exposes six API namespaces:
        engine.scene // Scene API — content hierarchy
        engine.block // Block API — create and modify blocks
        engine.asset // Asset API — manage asset sources
        engine.editor // Editor API — edit modes, undo/redo, roles
        engine.event // Event API — subscribe to changes
        engine.variable // Variable API — template variables
        // highlight-architecture-apis

        // highlight-architecture-hierarchy
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
        // highlight-architecture-hierarchy

        // highlight-architecture-sceneModes
        // Scenes use the same hierarchy for static and time-based experiences.
        val contentScene = engine.scene.create()
        val contentPage = engine.block.create(DesignBlockType.Page)
        engine.block.appendChild(parent = contentScene, child = contentPage)
        // highlight-architecture-sceneModes

        // highlight-architecture-events
        // Subscribe to block changes using Flow.
        subscription =
            engine.event.subscribe(blocks = listOf(scene))
                .onEach { events ->
                    events.forEach { event ->
                        println("Block ${event.block} had event: ${event.type}")
                    }
                }
                // `this` is the CoroutineScope from the surrounding launch block.
                .launchIn(this)
        // highlight-architecture-events

        // highlight-architecture-variables
        // Set and retrieve template variables.
        engine.variable.set(key = "username", value = "Jane")
        val username = engine.variable.get(key = "username")
        // highlight-architecture-variables
    } finally {
        subscription?.cancel()
        if (engineStarted) {
            engine.stop()
        }
    }
}
