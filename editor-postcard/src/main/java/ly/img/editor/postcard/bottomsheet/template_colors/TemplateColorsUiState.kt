package ly.img.editor.postcard.bottomsheet.template_colors

import androidx.compose.ui.graphics.Color
import ly.img.editor.postcard.util.NamedColor

data class TemplateColorsUiState(
    val colorPalette: List<Color>,
    val colorSections: List<NamedColor>,
)
