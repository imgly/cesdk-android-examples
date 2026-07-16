import android.net.Uri
import ly.img.engine.ContentFillMode
import ly.img.engine.DesignBlockType
import ly.img.engine.EffectType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType

suspend fun editImageProgrammatically(engine: Engine): ProgrammaticImageEditResult {
    // highlight-android-load-image-scene
    val imageUri = Uri.parse("https://img.ly/static/ubq_samples/sample_1.jpg")
    engine.scene.createFromImage(imageUri)
    // highlight-android-load-image-scene

    // highlight-android-find-image-block
    val page = engine.block.findByType(DesignBlockType.Page).first()
    val imageFill = engine.block.getFill(page)

    require(engine.block.getType(imageFill) == FillType.Image.key) {
        "Expected the imported page to use an image fill."
    }
    // highlight-android-find-image-block

    // highlight-android-apply-basic-edits
    engine.block.setWidth(block = page, value = 900F)
    engine.block.setHeight(block = page, value = 600F)
    engine.block.setContentFillMode(block = page, mode = ContentFillMode.COVER)
    // highlight-android-apply-basic-edits

    // highlight-android-add-adjustment
    val adjustment = engine.block.createEffect(type = EffectType.Adjustments)
    engine.block.appendEffect(block = page, effectBlock = adjustment)
    engine.block.setFloat(adjustment, property = "effect/adjustments/brightness", value = 0.08F)
    engine.block.setFloat(adjustment, property = "effect/adjustments/contrast", value = 0.18F)
    // highlight-android-add-adjustment

    // highlight-android-export-image
    val editedPng = engine.block.export(block = page, mimeType = MimeType.PNG)

    check(editedPng.hasRemaining()) { "PNG export is empty." }
    // highlight-android-export-image

    return ProgrammaticImageEditResult(
        width = engine.block.getWidth(page),
        height = engine.block.getHeight(page),
        fillMode = engine.block.getContentFillMode(page),
        brightness = engine.block.getFloat(adjustment, property = "effect/adjustments/brightness"),
        contrast = engine.block.getFloat(adjustment, property = "effect/adjustments/contrast"),
        imageFillType = engine.block.getType(imageFill),
        exportedPng = editedPng.asReadOnlyBuffer(),
    )
}
