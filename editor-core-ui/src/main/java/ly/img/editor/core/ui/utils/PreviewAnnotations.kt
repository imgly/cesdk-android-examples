package ly.img.editor.core.ui.utils

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

// Pre-made preview annotations for easier preview creation

@Preview(
    name = "light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    backgroundColor = 0xffffffff,
)
@Preview(
    name = "dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    backgroundColor = 0xff1b1b1f,
)
annotation class ThemePreview
