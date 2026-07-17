import ly.img.engine.DesignBlockType
import ly.img.engine.DesignUnit
import ly.img.engine.Engine
import ly.img.engine.ExportOptions
import ly.img.engine.MimeType
import java.nio.ByteBuffer

data class TextOnPath(
    val pathBeforeClear: String?,
    val verticalAlignment: String,
    val offset: Float,
    val isFlipped: Boolean,
    val pathAfterClear: String?,
    val curvedPreviewPng: ByteBuffer,
)

suspend fun textOnPath(engine: Engine): TextOnPath {
    // Demo scaffolding: a square page that frames the curved text for the
    // preview export below. Creating the scene with `DesignUnit.PIXEL` pairs
    // the font-size unit to Pixel, so the `setTextFontSize` value below
    // renders at the scale the SVG path's local coordinates assume.
    // `setTextOnPath` sizes the block to span about 7 font heights of the
    // curve's larger dimension, so the page needs to be generous relative to
    // the font size below or the curve renders larger than the page.
    val scene = engine.scene.create(designUnit = DesignUnit.PIXEL)
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 480F)
    engine.block.setHeight(page, value = 480F)
    engine.block.appendChild(parent = scene, child = page)

    // highlight-android-create-text
    val text = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = text)
    engine.block.replaceText(text, text = "TEXT ON A PATH")
    engine.block.setTextFontSize(text, fontSize = 48F)
    // highlight-android-create-text

    // highlight-android-place-on-path
    // The path must contain a single subpath; the block resizes to match the
    // path's aspect ratio and word wrapping is disabled.
    val circlePath = "M 60,119.5 A 59.5,59.5 0 1,1 60.01,119.5 Z"
    engine.block.setTextOnPath(text, svgPath = circlePath)
    // highlight-android-place-on-path

    // Demo scaffolding: setTextOnPath resizes the block to the path's aspect
    // ratio, so center it on the page now that its size is known.
    engine.block.setPositionX(text, value = (480F - engine.block.getWidth(text)) / 2F)
    engine.block.setPositionY(text, value = (480F - engine.block.getHeight(text)) / 2F)

    // highlight-android-vertical-position
    engine.block.setEnum(text, property = "text/verticalAlignment", value = "Center")
    val verticalAlignment = engine.block.getEnum(text, property = "text/verticalAlignment")
    // highlight-android-vertical-position

    // highlight-android-offset
    // The offset is a proportion of the path length, clamped to [-1, 1].
    engine.block.setTextOnPathOffset(text, offset = 0.05F)
    val offset = engine.block.getTextOnPathOffset(text)
    // highlight-android-offset

    // Export a preview of the curved state so the smoke test can verify the
    // rendered result.
    val curvedPreviewPng = engine.block.export(
        block = page,
        mimeType = MimeType.PNG,
        options = ExportOptions(targetWidth = 1200F, targetHeight = 1200F),
    )

    // highlight-android-direction
    engine.block.setTextOnPathFlipped(text, flipped = true)
    val isFlipped = engine.block.getTextOnPathFlipped(text)
    // highlight-android-direction

    // highlight-android-read-and-clear
    val pathBeforeClear = engine.block.getTextOnPath(text)

    // Passing null removes the curve and restores a straight, auto-sized baseline.
    engine.block.setTextOnPath(text, svgPath = null)
    val pathAfterClear = engine.block.getTextOnPath(text)
    // highlight-android-read-and-clear

    check(pathBeforeClear == circlePath)
    check(verticalAlignment == "Center")
    check(offset == 0.05F)
    check(isFlipped)
    check(pathAfterClear == null)
    check(curvedPreviewPng.hasRemaining()) { "Curved text preview export is empty." }

    return TextOnPath(
        pathBeforeClear = pathBeforeClear,
        verticalAlignment = verticalAlignment,
        offset = offset,
        isFlipped = isFlipped,
        pathAfterClear = pathAfterClear,
        curvedPreviewPng = curvedPreviewPng.asReadOnlyBuffer(),
    )
}
