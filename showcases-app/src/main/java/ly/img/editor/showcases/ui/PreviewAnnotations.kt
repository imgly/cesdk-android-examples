package ly.img.editor.showcases.ui

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    showBackground = true,
    backgroundColor = 0xffFFFFFF,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light",
)
@Preview(
    showBackground = true,
    backgroundColor = 0xff212121,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark",
)
annotation class ThemePreview
