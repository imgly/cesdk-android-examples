import android.net.Uri
import ly.img.engine.BlurType
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

private const val PAGE_WIDTH = 800F
private const val PAGE_HEIGHT = 600F

data class BlurEffects(
    val pageSupportsBlur: Boolean,
    val imageSupportsBlur: Boolean,
    val imageAllowsBlur: Boolean,
    val radialBlurType: String,
    val radialBlurRadius: Float,
    val radialBlurEnabled: Boolean,
    val disabledState: Boolean,
    val reenabledState: Boolean,
    val pageSharedBlur: DesignBlock,
    val secondarySharedBlur: DesignBlock,
    val pageSharedBlurType: String,
    val secondarySharedBlurType: String,
    val pageSharedBlurEnabled: Boolean,
    val secondarySharedBlurEnabled: Boolean,
    val oldBlur: DesignBlock,
    val replacementBlur: DesignBlock,
    val replacementBlurType: String,
    val replacementBlurRadius: Float,
    val oldBlurValidAfterDestroy: Boolean,
    val previewPng: ByteBuffer,
)

suspend fun blurEffects(engine: Engine): BlurEffects {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = PAGE_WIDTH)
    engine.block.setHeight(page, value = PAGE_HEIGHT)
    engine.block.appendChild(parent = scene, child = page)

    val imageBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(imageBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(imageBlock, value = PAGE_WIDTH)
    engine.block.setHeight(imageBlock, value = PAGE_HEIGHT)

    val imageFill = engine.block.createFill(FillType.Image)
    engine.block.setUri(
        block = imageFill,
        property = "fill/image/imageFileURI",
        value = Uri.parse("file:///android_asset/imgly-assets/ly.img.image/images/sample_1.jpg"),
    )
    engine.block.setFill(block = imageBlock, fill = imageFill)
    engine.block.appendChild(parent = page, child = imageBlock)

    // highlight-android-check-blur-support
    val pageSupportsBlur = engine.block.supportsBlur(page)
    val imageSupportsBlur = engine.block.supportsBlur(imageBlock)
    val imageAllowsBlur = engine.block.isAllowedByScope(imageBlock, "appearance/blur")

    check(imageSupportsBlur) { "The image block must support blur effects." }
    check(imageAllowsBlur) { "The image block must allow blur changes." }
    // highlight-android-check-blur-support

    // highlight-android-create-blur
    val radialBlur = engine.block.createBlur(type = BlurType.Radial)
    // highlight-android-create-blur

    // highlight-android-configure-blur
    engine.block.setFloat(block = radialBlur, property = "blur/radial/blurRadius", value = 40F)
    engine.block.setFloat(block = radialBlur, property = "blur/radial/radius", value = 100F)
    engine.block.setFloat(block = radialBlur, property = "blur/radial/gradientRadius", value = 80F)
    engine.block.setFloat(block = radialBlur, property = "blur/radial/x", value = 0.5F)
    engine.block.setFloat(block = radialBlur, property = "blur/radial/y", value = 0.5F)
    // highlight-android-configure-blur

    // highlight-android-apply-blur
    engine.block.setBlur(block = imageBlock, blurBlock = radialBlur)
    engine.block.setBlurEnabled(block = imageBlock, enabled = true)
    // highlight-android-apply-blur

    // highlight-android-read-blur
    val appliedBlur = engine.block.getBlur(block = imageBlock)
    val radialBlurType = engine.block.getType(block = appliedBlur)
    val radialBlurRadius = engine.block.getFloat(
        block = appliedBlur,
        property = "blur/radial/blurRadius",
    )
    val radialBlurEnabled = engine.block.isBlurEnabled(block = imageBlock)
    // highlight-android-read-blur

    // highlight-android-toggle-blur
    engine.block.setBlurEnabled(block = imageBlock, enabled = false)
    val disabledState = engine.block.isBlurEnabled(block = imageBlock)

    engine.block.setBlurEnabled(block = imageBlock, enabled = true)
    val reenabledState = engine.block.isBlurEnabled(block = imageBlock)
    // highlight-android-toggle-blur

    val previewPng = engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
        options = ExportOptions(targetWidth = PAGE_WIDTH, targetHeight = PAGE_HEIGHT),
    )

    val secondaryBlock = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(secondaryBlock, shape = engine.block.createShape(ShapeType.Rect))
    val secondaryFill = engine.block.createFill(FillType.Color)
    engine.block.setFill(block = secondaryBlock, fill = secondaryFill)
    engine.block.setFillSolidColor(
        block = secondaryBlock,
        color = Color.fromRGBA(r = 0.1F, g = 0.18F, b = 0.28F, a = 1F),
    )
    engine.block.setWidth(secondaryBlock, value = 160F)
    engine.block.setHeight(secondaryBlock, value = 160F)
    engine.block.setPositionX(secondaryBlock, value = 560F)
    engine.block.setPositionY(secondaryBlock, value = 380F)
    engine.block.appendChild(parent = page, child = secondaryBlock)

    // highlight-android-share-blur
    val sharedBlur = engine.block.createBlur(type = BlurType.Uniform)
    engine.block.setFloat(block = sharedBlur, property = "blur/uniform/intensity", value = 0.35F)
    engine.block.setBlur(block = secondaryBlock, blurBlock = sharedBlur)
    engine.block.setBlur(block = page, blurBlock = sharedBlur)
    engine.block.setBlurEnabled(block = secondaryBlock, enabled = true)
    engine.block.setBlurEnabled(block = page, enabled = true)

    val pageSharedBlur = engine.block.getBlur(block = page)
    val secondarySharedBlur = engine.block.getBlur(block = secondaryBlock)
    // highlight-android-share-blur

    // highlight-android-replace-blur
    val oldBlur = engine.block.getBlur(block = imageBlock)
    val linearBlur = engine.block.createBlur(type = BlurType.Linear)
    engine.block.setFloat(block = linearBlur, property = "blur/linear/blurRadius", value = 30F)
    engine.block.setFloat(block = linearBlur, property = "blur/linear/x1", value = 0F)
    engine.block.setFloat(block = linearBlur, property = "blur/linear/y1", value = 0.5F)
    engine.block.setFloat(block = linearBlur, property = "blur/linear/x2", value = 1F)
    engine.block.setFloat(block = linearBlur, property = "blur/linear/y2", value = 0.5F)
    engine.block.setBlur(block = imageBlock, blurBlock = linearBlur)
    engine.block.setBlurEnabled(block = imageBlock, enabled = true)
    val replacementBlur = engine.block.getBlur(block = imageBlock)
    engine.block.destroy(block = oldBlur)
    val oldBlurValidAfterDestroy = engine.block.isValid(block = oldBlur)
    // highlight-android-replace-blur

    return BlurEffects(
        pageSupportsBlur = pageSupportsBlur,
        imageSupportsBlur = imageSupportsBlur,
        imageAllowsBlur = imageAllowsBlur,
        radialBlurType = radialBlurType,
        radialBlurRadius = radialBlurRadius,
        radialBlurEnabled = radialBlurEnabled,
        disabledState = disabledState,
        reenabledState = reenabledState,
        pageSharedBlur = pageSharedBlur,
        secondarySharedBlur = secondarySharedBlur,
        pageSharedBlurType = engine.block.getType(block = pageSharedBlur),
        secondarySharedBlurType = engine.block.getType(block = secondarySharedBlur),
        pageSharedBlurEnabled = engine.block.isBlurEnabled(block = page),
        secondarySharedBlurEnabled = engine.block.isBlurEnabled(block = secondaryBlock),
        oldBlur = oldBlur,
        replacementBlur = replacementBlur,
        replacementBlurType = engine.block.getType(block = replacementBlur),
        replacementBlurRadius = engine.block.getFloat(block = replacementBlur, property = "blur/linear/blurRadius"),
        oldBlurValidAfterDestroy = oldBlurValidAfterDestroy,
        previewPng = previewPng,
    )
}
