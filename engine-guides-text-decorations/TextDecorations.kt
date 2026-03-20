// highlight-text-decorations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ly.img.engine.Color
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.SizeMode
import ly.img.engine.TextDecorationConfig
import ly.img.engine.TextDecorationLine
import ly.img.engine.TextDecorationStyle

fun textDecorations(
    license: String,
    userId: String,
) = CoroutineScope(Dispatchers.Main).launch {
    val engine = Engine.getInstance(id = "ly.img.engine.example")
    engine.start(license = license, userId = userId)
    engine.bindOffscreen(width = 100, height = 100)

    val scene = engine.scene.create()
    val text = engine.block.create(DesignBlockType.Text)
    engine.block.appendChild(parent = scene, child = text)
    engine.block.setWidthMode(text, mode = SizeMode.AUTO)
    engine.block.setHeightMode(text, mode = SizeMode.AUTO)
    engine.block.replaceText(text, text = "Hello CE.SDK")

    // highlight-toggle-decorations
    // Toggle underline on the entire text
    engine.block.toggleTextDecorationUnderline(text)

    // Toggle strikethrough on the entire text
    engine.block.toggleTextDecorationStrikethrough(text)

    // Toggle overline on the entire text
    engine.block.toggleTextDecorationOverline(text)

    // Calling toggle again removes the decoration
    engine.block.toggleTextDecorationOverline(text)
    // highlight-toggle-decorations

    // highlight-query-decorations
    // Query the current decoration configurations
    // Returns a list of unique TextDecorationConfig values in the range
    val decorations = engine.block.getTextDecorations(text)
    // Each config contains: lines, style, underlineColor, underlineThickness, underlineOffset, skipInk
    // highlight-query-decorations

    // highlight-custom-style
    // Set a specific decoration style
    // Available styles: SOLID, DOUBLE, DOTTED, DASHED, WAVY
    engine.block.setTextDecoration(
        text,
        TextDecorationConfig(
            lines = setOf(TextDecorationLine.UNDERLINE),
            style = TextDecorationStyle.DASHED,
        ),
    )
    // highlight-custom-style

    // highlight-underline-color
    // Set a custom underline color (only applies to underlines)
    // Strikethrough and overline always use the text color
    engine.block.setTextDecoration(
        text,
        TextDecorationConfig(
            lines = setOf(TextDecorationLine.UNDERLINE),
            underlineColor = Color.fromRGBA(r = 1F, g = 0F, b = 0F, a = 1F),
        ),
    )
    // highlight-underline-color

    // highlight-thickness
    // Adjust the underline thickness
    // Default is 1.0, values above 1.0 make the line thicker
    engine.block.setTextDecoration(
        text,
        TextDecorationConfig(
            lines = setOf(TextDecorationLine.UNDERLINE),
            underlineThickness = 2.0f,
        ),
    )
    // highlight-thickness

    // highlight-offset
    // Adjust the underline position relative to the font default
    // 0 = font default, positive values move further from baseline, negative values move closer
    engine.block.setTextDecoration(
        text,
        TextDecorationConfig(
            lines = setOf(TextDecorationLine.UNDERLINE),
            underlineOffset = 0.1f,
        ),
    )
    // highlight-offset

    // highlight-subrange
    // Apply decorations to a specific character range using UTF-16 indices
    // Toggle underline on characters 0-5 ("Hello")
    engine.block.toggleTextDecorationUnderline(text, from = 0, to = 5)

    // Set strikethrough on characters 6-12 ("CE.SDK")
    engine.block.setTextDecoration(
        text,
        TextDecorationConfig(lines = setOf(TextDecorationLine.STRIKETHROUGH)),
        from = 6,
        to = 12,
    )

    // Query decorations in a specific range
    val subrangeDecorations = engine.block.getTextDecorations(text, from = 0, to = 5)
    // highlight-subrange

    // highlight-combine
    // Combine multiple decoration lines on the same text
    // All active lines share the same style and thickness
    engine.block.setTextDecoration(
        text,
        TextDecorationConfig(
            lines = setOf(TextDecorationLine.UNDERLINE, TextDecorationLine.STRIKETHROUGH),
        ),
    )
    // highlight-combine

    // highlight-remove
    // Remove all decorations
    engine.block.setTextDecoration(
        text,
        TextDecorationConfig(lines = setOf(TextDecorationLine.NONE)),
    )
    // highlight-remove

    engine.stop()
}

// highlight-text-decorations
