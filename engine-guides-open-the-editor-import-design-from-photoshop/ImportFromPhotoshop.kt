import android.net.Uri
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

suspend fun importFromPhotoshop(
    engine: Engine,
    archiveUri: Uri,
): DesignBlock {
    // highlight-android-load-converted-archive
    val scene = engine.scene.loadArchive(
        archiveUri = archiveUri,
        waitForResources = true,
    )
    // highlight-android-load-converted-archive

    // highlight-android-verify-import
    check(engine.scene.getPages().isNotEmpty()) {
        "The converted Photoshop archive contains no pages."
    }
    // highlight-android-verify-import

    // highlight-android-fit-viewport
    engine.scene.zoomToBlock(
        block = scene,
        paddingLeft = 40F,
        paddingTop = 40F,
        paddingRight = 40F,
        paddingBottom = 40F,
    )
    // highlight-android-fit-viewport

    return scene
}
