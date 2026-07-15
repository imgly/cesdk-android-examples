import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.CompressionFormat
import ly.img.engine.CompressionLevel
import ly.img.engine.CompressionOptions
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.SaveToStringOptions
import ly.img.engine.ShapeType
import java.io.File
import java.io.FileOutputStream

suspend fun saveDesigns(
    engine: Engine,
    outputDir: File,
): SaveDesigns {
    withContext(Dispatchers.IO) {
        outputDir.mkdirs()
    }

    val scene = engine.scene.create()
    engine.block.setName(scene, name = "Spring Campaign")

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setName(page, name = "Campaign Cover")
    engine.block.setWidth(page, value = 1080F)
    engine.block.setHeight(page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)

    val background = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(background, name = "Background Panel")
    engine.block.setShape(background, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(background, value = 1080F)
    engine.block.setHeight(background, value = 1080F)
    engine.block.setFill(background, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(block = background, color = Color.fromHex("#FFF4F7FB"))
    engine.block.appendChild(parent = page, child = background)

    val badge = engine.block.create(DesignBlockType.Graphic)
    engine.block.setName(badge, name = "Reusable Badge")
    engine.block.setShape(badge, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(badge, value = 330F)
    engine.block.setPositionY(badge, value = 390F)
    engine.block.setWidth(badge, value = 420F)
    engine.block.setHeight(badge, value = 300F)
    engine.block.setFill(badge, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(block = badge, color = Color.fromHex("#FF2156D9"))
    engine.block.appendChild(parent = page, child = badge)

    // highlight-android-save-to-string
    val sceneString = engine.scene.saveToString(
        scene = scene,
        allowedResourceSchemes = listOf("bundle", "file", "http", "https"),
    )
    // highlight-android-save-to-string
    check(sceneString.isNotBlank())

    // highlight-android-save-to-archive
    val sceneArchive = engine.scene.saveToArchive(scene = scene)
    // highlight-android-save-to-archive

    // highlight-android-write-to-disk
    val sceneFile = File(outputDir, "spring-campaign.scene")
    val sceneArchiveFile = File(outputDir, "spring-campaign.zip")

    withContext(Dispatchers.IO) {
        sceneFile.bufferedWriter(Charsets.UTF_8).use { writer ->
            writer.write(sceneString)
        }

        FileOutputStream(sceneArchiveFile).channel.use { channel ->
            val archiveBuffer = sceneArchive.asReadOnlyBuffer()
            while (archiveBuffer.hasRemaining()) {
                channel.write(archiveBuffer)
            }
        }
    }
    // highlight-android-write-to-disk
    check(sceneFile.length() > 0L)
    check(sceneArchiveFile.length() > 0L)

    // highlight-android-compression
    val compressedSceneString = engine.scene.saveToString(
        scene = scene,
        options = SaveToStringOptions(
            allowedResourceSchemes = listOf("bundle", "file", "http", "https"),
            compression = CompressionOptions(
                format = CompressionFormat.ZSTD,
                level = CompressionLevel.DEFAULT,
            ),
        ),
    )
    // highlight-android-compression
    check(compressedSceneString.isNotBlank())

    // highlight-android-save-blocks
    val blockString = engine.block.saveToString(blocks = listOf(badge))
    val blockArchive = engine.block.saveToArchive(blocks = listOf(badge))
    // highlight-android-save-blocks

    val blockArchiveFile = File(outputDir, "reusable-badge.zip")
    withContext(Dispatchers.IO) {
        FileOutputStream(blockArchiveFile).channel.use { channel ->
            val archiveBuffer = blockArchive.asReadOnlyBuffer()
            while (archiveBuffer.hasRemaining()) {
                channel.write(archiveBuffer)
            }
        }
    }
    check(blockString.isNotBlank())
    check(blockArchiveFile.length() > 0L)

    // highlight-android-load-scene
    val savedScene = withContext(Dispatchers.IO) {
        sceneFile.readText(Charsets.UTF_8)
    }
    val loadedScene = engine.scene.load(
        scene = savedScene,
        waitForResources = true,
    )
    // highlight-android-load-scene
    val loadedSceneName = engine.block.getName(loadedScene)
    check(loadedSceneName == "Spring Campaign")

    // highlight-android-load-archive
    engine.scene.loadArchive(
        archiveUri = Uri.fromFile(sceneArchiveFile),
        waitForResources = true,
    )
    // highlight-android-load-archive
    val loadedArchivePageCount = engine.scene.getPages().size
    check(loadedArchivePageCount == 1)

    // highlight-android-load-blocks
    val currentPage = engine.scene.getPages().first()
    val loadedStringBlocks = engine.block.loadFromString(blockString)
    val loadedArchiveBlocks = engine.block.loadFromArchive(Uri.fromFile(blockArchiveFile))

    (loadedStringBlocks + loadedArchiveBlocks).forEach { block ->
        engine.block.appendChild(parent = currentPage, child = block)
    }
    // highlight-android-load-blocks

    val loadedBlockNames = (loadedStringBlocks + loadedArchiveBlocks).map(engine.block::getName)
    check(loadedBlockNames == listOf("Reusable Badge", "Reusable Badge"))

    return SaveDesigns(
        sceneStringLength = sceneString.length,
        compressedSceneStringLength = compressedSceneString.length,
        sceneFile = sceneFile,
        sceneArchiveFile = sceneArchiveFile,
        blockStringLength = blockString.length,
        blockArchiveFile = blockArchiveFile,
        loadedSceneName = loadedSceneName,
        loadedArchivePageCount = loadedArchivePageCount,
        loadedBlockNames = loadedBlockNames,
    )
}

data class SaveDesigns(
    val sceneStringLength: Int,
    val compressedSceneStringLength: Int,
    val sceneFile: File,
    val sceneArchiveFile: File,
    val blockStringLength: Int,
    val blockArchiveFile: File,
    val loadedSceneName: String,
    val loadedArchivePageCount: Int,
    val loadedBlockNames: List<String>,
)
