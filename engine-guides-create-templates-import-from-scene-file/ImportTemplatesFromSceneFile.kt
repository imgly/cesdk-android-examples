import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.DesignUnit
import ly.img.engine.Engine
import ly.img.engine.MimeType
import ly.img.engine.SceneLayout
import java.io.File

suspend fun importTemplatesFromSceneFile(engine: Engine): ImportedTemplateSummary {
    // highlight-android-template-source
    val cdnAssetsBaseUri = ly.img.editor.defaultBaseUri
    engine.editor.setSettingString("basePath", cdnAssetsBaseUri.toString())

    val sceneUri = Uri.parse(
        "$cdnAssetsBaseUri/ly.img.templates/templates/cesdk_business_card_1.scene",
    )
    // highlight-android-template-source

    // highlight-android-load-from-url
    val sceneFromUrl = engine.scene.load(
        sceneUri = sceneUri,
        waitForResources = true,
    )
    // highlight-android-load-from-url

    val sceneUrlPageCount = engine.scene.getPages().size

    // highlight-android-load-from-string
    val templateString = engine.scene.saveToString(scene = sceneFromUrl)
    val sceneFromString = engine.scene.load(
        scene = templateString,
        waitForResources = true,
    )
    // highlight-android-load-from-string

    check(engine.scene.get() == sceneFromString)
    val stringPageCount = engine.scene.getPages().size

    // highlight-android-prepare-archive-uri
    val templateArchiveFile = File.createTempFile("template", ".zip")
    val templateArchive = engine.scene.saveToArchive(scene = sceneFromString)
    withContext(Dispatchers.IO) {
        templateArchiveFile.outputStream().use { output ->
            val archiveBuffer = templateArchive.asReadOnlyBuffer()
            val channel = output.channel
            while (archiveBuffer.hasRemaining()) {
                channel.write(archiveBuffer)
            }
        }
    }
    // highlight-android-prepare-archive-uri

    // highlight-android-load-from-archive
    val archiveUri = Uri.fromFile(templateArchiveFile)
    val sceneFromArchive = engine.scene.loadArchive(
        archiveUri = archiveUri,
        waitForResources = true,
    )
    // highlight-android-load-from-archive

    check(engine.scene.get() == sceneFromArchive)
    val archivePageCount = engine.scene.getPages().size

    // highlight-android-apply-template
    val existingScene = engine.scene.create(
        designUnit = DesignUnit.PIXEL,
        sceneLayout = SceneLayout.FREE,
    )
    engine.block.setBoolean(block = existingScene, property = "scene/aspectRatioLock", value = false)
    engine.block.setFloat(block = existingScene, property = "scene/pageDimensions/width", value = 1920F)
    engine.block.setFloat(block = existingScene, property = "scene/pageDimensions/height", value = 1080F)

    engine.scene.applyTemplate(templateUri = sceneUri)

    val appliedPage = engine.scene.getPages().first()
    val appliedPageWidth = engine.block.getWidth(appliedPage)
    val appliedPageHeight = engine.block.getHeight(appliedPage)
    // highlight-android-apply-template

    val previewPngData = engine.block.export(
        block = appliedPage,
        mimeType = MimeType.PNG,
    )

    // highlight-android-inspect-scene
    val activeScene = requireNotNull(engine.scene.get())
    val appliedPageCount = engine.scene.getPages().size
    val designUnit = engine.scene.getDesignUnit()

    engine.scene.zoomToBlock(
        block = activeScene,
        paddingLeft = 40F,
        paddingTop = 40F,
        paddingRight = 40F,
        paddingBottom = 40F,
    )
    // highlight-android-inspect-scene

    return ImportedTemplateSummary(
        sceneUrlPageCount = sceneUrlPageCount,
        stringPageCount = stringPageCount,
        archivePageCount = archivePageCount,
        appliedPageCount = appliedPageCount,
        appliedPageWidth = appliedPageWidth,
        appliedPageHeight = appliedPageHeight,
        designUnit = designUnit,
        previewPngData = previewPngData,
    )
}
