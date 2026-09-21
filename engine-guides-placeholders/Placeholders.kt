import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType

data class Placeholders(
    val imagePlaceholder: DesignBlock,
    val textPlaceholder: DesignBlock,
    val imageBehaviorSupported: Boolean,
    val textBehaviorSupported: Boolean,
    val imageControlsSupported: Boolean,
    val textControlsSupported: Boolean,
    val imageBehaviorEnabled: Boolean,
    val textBehaviorEnabled: Boolean,
    val imagePlaceholderEnabled: Boolean,
    val textPlaceholderEnabled: Boolean,
    val overlayEnabled: Boolean,
    val buttonEnabled: Boolean,
    val batchImageBlock: DesignBlock,
    val batchImageFill: DesignBlock,
    val batchTextBlock: DesignBlock,
    val placeholders: List<DesignBlock>,
)

fun placeholders(engine: Engine): Placeholders {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.appendChild(parent = scene, child = page)

    val imagePlaceholder = engine.block.create(DesignBlockType.Graphic)
    val imageFill = engine.block.createFill(FillType.Image)
    engine.block.setName(imagePlaceholder, name = "image-placeholder")
    engine.block.setShape(imagePlaceholder, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(imagePlaceholder, fill = imageFill)
    engine.block.appendChild(parent = page, child = imagePlaceholder)

    val textPlaceholder = engine.block.create(DesignBlockType.Text)
    engine.block.setName(textPlaceholder, name = "text-placeholder")
    engine.block.replaceText(textPlaceholder, text = "Replace this text")
    engine.block.appendChild(parent = page, child = textPlaceholder)

    // highlight-android-check-support
    val imagePlaceholderFill = engine.block.getFill(imagePlaceholder)
    val imageBehaviorSupported = engine.block.supportsPlaceholderBehavior(imagePlaceholderFill)
    val imageControlsSupported = engine.block.supportsPlaceholderControls(imagePlaceholder)

    val textBehaviorSupported = engine.block.supportsPlaceholderBehavior(textPlaceholder)
    val textControlsSupported = engine.block.supportsPlaceholderControls(textPlaceholder)
    // highlight-android-check-support

    // highlight-android-enable-image-behavior
    val imageBehaviorEnabled = if (imageBehaviorSupported) {
        engine.block.setPlaceholderBehaviorEnabled(imagePlaceholderFill, enabled = true)
        engine.block.isPlaceholderBehaviorEnabled(imagePlaceholderFill)
    } else {
        false
    }
    // highlight-android-enable-image-behavior

    // highlight-android-enable-text-behavior
    val textBehaviorEnabled = if (textBehaviorSupported) {
        engine.block.setPlaceholderBehaviorEnabled(textPlaceholder, enabled = true)
        engine.block.isPlaceholderBehaviorEnabled(textPlaceholder)
    } else {
        false
    }
    // highlight-android-enable-text-behavior

    // highlight-android-enable-interaction
    engine.block.setPlaceholderEnabled(imagePlaceholder, enabled = true)
    engine.block.setPlaceholderEnabled(textPlaceholder, enabled = true)

    val imagePlaceholderEnabled = engine.block.isPlaceholderEnabled(imagePlaceholder)
    val textPlaceholderEnabled = engine.block.isPlaceholderEnabled(textPlaceholder)
    // highlight-android-enable-interaction

    // highlight-android-enable-controls
    val overlayEnabled: Boolean
    val buttonEnabled: Boolean
    if (imageControlsSupported) {
        engine.block.setPlaceholderControlsOverlayEnabled(imagePlaceholder, enabled = true)
        engine.block.setPlaceholderControlsButtonEnabled(imagePlaceholder, enabled = true)
        overlayEnabled = engine.block.isPlaceholderControlsOverlayEnabled(imagePlaceholder)
        buttonEnabled = engine.block.isPlaceholderControlsButtonEnabled(imagePlaceholder)
    } else {
        overlayEnabled = false
        buttonEnabled = false
    }
    // highlight-android-enable-controls

    // highlight-android-enable-scopes
    engine.block.setScopeEnabled(imagePlaceholder, key = "fill/change", enabled = true)
    engine.block.setScopeEnabled(textPlaceholder, key = "text/edit", enabled = true)
    // highlight-android-enable-scopes

    val batchImageBlock = engine.block.create(DesignBlockType.Graphic)
    val batchImageFill = engine.block.createFill(FillType.Image)
    engine.block.setShape(batchImageBlock, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setFill(batchImageBlock, fill = batchImageFill)
    engine.block.appendChild(parent = page, child = batchImageBlock)

    val batchTextBlock = engine.block.create(DesignBlockType.Text)
    engine.block.replaceText(batchTextBlock, text = "Replace this text too")
    engine.block.appendChild(parent = page, child = batchTextBlock)

    // highlight-android-batch-placeholders
    val batchTargets = listOf(
        Triple(batchImageBlock, batchImageFill, "fill/change"),
        Triple(batchTextBlock, batchTextBlock, "text/edit"),
    )
    batchTargets.forEach { (block, behaviorTarget, contentScope) ->
        if (engine.block.supportsPlaceholderBehavior(behaviorTarget)) {
            engine.block.setPlaceholderBehaviorEnabled(behaviorTarget, enabled = true)
            engine.block.setPlaceholderEnabled(block, enabled = true)
            engine.block.setScopeEnabled(block, key = contentScope, enabled = true)
        }
        if (engine.block.supportsPlaceholderControls(block)) {
            engine.block.setPlaceholderControlsOverlayEnabled(block, enabled = true)
            engine.block.setPlaceholderControlsButtonEnabled(block, enabled = true)
        }
    }
    // highlight-android-batch-placeholders

    // highlight-android-find-placeholders
    val placeholders = engine.block.findAllPlaceholders()
    // highlight-android-find-placeholders

    return Placeholders(
        imagePlaceholder = imagePlaceholder,
        textPlaceholder = textPlaceholder,
        imageBehaviorSupported = imageBehaviorSupported,
        textBehaviorSupported = textBehaviorSupported,
        imageControlsSupported = imageControlsSupported,
        textControlsSupported = textControlsSupported,
        imageBehaviorEnabled = imageBehaviorEnabled,
        textBehaviorEnabled = textBehaviorEnabled,
        imagePlaceholderEnabled = imagePlaceholderEnabled,
        textPlaceholderEnabled = textPlaceholderEnabled,
        overlayEnabled = overlayEnabled,
        buttonEnabled = buttonEnabled,
        batchImageBlock = batchImageBlock,
        batchImageFill = batchImageFill,
        batchTextBlock = batchTextBlock,
        placeholders = placeholders,
    )
}
