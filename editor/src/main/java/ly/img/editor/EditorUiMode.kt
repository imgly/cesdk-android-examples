package ly.img.editor

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

/**
 * An enum class for configuring the ui mode of the editor.
 */
enum class EditorUiMode {
    /**
     * Display the editor in the same mode as the operating system.
     */
    SYSTEM,

    /**
     * Display the editor in light mode.
     */
    LIGHT,

    /**
     * Display the editor in dark mode.
     */
    DARK,
}

internal val EditorUiMode.useDarkTheme
    @Composable
    get() =
        when (this) {
            EditorUiMode.SYSTEM -> isSystemInDarkTheme()
            EditorUiMode.LIGHT -> false
            EditorUiMode.DARK -> true
        }
