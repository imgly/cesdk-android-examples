package ly.img.editor.postcard.bottomsheet.message_font

import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.ui.library.TypefaceLibraryCategory
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

data class MessageFontUiState(
    val libraryCategory: LibraryCategory,
    val fontFamily: String,
    val filter: List<String>,
)

internal fun createMessageFontUiState(
    designBlock: DesignBlock,
    engine: Engine,
): MessageFontUiState {
    val typeface = runCatching { engine.block.getTypeface(designBlock) }.getOrNull()
    return MessageFontUiState(
        libraryCategory = TypefaceLibraryCategory,
        fontFamily = typeface?.name ?: "Default",
        filter =
            listOf(
                "//ly.img.typeface/caveat",
                "//ly.img.typeface/amaticsc",
                "//ly.img.typeface/courier_prime",
                "//ly.img.typeface/archivo",
                "//ly.img.typeface/roboto",
                "//ly.img.typeface/parisienne",
            ),
    )
}
