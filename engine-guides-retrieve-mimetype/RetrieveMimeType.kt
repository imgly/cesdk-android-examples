import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.channels.Channels

data class ResourceWithMimeType(
    val uri: Uri,
    val size: Int,
    val mimeType: String,
)

data class RetrieveMimeTypeResult(
    val resourcesByType: Map<String, Int>,
    val imageResourceCount: Int,
    val uploadedResourceCount: Int,
    val uploadedFileResourceCount: Int,
    val uploadedBufferResourceCount: Int,
    val remainingTransientResourceCount: Int,
    val externalMimeType: String,
)

suspend fun retrieveMimeType(engine: Engine): RetrieveMimeTypeResult {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)
    engine.block.setWidth(page, value = 320F)
    engine.block.setHeight(page, value = 180F)
    engine.block.setDuration(page, duration = 1.0)

    val bitmap =
        Bitmap
            .createBitmap(32, 32, Bitmap.Config.ARGB_8888)
            .apply { eraseColor(Color.rgb(52, 118, 235)) }
    val imageBuffer = bitmap.toPngBuffer()
    val imageBufferUri = engine.editor.createBuffer()
    try {
        engine.editor.setBufferData(uri = imageBufferUri, offset = 0, data = imageBuffer)

        val imageBlock = engine.block.create(DesignBlockType.Graphic)
        val imageFill = engine.block.createFill(FillType.Image)
        engine.block.setShape(imageBlock, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setWidth(imageBlock, value = 160F)
        engine.block.setHeight(imageBlock, value = 120F)
        engine.block.setPositionX(imageBlock, value = 80F)
        engine.block.setPositionY(imageBlock, value = 30F)
        engine.block.setUri(
            block = imageFill,
            property = "fill/image/imageFileURI",
            value = imageBufferUri,
        )
        engine.block.setFill(block = imageBlock, fill = imageFill)
        engine.block.setContentFillMode(block = imageBlock, mode = ContentFillMode.COVER)
        engine.block.appendChild(parent = page, child = imageBlock)

        val assetImageUri = Uri.parse("file:///android_asset/webkit-P3.png")
        val assetImageBlock = engine.block.create(DesignBlockType.Graphic)
        val assetImageFill = engine.block.createFill(FillType.Image)
        engine.block.setShape(assetImageBlock, shape = engine.block.createShape(ShapeType.Rect))
        engine.block.setWidth(assetImageBlock, value = 80F)
        engine.block.setHeight(assetImageBlock, value = 80F)
        engine.block.setPositionX(assetImageBlock, value = 200F)
        engine.block.setPositionY(assetImageBlock, value = 50F)
        engine.block.setUri(
            block = assetImageFill,
            property = "fill/image/imageFileURI",
            value = assetImageUri,
        )
        engine.block.setFill(block = assetImageBlock, fill = assetImageFill)
        engine.block.setContentFillMode(block = assetImageBlock, mode = ContentFillMode.COVER)
        engine.block.appendChild(parent = page, child = assetImageBlock)
        engine.block.forceLoadResources(blocks = listOf(scene))

        // highlight-android-find-transient-resources
        val transientResources = engine.editor.findAllTransientResources()
        // highlight-android-find-transient-resources

        check(transientResources.any { (uri, _) -> uri == imageBufferUri })
        check(transientResources.any { (uri, _) -> uri == assetImageUri })

        // highlight-android-get-mime-type
        val resourcesWithMimeType =
            transientResources.map { (uri, size) ->
                ResourceWithMimeType(
                    uri = uri,
                    size = size,
                    mimeType = engine.editor.getMimeType(uri = uri),
                )
            }

        val resourcesByType =
            resourcesWithMimeType
                .groupingBy { it.mimeType }
                .eachCount()
        // highlight-android-get-mime-type

        // highlight-android-filter-images
        val imageResources =
            resourcesWithMimeType.filter { resource ->
                resource.mimeType.startsWith("image/")
            }
        // highlight-android-filter-images

        check(imageResources.isNotEmpty())

        // highlight-android-read-resource-data
        val uploadedResources =
            imageResources.associate { resource ->
                val uploadedResource = File.createTempFile("cesdk-resource-", ".upload")
                try {
                    FileOutputStream(uploadedResource).channel.use { channel ->
                        engine.editor.getResourceData(
                            uri = resource.uri,
                            chunkSize = 64 * 1024,
                        ) { chunk ->
                            val data = chunk.asReadOnlyBuffer()
                            while (data.hasRemaining()) {
                                channel.write(data)
                            }
                            true
                        }
                    }
                    check(uploadedResource.length() == resource.size.toLong())

                    val permanentUri = uploadTransientResource(
                        uri = resource.uri,
                        file = uploadedResource,
                        mimeType = resource.mimeType,
                    )
                    resource.uri to permanentUri
                } finally {
                    uploadedResource.delete()
                }
            }
        // highlight-android-read-resource-data

        // highlight-android-relocate-resources
        for ((transientUri, permanentUri) in uploadedResources) {
            engine.editor.relocateResource(
                currentUri = transientUri,
                relocatedUri = permanentUri,
            )
        }
        // highlight-android-relocate-resources

        // highlight-android-verify-relocation
        val remainingTransientResources = engine.editor.findAllTransientResources()
        check(remainingTransientResources.none { (uri, _) -> uri in uploadedResources.keys })
        // highlight-android-verify-relocation

        // highlight-android-validate-external-uri
        val externalMimeType = engine.editor.getMimeType(
            uri = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg"),
        )
        // highlight-android-validate-external-uri

        return RetrieveMimeTypeResult(
            resourcesByType = resourcesByType,
            imageResourceCount = imageResources.size,
            uploadedResourceCount = uploadedResources.size,
            uploadedFileResourceCount = uploadedResources.keys.count { it.scheme == "file" },
            uploadedBufferResourceCount = uploadedResources.keys.count { it.scheme == "buffer" },
            remainingTransientResourceCount = remainingTransientResources.size,
            externalMimeType = externalMimeType,
        )
    } finally {
        engine.editor.destroyBuffer(uri = imageBufferUri)
    }
}

private fun Bitmap.toPngBuffer(): ByteBuffer {
    val pngFile = File.createTempFile("cesdk-buffer-image-", ".png")
    try {
        FileOutputStream(pngFile).channel.use { channel ->
            val output = Channels.newOutputStream(channel)
            check(compress(Bitmap.CompressFormat.PNG, 100, output))
            output.flush()
        }

        return FileInputStream(pngFile).channel.use { channel ->
            val size = channel.size()
            check(size <= Int.MAX_VALUE) { "PNG buffer is too large." }

            ByteBuffer.allocateDirect(size.toInt()).apply {
                while (hasRemaining() && channel.read(this) != -1) {
                    // Keep reading until the direct buffer contains the full PNG file.
                }
                flip()
            }
        }
    } finally {
        recycle()
        pngFile.delete()
    }
}

// highlight-android-upload-helper
private fun uploadTransientResource(
    uri: Uri,
    file: File,
    mimeType: String,
): Uri {
    check(file.length() > 0) { "Cannot upload an empty resource." }

    // Replace this with your app's storage client and use mimeType as the upload content type.
    val extension =
        when (mimeType) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> "bin"
        }
    val sourceName = uri.lastPathSegment?.takeIf { it.isNotBlank() } ?: "resource"
    val storageKey =
        sourceName
            .substringBeforeLast(delimiter = ".", missingDelimiterValue = sourceName)
            .ifBlank { "resource" }
    return Uri.parse("https://your-storage.example/uploads/$storageKey.$extension")
}
// highlight-android-upload-helper
