import android.net.Uri
import ly.img.editor.defaultBaseUri
import ly.img.engine.Engine
import java.io.File
import kotlin.math.abs

private const val CURRENT_PAGE_WIDTH = 1080F
private const val CURRENT_PAGE_HEIGHT = 1350F
private const val PAGE_SIZE_TOLERANCE = 0.01F

private val templateSceneUri: Uri
    get() = defaultBaseUri.buildUpon()
        .appendPath("ly.img.templates")
        .appendPath("templates")
        .appendPath("cesdk_business_card_1.scene")
        .build()

data class ImportTemplatesResult(
    val loadedPageCount: Int,
    val appliedFromUriPageSize: PageSize,
    val appliedFromStringPageSize: PageSize,
)

data class PageSize(
    val width: Float,
    val height: Float,
)

suspend fun importTemplates(engine: Engine): ImportTemplatesResult {
    val sceneUri = templateSceneUri
    val archiveFile = File.createTempFile("imported-template", ".zip")

    try {
        // highlight-android-load-from-url
        val sceneFromUrl = engine.scene.load(
            sceneUri = sceneUri,
            waitForResources = true,
        )
        // highlight-android-load-from-url

        val templateString = engine.scene.saveToString(scene = sceneFromUrl)
        val archiveBuffer = engine.scene.saveToArchive(scene = sceneFromUrl)
        archiveBuffer.rewind()
        archiveFile.outputStream().channel.use { channel ->
            while (archiveBuffer.hasRemaining()) {
                channel.write(archiveBuffer)
            }
        }
        val archiveUri = Uri.fromFile(archiveFile)

        // highlight-android-load-from-archive
        engine.scene.loadArchive(
            archiveUri = archiveUri,
            waitForResources = true,
        )
        // highlight-android-load-from-archive

        // highlight-android-load-from-string
        engine.scene.load(
            scene = templateString,
            waitForResources = true,
        )
        // highlight-android-load-from-string

        // highlight-android-get-scene
        val loadedScene = requireNotNull(engine.scene.get()) {
            "Template scene was not loaded."
        }
        val pages = engine.scene.getPages()
        check(pages.isNotEmpty()) { "Template did not contain any pages." }
        // highlight-android-get-scene

        val currentPage = pages.first()
        engine.block.setFloat(
            block = loadedScene,
            property = "scene/pageDimensions/width",
            value = CURRENT_PAGE_WIDTH,
        )
        engine.block.setFloat(
            block = loadedScene,
            property = "scene/pageDimensions/height",
            value = CURRENT_PAGE_HEIGHT,
        )
        engine.block.setWidth(block = currentPage, value = CURRENT_PAGE_WIDTH)
        engine.block.setHeight(block = currentPage, value = CURRENT_PAGE_HEIGHT)

        // highlight-android-apply-template-uri
        engine.scene.applyTemplate(templateUri = sceneUri)
        // highlight-android-apply-template-uri

        val appliedFromUriPage = engine.scene.getPages().first()
        val appliedFromUriPageSize = PageSize(
            width = engine.block.getWidth(appliedFromUriPage),
            height = engine.block.getHeight(appliedFromUriPage),
        )
        check(abs(appliedFromUriPageSize.width - CURRENT_PAGE_WIDTH) < PAGE_SIZE_TOLERANCE) {
            "Applied URI template width ${appliedFromUriPageSize.width} did not preserve $CURRENT_PAGE_WIDTH."
        }
        check(abs(appliedFromUriPageSize.height - CURRENT_PAGE_HEIGHT) < PAGE_SIZE_TOLERANCE) {
            "Applied URI template height ${appliedFromUriPageSize.height} did not preserve $CURRENT_PAGE_HEIGHT."
        }

        // highlight-android-apply-template-string
        engine.scene.applyTemplate(template = templateString)
        // highlight-android-apply-template-string

        val appliedFromStringPage = engine.scene.getPages().first()
        val appliedFromStringPageSize = PageSize(
            width = engine.block.getWidth(appliedFromStringPage),
            height = engine.block.getHeight(appliedFromStringPage),
        )
        check(abs(appliedFromStringPageSize.width - CURRENT_PAGE_WIDTH) < PAGE_SIZE_TOLERANCE) {
            "Applied string template width ${appliedFromStringPageSize.width} did not preserve $CURRENT_PAGE_WIDTH."
        }
        check(abs(appliedFromStringPageSize.height - CURRENT_PAGE_HEIGHT) < PAGE_SIZE_TOLERANCE) {
            "Applied string template height ${appliedFromStringPageSize.height} did not preserve $CURRENT_PAGE_HEIGHT."
        }

        // highlight-android-zoom-to-scene
        val currentScene = requireNotNull(engine.scene.get()) {
            "Applied template scene was not available."
        }
        engine.scene.zoomToBlock(
            block = currentScene,
            paddingLeft = 40F,
            paddingTop = 40F,
            paddingRight = 40F,
            paddingBottom = 40F,
        )
        // highlight-android-zoom-to-scene

        return ImportTemplatesResult(
            loadedPageCount = pages.size,
            appliedFromUriPageSize = appliedFromUriPageSize,
            appliedFromStringPageSize = appliedFromStringPageSize,
        )
    } finally {
        archiveFile.delete()
    }
}
