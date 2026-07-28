import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

data class ImportDesignFromArchiveResult(
    val archiveFile: File,
    val archiveByteCount: Int,
    val loadedScene: DesignBlock,
    val updatedTextBlock: DesignBlock,
)

suspend fun importDesignFromArchive(
    engine: Engine,
    outputDir: File,
): ImportDesignFromArchiveResult {
    val sourceScene = createArchiveSourceScene(engine)
    val archive = createArchiveForTransfer(engine, sourceScene)
    val archiveFile = writeArchiveToFile(
        archive = archive,
        archiveFile = File(outputDir, "portable-design.zip"),
    )
    val loadedScene = loadArchiveFromUri(
        engine = engine,
        archiveUri = Uri.fromFile(archiveFile),
    )
    val updatedTextBlock = modifyLoadedArchive(engine)

    return ImportDesignFromArchiveResult(
        archiveFile = archiveFile,
        archiveByteCount = archive.remaining(),
        loadedScene = loadedScene,
        updatedTextBlock = updatedTextBlock,
    )
}

// highlight-android-create-archive
suspend fun createArchiveForTransfer(
    engine: Engine,
    scene: DesignBlock,
): ByteBuffer {
    val archive = engine.scene.saveToArchive(scene = scene)
    check(archive.hasRemaining()) { "Archive is empty" }
    return archive.asReadOnlyBuffer()
}
// highlight-android-create-archive

// highlight-android-write-archive-file
suspend fun writeArchiveToFile(
    archive: ByteBuffer,
    archiveFile: File,
): File = withContext(Dispatchers.IO) {
    archiveFile.parentFile?.mkdirs()

    val readableArchive = archive.asReadOnlyBuffer()
    FileOutputStream(archiveFile).channel.use { channel ->
        while (readableArchive.hasRemaining()) {
            channel.write(readableArchive)
        }
    }

    check(archiveFile.length() > 0L) { "Saved archive is empty" }
    archiveFile
}
// highlight-android-write-archive-file

// highlight-android-load-archive
suspend fun loadArchiveFromUri(
    engine: Engine,
    archiveUri: Uri,
): DesignBlock = engine.scene.loadArchive(
    archiveUri = archiveUri,
    waitForResources = true,
)
// highlight-android-load-archive

// highlight-android-modify-loaded-archive
fun modifyLoadedArchive(engine: Engine): DesignBlock {
    val textBlock = engine.block.findByType(DesignBlockType.Text).first()
    engine.block.replaceText(
        block = textBlock,
        text = "Archived design loaded",
    )
    return textBlock
}
// highlight-android-modify-loaded-archive

private fun createArchiveSourceScene(engine: Engine): DesignBlock {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 1080F)
    engine.block.setHeight(page, value = 1080F)
    engine.block.appendChild(parent = scene, child = page)

    val background = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(background, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(background, value = 1080F)
    engine.block.setHeight(background, value = 1080F)
    engine.block.setFill(background, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(background, color = Color.fromHex("#FFF6F8FB"))
    engine.block.appendChild(parent = page, child = background)

    val textBlock = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = textBlock)
    engine.block.replaceText(textBlock, text = "Portable archive")
    engine.block.setWidthMode(textBlock, mode = SizeMode.AUTO)
    engine.block.setHeightMode(textBlock, mode = SizeMode.AUTO)
    engine.block.setTextFontSize(textBlock, fontSize = 64F)
    engine.block.setPositionX(textBlock, value = 96F)
    engine.block.setPositionY(textBlock, value = 96F)

    return scene
}
