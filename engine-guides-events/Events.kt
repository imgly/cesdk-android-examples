package ly.img.editor.examples.engine.guides.events

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlockEvent
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

suspend fun events(engine: Engine) = withContext(engine.dispatcher) {
    // highlight-android-setup
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-android-setup

    // highlight-android-subscribe-all
    val allBlocksSubscription = engine.event.subscribe(blocks = emptyList())
        .onEach { events ->
            events.forEach { event ->
                println("[All Blocks] ${event.type} event for block ${event.block}")
            }
        }.launchIn(this)
    // highlight-android-subscribe-all

    // highlight-android-event-created
    val graphic = engine.block.create(DesignBlockType.Graphic)
    val rectShape = engine.block.createShape(ShapeType.Rect)
    engine.block.setShape(block = graphic, shape = rectShape)
    engine.block.setPositionX(graphic, value = 200F)
    engine.block.setPositionY(graphic, value = 150F)
    engine.block.setWidth(graphic, value = 400F)
    engine.block.setHeight(graphic, value = 300F)

    val imageFill = engine.block.createFill(FillType.Image)
    engine.block.setString(
        block = imageFill,
        property = "fill/image/imageFileURI",
        value = "https://img.ly/static/ubq_samples/sample_1.jpg",
    )
    engine.block.setFill(block = graphic, fill = imageFill)
    engine.block.setEnum(
        block = graphic,
        property = "contentFill/mode",
        value = "Cover",
    )
    engine.block.appendChild(parent = page, child = graphic)
    // highlight-android-event-created

    engine.block.forceLoadResources(listOf(graphic))

    // highlight-android-subscribe-specific
    val specificBlocksSubscription = engine.event.subscribe(blocks = listOf(graphic))
        .onEach { events ->
            events.forEach { event ->
                println("[Specific Block] ${event.type} event for block ${event.block}")
            }
        }.launchIn(this)
    // highlight-android-subscribe-specific

    // highlight-android-process-events
    val processedEventsSubscription = engine.event.subscribe(blocks = emptyList())
        .onEach { events ->
            events.forEach { event ->
                when (event.type) {
                    DesignBlockEvent.Type.CREATED -> {
                        val blockType = engine.block.getType(event.block)
                        println("Block created with type: $blockType")
                    }

                    DesignBlockEvent.Type.UPDATED -> {
                        println("Block ${event.block} was updated")
                    }

                    DesignBlockEvent.Type.DESTROYED -> {
                        println("Block ${event.block} was destroyed")
                    }
                }
            }
        }.launchIn(this)
    // highlight-android-process-events

    try {
        // highlight-android-destroyed-safety
        val cachedBlocks = mutableSetOf(graphic)
        cachedBlocks.removeAll { block -> !engine.block.isValid(block) }
        // highlight-android-destroyed-safety

        // highlight-android-event-updated
        engine.block.setRotation(graphic, radians = 0.1F)
        engine.block.setFloat(
            block = graphic,
            property = "opacity",
            value = 0.9F,
        )
        // highlight-android-event-updated

        // highlight-android-event-destroyed
        engine.block.destroy(graphic)
        println("Destroyed graphic block $graphic")
        // highlight-android-event-destroyed

        // Let the guide test collect the final batched events before cleanup.
        delay(1_000)
    } finally {
        withContext(NonCancellable) {
            // highlight-android-unsubscribe
            allBlocksSubscription.cancelAndJoin()
            specificBlocksSubscription.cancelAndJoin()
            processedEventsSubscription.cancelAndJoin()
            // highlight-android-unsubscribe
        }
    }
}
