package ly.img.editor.core.ui.library.data.font

import android.net.Uri
import androidx.compose.ui.text.font.FontWeight
import ly.img.engine.FontStyle
import ly.img.engine.Typeface

data class FontData(
    val typeface: Typeface,
    val uri: Uri,
    val weight: FontWeight,
    val style: FontStyle,
)
