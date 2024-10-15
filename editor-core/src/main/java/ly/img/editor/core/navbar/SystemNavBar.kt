package ly.img.editor.core.navbar

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

@Composable
fun SystemNavBar(navigationBarColor: Color) {
    val view = LocalView.current
    val window = (view.context as Activity).window
    DisposableEffect(Unit) {
        val colorBefore = window.navigationBarColor
        onDispose {
            window.navigationBarColor = colorBefore
        }
    }
    LaunchedEffect(navigationBarColor) {
        if (view.context.navigationBarInButtonsMode) {
            window.navigationBarColor = navigationBarColor.toArgb()
        }
    }
}

private const val NAVIGATION_BAR_INTERACTION_MODE_GESTURE = 2
private val Context.navigationBarInButtonsMode
    get(): Boolean {
        val resourceId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android")
        val mode = if (resourceId > 0) resources.getInteger(resourceId) else 0
        return mode != NAVIGATION_BAR_INTERACTION_MODE_GESTURE
    }
