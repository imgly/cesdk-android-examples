import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ly.img.engine.DesignBlockType
import ly.img.engine.DesignUnit
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.SceneLayout
import ly.img.engine.ShapeType
import ly.img.engine.ZoomAutoFitAxis

suspend fun modifyingScenes(engine: Engine) = withContext(engine.dispatcher) {
    // highlight-android-create-scene
    val scene = engine.scene.create(sceneLayout = SceneLayout.VERTICAL_STACK)
    // highlight-android-create-scene

    // highlight-android-create-page
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-android-create-page

    // highlight-android-create-block
    val block = engine.block.create(DesignBlockType.Graphic)
    val shape = engine.block.createShape(ShapeType.Rect)
    engine.block.setShape(block, shape = shape)
    val fill = engine.block.createFill(FillType.Color)
    engine.block.setFill(block, fill = fill)
    engine.block.setWidth(block, value = 200F)
    engine.block.setHeight(block, value = 200F)
    engine.block.appendChild(parent = page, child = block)
    // highlight-android-create-block

    // highlight-android-design-unit
    val designUnit = engine.scene.getDesignUnit()
    println("Design unit: $designUnit")

    engine.scene.setDesignUnit(DesignUnit.MILLIMETER)
    // highlight-android-design-unit

    // highlight-android-scene-layout
    engine.scene.setLayout(SceneLayout.HORIZONTAL_STACK)

    val layout = engine.scene.getLayout()
    println("Layout: $layout")
    // highlight-android-scene-layout

    // highlight-android-page-navigation
    val pages = engine.scene.getPages()
    println("Number of pages: ${pages.size}")

    val currentPage = engine.scene.getCurrentPage()
    println("Current page: $currentPage")
    // highlight-android-page-navigation

    // highlight-android-zoom-to-block
    engine.scene.zoomToBlock(
        block = page,
        paddingLeft = 20F,
        paddingTop = 20F,
        paddingRight = 20F,
        paddingBottom = 20F,
    )
    // highlight-android-zoom-to-block

    // highlight-android-zoom-level
    val zoomLevel = engine.scene.getZoomLevel()
    println("Zoom level: $zoomLevel")

    engine.scene.setZoomLevel(1F)
    // highlight-android-zoom-level

    // highlight-android-zoom-auto-fit
    engine.scene.enableZoomAutoFit(
        block = page,
        axis = ZoomAutoFitAxis.BOTH,
        paddingLeft = 20F,
        paddingTop = 20F,
        paddingRight = 20F,
        paddingBottom = 20F,
    )
    println("Auto-fit enabled: ${engine.scene.isZoomAutoFitEnabled(page)}")
    engine.scene.disableZoomAutoFit(page)
    // highlight-android-zoom-auto-fit

    // highlight-android-save-scene
    val savedScene = engine.scene.saveToString(scene = scene)
    println("Scene saved, length: ${savedScene.length}")
    // highlight-android-save-scene

    // highlight-android-load-scene
    val loadedScene = engine.scene.load(scene = savedScene)
    println("Scene loaded: $loadedScene")
    // highlight-android-load-scene

    // highlight-android-event-subscriptions
    val zoomEvents = engine.scene.onZoomLevelChanged()
        .onEach {
            println("Zoom changed: ${engine.scene.getZoomLevel()}")
        }
        .launchIn(this)

    val activeSceneEvents = engine.scene.onActiveChanged()
        .onEach {
            println("Active scene changed")
        }
        .launchIn(this)

    try {
        engine.scene.setZoomLevel(2F)
        engine.scene.load(scene = savedScene)
    } finally {
        zoomEvents.cancel()
        activeSceneEvents.cancel()
    }
    // highlight-android-event-subscriptions
}
