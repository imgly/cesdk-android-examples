import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode

data class TextAutoSize(
    val autoWidthMode: SizeMode,
    val autoHeightMode: SizeMode,
    val automaticFontSizeEnabled: Boolean,
    val minAutomaticFontSize: Float,
    val maxAutomaticFontSize: Float,
    val hasClippedLines: Boolean,
)

suspend fun textAutoSize(engine: Engine): TextAutoSize {
    val scene = engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(page, value = 800F)
    engine.block.setHeight(page, value = 600F)
    engine.block.appendChild(parent = scene, child = page)

    val background = engine.block.create(DesignBlockType.Graphic)
    engine.block.setShape(background, shape = engine.block.createShape(ShapeType.Rect))
    engine.block.setWidth(background, value = 800F)
    engine.block.setHeight(background, value = 600F)
    engine.block.setFill(background, fill = engine.block.createFill(FillType.Color))
    engine.block.setFillSolidColor(background, color = Color.fromHex("#FFF7F5EF"))
    engine.block.appendChild(parent = page, child = background)

    // highlight-android-auto-width-height
    val autoText = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = autoText)
    engine.block.setWidthMode(autoText, mode = SizeMode.AUTO)
    engine.block.setHeightMode(autoText, mode = SizeMode.AUTO)
    engine.block.replaceText(autoText, text = "Auto-sized text")
    engine.block.setTextFontSize(autoText, fontSize = 48F)
    engine.block.setTextColor(autoText, color = Color.fromHex("#FF1F2937"))
    engine.block.setPositionX(autoText, value = 50F)
    engine.block.setPositionY(autoText, value = 50F)
    // highlight-android-auto-width-height

    // highlight-android-fixed-width-auto-height
    val wrappedText = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = wrappedText)
    engine.block.setWidthMode(wrappedText, mode = SizeMode.ABSOLUTE)
    engine.block.setWidth(wrappedText, value = 250F)
    engine.block.setHeightMode(wrappedText, mode = SizeMode.AUTO)
    engine.block.replaceText(
        wrappedText,
        text = "This text has a fixed width but auto height, so it wraps to multiple lines",
    )
    engine.block.setTextFontSize(wrappedText, fontSize = 48F)
    engine.block.setTextColor(wrappedText, color = Color.fromHex("#FF334155"))
    engine.block.setPositionX(wrappedText, value = 50F)
    engine.block.setPositionY(wrappedText, value = 150F)
    // highlight-android-fixed-width-auto-height

    // highlight-android-automatic-font-sizing
    val scaledText = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = scaledText)
    engine.block.setWidthMode(scaledText, mode = SizeMode.ABSOLUTE)
    engine.block.setHeightMode(scaledText, mode = SizeMode.ABSOLUTE)
    engine.block.setWidth(scaledText, value = 300F)
    engine.block.setHeight(scaledText, value = 80F)
    engine.block.setBoolean(scaledText, property = "text/automaticFontSizeEnabled", value = true)
    engine.block.replaceText(scaledText, text = "Auto-scaled font")
    engine.block.setTextColor(scaledText, color = Color.fromHex("#FF1D4ED8"))
    engine.block.setPositionX(scaledText, value = 50F)
    engine.block.setPositionY(scaledText, value = 350F)
    // highlight-android-automatic-font-sizing

    // highlight-android-font-size-constraints
    val constrainedText = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = constrainedText)
    engine.block.setWidthMode(constrainedText, mode = SizeMode.ABSOLUTE)
    engine.block.setHeightMode(constrainedText, mode = SizeMode.ABSOLUTE)
    engine.block.setWidth(constrainedText, value = 300F)
    engine.block.setHeight(constrainedText, value = 80F)
    engine.block.setBoolean(constrainedText, property = "text/automaticFontSizeEnabled", value = true)
    engine.block.setFloat(constrainedText, property = "text/minAutomaticFontSize", value = 12F)
    engine.block.setFloat(constrainedText, property = "text/maxAutomaticFontSize", value = 48F)
    engine.block.replaceText(
        constrainedText,
        text = "Edit this text to see automatic font scaling in a 12-48 pt range",
    )
    engine.block.setTextColor(constrainedText, color = Color.fromHex("#FF7C2D12"))
    engine.block.setPositionX(constrainedText, value = 50F)
    engine.block.setPositionY(constrainedText, value = 460F)
    // highlight-android-font-size-constraints

    // highlight-android-query-size-modes
    val widthMode = engine.block.getWidthMode(autoText)
    val heightMode = engine.block.getHeightMode(autoText)
    // highlight-android-query-size-modes

    val isAutomaticFontSizeEnabled = engine.block.getBoolean(
        scaledText,
        property = "text/automaticFontSizeEnabled",
    )
    val minAutomaticFontSize = engine.block.getFloat(
        constrainedText,
        property = "text/minAutomaticFontSize",
    )
    val maxAutomaticFontSize = engine.block.getFloat(
        constrainedText,
        property = "text/maxAutomaticFontSize",
    )

    // highlight-android-text-clipping
    val clippedText = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = page, child = clippedText)
    engine.block.setWidthMode(clippedText, mode = SizeMode.ABSOLUTE)
    engine.block.setHeightMode(clippedText, mode = SizeMode.ABSOLUTE)
    engine.block.setWidth(clippedText, value = 300F)
    engine.block.setHeight(clippedText, value = 60F)
    engine.block.replaceText(
        clippedText,
        text = "This line fits.\nThis line overflows.\nThis line is clipped.",
    )
    engine.block.setTextFontSize(clippedText, fontSize = 48F)
    engine.block.setBoolean(clippedText, property = "text/clipLinesOutsideOfFrame", value = true)
    var hasClippedLines = false
    var layoutPasses = 0
    while (!hasClippedLines && layoutPasses < 10) {
        yield()
        delay(16) // Let the bound engine process a frame before reading layout-backed state.
        hasClippedLines = engine.block.getBoolean(
            clippedText,
            property = "text/hasClippedLines",
        )
        layoutPasses += 1
    }
    // highlight-android-text-clipping

    engine.block.setTextColor(clippedText, color = Color.fromHex("#FF991B1B"))
    engine.block.setPositionX(clippedText, value = 430F)
    engine.block.setPositionY(clippedText, value = 350F)

    return TextAutoSize(
        autoWidthMode = widthMode,
        autoHeightMode = heightMode,
        automaticFontSizeEnabled = isAutomaticFontSizeEnabled,
        minAutomaticFontSize = minAutomaticFontSize,
        maxAutomaticFontSize = maxAutomaticFontSize,
        hasClippedLines = hasClippedLines,
    )
}
