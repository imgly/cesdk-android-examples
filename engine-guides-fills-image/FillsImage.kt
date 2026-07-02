import android.net.Uri
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import ly.img.engine.Source
import java.nio.ByteBuffer
import kotlin.math.abs

data class FillsImage(
    val sceneSupportsFill: Boolean,
    val blockSupportsFill: Boolean,
    val blockSupportsContentFillMode: Boolean,
    val blockSupportsOpacity: Boolean,
    val imageFillType: String,
    val imageUri: Uri,
    val currentFillMatches: Boolean,
    val coverMode: ContentFillMode,
    val containMode: ContentFillMode,
    val cropMode: ContentFillMode,
    val contentFillModeAfterSourceSet: ContentFillMode,
    val sourceSetWidths: List<Int>,
    val opacity: Float,
    val exportedImage: ByteBuffer,
)

suspend fun fillsImage(engine: Engine): FillsImage {
    val scene = engine.scene.create()

    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val imageBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(imageBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setPositionX(imageBlock, value = 100F)
    engine.block.setPositionY(imageBlock, value = 80F)
    engine.block.setWidth(imageBlock, value = 360F)
    engine.block.setHeight(imageBlock, value = 240F)
    engine.block.appendChild(parent = page, child = imageBlock)

    // highlight-android-check-support
    val sceneSupportsFill = engine.block.supportsFill(scene)
    val blockSupportsFill = engine.block.supportsFill(imageBlock)
    val blockSupportsContentFillMode = engine.block.supportsContentFillMode(imageBlock)
    val blockSupportsOpacity = engine.block.supportsOpacity(imageBlock)

    require(!sceneSupportsFill) { "Scenes do not support fills." }
    require(blockSupportsFill) { "Graphic blocks with shapes can render fills." }
    require(blockSupportsContentFillMode) { "This block must support content fill modes." }
    require(blockSupportsOpacity) { "This block must support opacity." }
    // highlight-android-check-support

    // highlight-android-create-image-fill
    val imageFill = engine.block.createFill(FillType.Image)
    val imageUri = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg")

    engine.block.setUri(
        block = imageFill,
        property = "fill/image/imageFileURI",
        value = imageUri,
    )
    engine.block.setFill(block = imageBlock, fill = imageFill)
    // highlight-android-create-image-fill

    check(engine.block.getType(imageFill) == FillType.Image.key)

    // highlight-android-get-current-fill
    val currentFill = engine.block.getFill(imageBlock)
    val imageFillType = engine.block.getType(currentFill)
    val currentImageUri = engine.block.getUri(
        block = currentFill,
        property = "fill/image/imageFileURI",
    )
    // highlight-android-get-current-fill

    check(currentFill == imageFill)
    check(imageFillType == FillType.Image.key)
    check(currentImageUri == imageUri)

    // highlight-android-cover-mode
    engine.block.setContentFillMode(block = imageBlock, mode = ContentFillMode.COVER)
    val coverMode = engine.block.getContentFillMode(imageBlock)
    // highlight-android-cover-mode

    check(coverMode == ContentFillMode.COVER)

    // highlight-android-contain-mode
    engine.block.setContentFillMode(block = imageBlock, mode = ContentFillMode.CONTAIN)
    val containMode = engine.block.getContentFillMode(imageBlock)
    // highlight-android-contain-mode

    check(containMode == ContentFillMode.CONTAIN)

    // highlight-android-crop-mode
    engine.block.setContentFillMode(block = imageBlock, mode = ContentFillMode.CROP)
    val cropMode = engine.block.getContentFillMode(imageBlock)
    // highlight-android-crop-mode

    check(cropMode == ContentFillMode.CROP)

    // highlight-android-source-set
    val sourceSet = listOf(
        Source(
            uri = Uri.parse("https://img.ly/static/ubq_samples/sample_1_512x341.jpg"),
            width = 512,
            height = 341,
        ),
        Source(
            uri = Uri.parse("https://img.ly/static/ubq_samples/sample_1_1024x683.jpg"),
            width = 1024,
            height = 683,
        ),
        Source(
            uri = Uri.parse("https://img.ly/static/ubq_samples/sample_1_2048x1366.jpg"),
            width = 2048,
            height = 1366,
        ),
    )

    engine.block.setSourceSet(
        block = imageFill,
        property = "fill/image/sourceSet",
        sourceSet = sourceSet,
    )

    // setSourceSet resets crop and content fill mode on the associated block.
    // Reapply Cover or Contain after changing responsive image sources.
    engine.block.setContentFillMode(block = imageBlock, mode = ContentFillMode.CONTAIN)
    // highlight-android-source-set

    val contentFillModeAfterSourceSet = engine.block.getContentFillMode(imageBlock)
    check(contentFillModeAfterSourceSet == ContentFillMode.CONTAIN)

    // highlight-android-get-source-set
    val currentSourceSet = engine.block.getSourceSet(
        block = imageFill,
        property = "fill/image/sourceSet",
    )
    // highlight-android-get-source-set

    check(currentSourceSet.map(Source::width) == listOf(2048, 1024, 512))

    // highlight-android-opacity
    engine.block.setOpacity(block = imageBlock, value = 0.65F)
    val opacity = engine.block.getOpacity(imageBlock)
    // highlight-android-opacity

    check(abs(opacity - 0.65F) < 0.0001F)

    val exportedImage = engine.block.export(imageBlock, mimeType = MimeType.PNG)

    return FillsImage(
        sceneSupportsFill = sceneSupportsFill,
        blockSupportsFill = blockSupportsFill,
        blockSupportsContentFillMode = blockSupportsContentFillMode,
        blockSupportsOpacity = blockSupportsOpacity,
        imageFillType = imageFillType,
        imageUri = currentImageUri,
        currentFillMatches = currentFill == imageFill,
        coverMode = coverMode,
        containMode = containMode,
        cropMode = cropMode,
        contentFillModeAfterSourceSet = contentFillModeAfterSourceSet,
        sourceSetWidths = currentSourceSet.map(Source::width),
        opacity = opacity,
        exportedImage = exportedImage,
    )
}
