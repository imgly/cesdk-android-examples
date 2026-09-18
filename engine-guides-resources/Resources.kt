import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import kotlinx.coroutines.withContext
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import java.io.OutputStream
import java.nio.ByteBuffer

suspend fun resources(engine: Engine): List<String> = withContext(engine.dispatcher) {
    val logLines = mutableListOf<String>()

    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 680F)
    engine.block.setHeight(page, value = 260F)
    engine.block.setDuration(page, duration = 1.0)

    // highlight-android-on-demand-loading
    val imageBlock = engine.block.create(DesignBlockType.Graphic)
    val imageFill = engine.block.createFill(FillType.Image)
    engine.block.setShape(imageBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(imageBlock, value = 30F)
    engine.block.setPositionY(imageBlock, value = 30F)
    engine.block.setWidth(imageBlock, value = 300F)
    engine.block.setHeight(imageBlock, value = 200F)

    val imageUri = Uri.parse(
        "https://img.ly/static/ubq_samples/sample_4.jpg?cesdk-resources-guide=source",
    )
    engine.block.setUri(
        block = imageFill,
        property = "fill/image/imageFileURI",
        value = imageUri,
    )
    engine.block.setFill(block = imageBlock, fill = imageFill)
    engine.block.setContentFillMode(block = imageBlock, mode = ContentFillMode.COVER)
    engine.block.appendChild(parent = page, child = imageBlock)
    // highlight-android-on-demand-loading

    // highlight-android-preload-av
    val videoBlock = engine.block.create(DesignBlockType.Graphic)
    val videoFill = engine.block.createFill(FillType.Video)
    engine.block.setShape(videoBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(videoBlock, value = 350F)
    engine.block.setPositionY(videoBlock, value = 30F)
    engine.block.setWidth(videoBlock, value = 300F)
    engine.block.setHeight(videoBlock, value = 200F)
    val videoUri = Uri.parse("https://img.ly/static/ubq_video_samples/bbb.mp4")
    engine.block.setUri(
        block = videoFill,
        property = "fill/video/fileURI",
        value = videoUri,
    )
    engine.block.setFill(block = videoBlock, fill = videoFill)
    engine.block.setContentFillMode(block = videoBlock, mode = ContentFillMode.COVER)
    engine.block.appendChild(parent = page, child = videoBlock)

    engine.block.forceLoadAVResource(block = videoFill)

    val duration = engine.block.getAVResourceTotalDuration(block = videoFill)
    val videoWidth = engine.block.getVideoWidth(videoFill = videoFill)
    val videoHeight = engine.block.getVideoHeight(videoFill = videoFill)
    // highlight-android-preload-av

    val transientBitmap =
        Bitmap
            .createBitmap(32, 32, Bitmap.Config.ARGB_8888)
            .apply {
                eraseColor(Color.rgb(255, 196, 0))
            }
    val transientImageBuffer =
        DirectByteBufferOutputStream().use { output ->
            check(transientBitmap.compress(Bitmap.CompressFormat.PNG, 100, output))
            output.toByteBuffer()
        }
    transientBitmap.recycle()
    val transientImageSize = transientImageBuffer.remaining()
    val transientImageBufferUri = engine.editor.createBuffer()
    var transientImageBlockToDestroy: DesignBlock? = null
    var transientImageFillToDestroy: DesignBlock? = null
    var transientImageRelocated = false
    try {
        engine.editor.setBufferData(
            uri = transientImageBufferUri,
            offset = 0,
            data = transientImageBuffer,
        )
        check(engine.editor.getBufferLength(uri = transientImageBufferUri) == transientImageSize)

        val transientImageBlock = engine.block.create(DesignBlockType.Graphic)
        transientImageBlockToDestroy = transientImageBlock
        val transientImageFill = engine.block.createFill(FillType.Image)
        transientImageFillToDestroy = transientImageFill
        engine.block.setShape(transientImageBlock, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setPositionX(transientImageBlock, value = 600F)
        engine.block.setPositionY(transientImageBlock, value = 20F)
        engine.block.setWidth(transientImageBlock, value = 48F)
        engine.block.setHeight(transientImageBlock, value = 48F)
        engine.block.setUri(
            block = transientImageFill,
            property = "fill/image/imageFileURI",
            value = transientImageBufferUri,
        )
        engine.block.setFill(block = transientImageBlock, fill = transientImageFill)
        engine.block.appendChild(parent = page, child = transientImageBlock)

        // highlight-android-preload-resources
        // Preload every resource referenced by the scene and its children.
        engine.block.forceLoadResources(blocks = listOf(scene))

        // Or preload only guide-owned blocks whose resources are needed next.
        val graphics = listOf(imageBlock, videoBlock, transientImageBlock)
        engine.block.forceLoadResources(blocks = graphics)
        // highlight-android-preload-resources

        // highlight-android-find-transient
        // No preload call is required; this inspects resource references that cannot be serialized.
        val transientResources = engine.editor.findAllTransientResources()
            .filter { (uri, _) -> uri == transientImageBufferUri }
        // highlight-android-find-transient

        // highlight-android-find-media-uris
        val mediaUris = engine.editor.findAllMediaURIs()
        val persistentMediaUris = mediaUris.filter { it.scheme in listOf("http", "https", "file") }
        // highlight-android-find-media-uris

        // highlight-android-find-unused-blocks
        val unusedBlock = engine.block.create(DesignBlockType.Graphic)
        val unusedBlocks = engine.block.findAllUnused()
        check(unusedBlock in unusedBlocks)
        engine.block.destroy(unusedBlock)
        // highlight-android-find-unused-blocks

        // highlight-android-detect-mime-type
        val mimeType = engine.editor.getMimeType(uri = imageUri)
        // highlight-android-detect-mime-type

        // highlight-android-relocate
        val relocatedImageUri = Uri.parse(
            "https://img.ly/static/ubq_samples/sample_1.jpg?cesdk-resources-guide=relocated",
        )
        engine.editor.relocateResource(
            currentUri = imageUri,
            relocatedUri = relocatedImageUri,
        )
        // highlight-android-relocate

        // highlight-android-persist-transient
        val relocatedResources =
            transientResources.map { (transientUri, resourceSize) ->
                val resourceData = ByteBuffer.allocateDirect(resourceSize)
                engine.editor.getResourceData(
                    uri = transientUri,
                    chunkSize = 64 * 1024,
                ) { chunk ->
                    resourceData.put(chunk.asReadOnlyBuffer())
                    true
                }
                resourceData.flip()

                val permanentUri =
                    uploadTransientResourceToPermanentStorage(
                        sourceUri = transientUri,
                        data = resourceData.asReadOnlyBuffer(),
                    )
                engine.editor.relocateResource(
                    currentUri = transientUri,
                    relocatedUri = permanentUri,
                )
                transientImageRelocated = true
                transientUri to permanentUri
            }

        val remainingTransientResources = engine.editor.findAllTransientResources()
            .filter { (uri, _) -> uri == transientImageBufferUri }
        val sceneString =
            engine.scene.saveToString(
                scene = scene,
                allowedResourceSchemes = listOf("http", "https"),
            )
        // highlight-android-persist-transient

        logLines +=
            "Created an image block for $imageUri. The resource loads on-demand when rendered or exported."
        logLines += "Preloaded resources for the scene and ${graphics.size} guide-owned graphic blocks."
        logLines += "Video metadata: ${duration}s, ${videoWidth}x$videoHeight."
        transientResources.forEach { (uri, size) ->
            logLines += "Transient resource: $uri ($size bytes)."
        }
        mediaUris.forEach { uri ->
            logLines += "Media URI: $uri"
        }
        logLines += "Persistent media URI count: ${persistentMediaUris.size}."
        logLines += "Found ${unusedBlocks.size} unused blocks and destroyed the guide-owned block."
        logLines += "MIME type for $imageUri: $mimeType"
        relocatedResources.forEach { (transientUri, permanentUri) ->
            logLines += "Relocated $transientUri to $permanentUri."
        }
        logLines += "Relocated image resource to $relocatedImageUri."
        logLines += "Transient resources after relocation: ${remainingTransientResources.size}."
        logLines += "Saved scene string (${sceneString.length} characters)."

        logLines
    } finally {
        if (!transientImageRelocated) {
            transientImageBlockToDestroy
                ?.takeIf(engine.block::isValid)
                ?.let(engine.block::destroy)
            transientImageFillToDestroy
                ?.takeIf(engine.block::isValid)
                ?.let(engine.block::destroy)
        }
        engine.editor.destroyBuffer(uri = transientImageBufferUri)
    }
}

// highlight-android-upload-helper
private suspend fun uploadTransientResourceToPermanentStorage(
    sourceUri: Uri,
    data: ByteBuffer,
): Uri {
    check(data.hasRemaining()) { "Cannot upload an empty resource." }

    // Upload the bytes with your app's storage client here, then return its permanent URI.
    // This sample only creates a placeholder URL so the guide can focus on the CE.SDK flow.
    val fileName =
        sourceUri
            .lastPathSegment
            ?.takeIf { it.isNotBlank() }
            ?: "transient-resource-${data.remaining()}"
    return Uri.parse("https://your-storage.example/uploads/$fileName")
}
// highlight-android-upload-helper

private class DirectByteBufferOutputStream(
    initialCapacity: Int = 4 * 1024,
) : OutputStream() {
    private var buffer = ByteBuffer.allocateDirect(initialCapacity)

    override fun write(value: Int) {
        ensureCapacity(1)
        buffer.put(value.toByte())
    }

    fun toByteBuffer(): ByteBuffer {
        val data = buffer.asReadOnlyBuffer()
        data.flip()
        return data.slice().asReadOnlyBuffer()
    }

    private fun ensureCapacity(additionalBytes: Int) {
        if (buffer.remaining() >= additionalBytes) return

        val expanded = ByteBuffer.allocateDirect(
            maxOf(buffer.capacity() * 2, buffer.position() + additionalBytes),
        )
        buffer.flip()
        expanded.put(buffer)
        buffer = expanded
    }
}
