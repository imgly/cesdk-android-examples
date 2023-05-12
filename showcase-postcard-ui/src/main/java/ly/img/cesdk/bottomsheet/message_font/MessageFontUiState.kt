package ly.img.cesdk.bottomsheet.message_font

import ly.img.cesdk.library.data.font.FontFamilyData
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

data class MessageFontUiState(
    val fontFamily: String,
    val fontFamilies: List<FontFamilyData>
)

internal fun createMessageFontUiState(
    designBlock: DesignBlock,
    engine: Engine,
    families: Map<String, FontFamilyData>
): MessageFontUiState {
    val fontPath = engine.block.getString(designBlock, "text/fontFileUri")

    var fontFamilyData: FontFamilyData? = null

    for (value in families.values) {
        value.getFontData(fontPath) ?: continue
        fontFamilyData = value
        break
    }

    val requiredFamiliesSet = hashSetOf(
        "Caveat", "AmaticSC", "Courier Prime", "Archivo", "Roboto", "Parisienne"
    )

    return MessageFontUiState(
        requireNotNull(fontFamilyData).name,
        families.values.filter { requiredFamiliesSet.contains(it.name) }
    )
}