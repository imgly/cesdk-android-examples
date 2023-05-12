package ly.img.cesdk.bottomsheet.template_colors

import androidx.compose.ui.graphics.Color
import ly.img.cesdk.util.NamedColor

data class TemplateColorsUiState(
    val colorPalette: List<Color>,
    val colorSections: List<NamedColor>
)