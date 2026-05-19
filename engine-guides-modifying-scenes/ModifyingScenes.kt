import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ly.img.engine.DesignBlockType
import ly.img.engine.DesignUnit
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.SceneLayout
import ly.img.engine.ShapeType
import ly.img.engine.ZoomAutoFitAxis

fun modifyingScenes(
    license: String?, // pass null or empty for evaluation mode with watermark
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 1080, height = 1920)

    // highlight-create-scene
    val scene = engine.scene.create(sceneLayout = SceneLayout.VERTICAL_STACK)
    // highlight-create-scene

    // highlight-create-page
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)
    // highlight-create-page

    // highlight-create-block
    val block = engine.block.create(DesignBlockType.Graphic)
    val shape = engine.block.createShape(ShapeType.Rect)
    engine.block.setShape(block, shape = shape)
    val fill = engine.block.createFill(FillType.Color)
    engine.block.setFill(block, fill = fill)
    engine.block.setWidth(block, value = 200F)
    engine.block.setHeight(block, value = 200F)
    engine.block.appendChild(parent = page, child = block)
    // highlight-create-block

    // highlight-design-unit
    val designUnit = engine.scene.getDesignUnit()
    println("Design unit: $designUnit")

    engine.scene.setDesignUnit(DesignUnit.MILLIMETER)
    // highlight-design-unit

    // highlight-scene-layout
    engine.scene.setLayout(SceneLayout.HORIZONTAL_STACK)

    val layout = engine.scene.getLayout()
    println("Layout: $layout")
    // highlight-scene-layout

    // highlight-page-navigation
    val pages = engine.scene.getPages()
    println("Number of pages: ${pages.size}")

    val currentPage = engine.scene.getCurrentPage()
    println("Current page: $currentPage")
    // highlight-page-navigation

    // highlight-zoom-to-block
    engine.scene.zoomToBlock(
        block = page,
        paddingLeft = 20F,
        paddingTop = 20F,
        paddingRight = 20F,
        paddingBottom = 20F,
    )
    // highlight-zoom-to-block

    // highlight-zoom-level
    val zoomLevel = engine.scene.getZoomLevel()
    println("Zoom level: $zoomLevel")

    engine.scene.setZoomLevel(1F)
    // highlight-zoom-level

    // highlight-zoom-auto-fit
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
    // highlight-zoom-auto-fit

    // highlight-save-scene
    val savedScene = engine.scene.saveToString(scene = scene)
    println("Scene saved, length: ${savedScene.length}")
    // highlight-save-scene

    // highlight-load-scene
    val loadedScene = engine.scene.load(scene = savedScene)
    println("Scene loaded: $loadedScene")
    // highlight-load-scene

    // highlight-event-subscriptions
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

    engine.scene.setZoomLevel(2F)
    engine.scene.load(scene = savedScene)

    zoomEvents.cancel()
    activeSceneEvents.cancel()
    // highlight-event-subscriptions

    engine.stop()
}
