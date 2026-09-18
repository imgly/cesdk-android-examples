import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.UnstableEngineApi
import ly.img.engine.ZoomAutoFitAxis

data class SetZoomLevelSummary(
    val zoom100: Float,
    val zoom50: Float,
    val autoFitEnabled: Boolean,
    val autoFitDisabled: Boolean,
    val zoomClampingEnabled: Boolean,
    val positionClampingEnabled: Boolean,
    val finalZoom: Float,
)

@OptIn(UnstableEngineApi::class)
suspend fun setZoomLevel(engine: Engine): SetZoomLevelSummary = coroutineScope {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    // highlight-android-get-set-zoom-level
    engine.scene.setZoomLevel(level = 1F)
    val zoom100 = engine.scene.getZoomLevel()

    engine.scene.setZoomLevel(level = zoom100 * 0.5F)
    val zoom50 = engine.scene.getZoomLevel()
    // highlight-android-get-set-zoom-level

    // highlight-android-zoom-to-block
    engine.scene.zoomToBlock(
        block = page,
        paddingLeft = 20F,
        paddingTop = 20F,
        paddingRight = 20F,
        paddingBottom = 20F,
    )

    engine.scene.immediateZoomToBlock(
        block = page,
        paddingLeft = 20F,
        paddingTop = 20F,
        paddingRight = 20F,
        paddingBottom = 20F,
        forceUpdate = true,
    )
    // highlight-android-zoom-to-block

    // highlight-android-auto-fit-zoom
    engine.scene.enableZoomAutoFit(
        block = page,
        axis = ZoomAutoFitAxis.BOTH,
        paddingLeft = 20F,
        paddingTop = 20F,
        paddingRight = 20F,
        paddingBottom = 20F,
    )
    val autoFitEnabled = engine.scene.isZoomAutoFitEnabled(page)
    // highlight-android-auto-fit-zoom

    // highlight-android-disable-auto-fit
    engine.scene.disableZoomAutoFit(page)
    val autoFitDisabled = engine.scene.isZoomAutoFitEnabled(page).not()
    // highlight-android-disable-auto-fit

    val zoomClampingEnabled = configureZoomClamping(engine = engine, page = page)
    val positionClampingEnabled = configurePositionClamping(engine = engine, scene = scene)

    engine.scene.setZoomLevel(level = 2F)
    val finalZoom = engine.scene.getZoomLevel()

    val summary = SetZoomLevelSummary(
        zoom100 = zoom100,
        zoom50 = zoom50,
        autoFitEnabled = autoFitEnabled,
        autoFitDisabled = autoFitDisabled,
        zoomClampingEnabled = zoomClampingEnabled,
        positionClampingEnabled = positionClampingEnabled,
        finalZoom = finalZoom,
    )

    engine.scene.disableCameraZoomClamping()
    engine.scene.disableCameraPositionClamping()

    summary
}

// highlight-android-zoom-clamping
@OptIn(UnstableEngineApi::class)
fun configureZoomClamping(
    engine: Engine,
    page: DesignBlock,
): Boolean {
    engine.scene.enableCameraZoomClamping(
        blocks = listOf(page),
        minZoomLimit = 0.125F,
        maxZoomLimit = 8F,
    )
    val zoomClampingEnabled = engine.scene.isCameraZoomClampingEnabled(page)
    return zoomClampingEnabled
}
// highlight-android-zoom-clamping

// highlight-android-subscribe-zoom-changes
fun observeZoomLevel(
    engine: Engine,
    zoomControlScope: CoroutineScope,
    onZoomChanged: (Float) -> Unit,
): Job = engine.scene.onZoomLevelChanged()
    .onEach {
        onZoomChanged(engine.scene.getZoomLevel())
    }
    .launchIn(zoomControlScope)
// highlight-android-subscribe-zoom-changes

suspend fun verifyZoomChangeSubscription(
    engine: Engine,
    zoomControlScope: CoroutineScope,
    pumpEngine: () -> Unit,
): Float {
    val observedEventCount = CompletableDeferred<Int>()
    val observedZoomLevels = mutableListOf<Float>()
    val zoomEvents = observeZoomLevel(
        engine = engine,
        zoomControlScope = zoomControlScope,
    ) { zoomLevel ->
        observedZoomLevels += zoomLevel
        if (observedZoomLevels.size == 3) {
            observedEventCount.complete(observedZoomLevels.size)
        }
    }

    // The offscreen smoke test has no render loop, so yield until the Flow is
    // subscribed and manually pump the engine after each zoom change.
    yield()
    pumpEngine()
    listOf(1F, 2F, 3F).forEach { zoomLevel ->
        engine.scene.setZoomLevel(level = zoomLevel)
        pumpEngine()
        yield()
    }
    withTimeout(5_000) {
        while (isActive && observedEventCount.isCompleted.not()) {
            pumpEngine()
            yield()
        }
        observedEventCount.await()
    }
    zoomEvents.cancel()
    return observedZoomLevels.last()
}

// highlight-android-position-clamping
@OptIn(UnstableEngineApi::class)
fun configurePositionClamping(
    engine: Engine,
    scene: DesignBlock,
): Boolean {
    engine.scene.enableCameraPositionClamping(
        blocks = listOf(scene),
        paddingLeft = 10F,
        paddingTop = 10F,
        paddingRight = 10F,
        paddingBottom = 10F,
        scaledPaddingLeft = 0F,
        scaledPaddingTop = 0F,
        scaledPaddingRight = 0F,
        scaledPaddingBottom = 0F,
    )
    val positionClampingEnabled = engine.scene.isCameraPositionClampingEnabled(scene)
    return positionClampingEnabled
}
// highlight-android-position-clamping
