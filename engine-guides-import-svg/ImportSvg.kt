import android.util.Log
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.EngineException
import ly.img.engine.ShapeType

private const val TAG = "ImportSvg"

fun importSvg(engine: Engine): DesignBlock {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    // highlight-android-svg-markup
    // A badge with a radial gradient, a stroked star and a dashed line.
    val badgeSvg = """
        <svg xmlns="http://www.w3.org/2000/svg" width="240" height="240" viewBox="0 0 240 240">
          <defs>
            <radialGradient id="glow" cx="120" cy="120" r="110" gradientUnits="userSpaceOnUse">
              <stop offset="0" stop-color="#ffd166"/>
              <stop offset="1" stop-color="#ef476f"/>
            </radialGradient>
          </defs>
          <circle cx="120" cy="120" r="110" fill="url(#glow)"/>
          <polygon points="120,40 143,97 205,97 155,133 174,192 120,156 66,192 85,133 35,97 97,97"
            fill="#ffffff" stroke="#073b4c" stroke-width="6" stroke-linejoin="round"/>
          <path d="M60 212 H180" fill="none" stroke="#073b4c" stroke-width="4"
            stroke-dasharray="12 6" stroke-linecap="round"/>
        </svg>
    """.trimIndent()
    // highlight-android-svg-markup

    // highlight-android-import-svg
    val badge = engine.block.createFromSVG(badgeSvg)
    engine.block.appendChild(parent = page, child = badge)
    // highlight-android-import-svg

    // highlight-android-inspect-result
    val isGroup = engine.block.getType(badge) == DesignBlockType.Group.key
    Log.i(TAG, "The import returned a group: $isGroup")
    val (circle, star, line) = engine.block.getChildren(badge)
    listOf(circle, star, line).forEach { child ->
        val shape = engine.block.getShape(child)
        val isVectorPath = engine.block.getType(shape) == ShapeType.VectorPath.key
        Log.i(TAG, "The child has a vector path shape: $isVectorPath")
    }
    // highlight-android-inspect-result

    // highlight-android-design-unit
    Log.i(TAG, "Design unit: ${engine.scene.getDesignUnit()}")
    // highlight-android-design-unit

    // highlight-android-fit-to-page
    val pageWidth = engine.block.getWidth(page)
    val pageHeight = engine.block.getHeight(page)
    val factor = 0.8F * minOf(
        pageWidth / engine.block.getWidth(badge),
        pageHeight / engine.block.getHeight(badge),
    )
    // scale() also scales the stroke widths and dashes.
    engine.block.scale(block = badge, scale = factor, anchorX = 0F, anchorY = 0F)
    engine.block.setPositionX(badge, value = (pageWidth - engine.block.getWidth(badge)) / 2F)
    engine.block.setPositionY(badge, value = (pageHeight - engine.block.getHeight(badge)) / 2F)
    // highlight-android-fit-to-page

    // highlight-android-edit-shapes
    engine.block.setFillSolidColor(
        block = star,
        color = Color.fromRGBA(r = 0.02F, g = 0.84F, b = 0.63F, a = 1F),
    )
    engine.block.setStrokeWidth(block = line, width = 12F)
    // highlight-android-edit-shapes

    // highlight-android-handle-errors
    try {
        val block = engine.block.createFromSVG("<html><body>No SVG here</body></html>")
        engine.block.appendChild(parent = page, child = block)
    } catch (error: EngineException) {
        Log.i(TAG, "The import failed: ${error.code}") // BLOCK.SVG_NOT_READABLE
    }
    // highlight-android-handle-errors

    return badge
}
