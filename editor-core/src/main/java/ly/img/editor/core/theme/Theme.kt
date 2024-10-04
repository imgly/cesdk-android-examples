package ly.img.editor.core.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.rememberShimmer
import ly.img.editor.core.navbar.SystemNavBar

private val LightColorScheme =
    lightColorScheme(
        surfaceTint = md_theme_light_surfaceTint,
        onErrorContainer = md_theme_light_onErrorContainer,
        onError = md_theme_light_onError,
        errorContainer = md_theme_light_errorContainer,
        onTertiaryContainer = md_theme_light_onTertiaryContainer,
        onTertiary = md_theme_light_onTertiary,
        tertiaryContainer = md_theme_light_tertiaryContainer,
        tertiary = md_theme_light_tertiary,
        error = md_theme_light_error,
        outline = md_theme_light_outline,
        outlineVariant = md_theme_light_outlineVariant,
        onBackground = md_theme_light_onBackground,
        background = md_theme_light_background,
        inverseOnSurface = md_theme_light_inverseOnSurface,
        inverseSurface = md_theme_light_inverseSurface,
        onSurfaceVariant = md_theme_light_onSurfaceVariant,
        onSurface = md_theme_light_onSurface,
        surfaceVariant = md_theme_light_surfaceVariant,
        surface = md_theme_light_surface,
        onSecondaryContainer = md_theme_light_onSecondaryContainer,
        onSecondary = md_theme_light_onSecondary,
        secondaryContainer = md_theme_light_secondaryContainer,
        secondary = md_theme_light_secondary,
        inversePrimary = md_theme_light_inversePrimary,
        onPrimaryContainer = md_theme_light_onPrimaryContainer,
        onPrimary = md_theme_light_onPrimary,
        primaryContainer = md_theme_light_primaryContainer,
        primary = md_theme_light_primary,
        scrim = md_theme_light_scrim,
    )

private val DarkColorScheme =
    darkColorScheme(
        surfaceTint = md_theme_dark_surfaceTint,
        onErrorContainer = md_theme_dark_onErrorContainer,
        onError = md_theme_dark_onError,
        errorContainer = md_theme_dark_errorContainer,
        onTertiaryContainer = md_theme_dark_onTertiaryContainer,
        onTertiary = md_theme_dark_onTertiary,
        tertiaryContainer = md_theme_dark_tertiaryContainer,
        tertiary = md_theme_dark_tertiary,
        error = md_theme_dark_error,
        outline = md_theme_dark_outline,
        outlineVariant = md_theme_dark_outlineVariant,
        onBackground = md_theme_dark_onBackground,
        background = md_theme_dark_background,
        inverseOnSurface = md_theme_dark_inverseOnSurface,
        inverseSurface = md_theme_dark_inverseSurface,
        onSurfaceVariant = md_theme_dark_onSurfaceVariant,
        onSurface = md_theme_dark_onSurface,
        surfaceVariant = md_theme_dark_surfaceVariant,
        surface = md_theme_dark_surface,
        onSecondaryContainer = md_theme_dark_onSecondaryContainer,
        onSecondary = md_theme_dark_onSecondary,
        secondaryContainer = md_theme_dark_secondaryContainer,
        secondary = md_theme_dark_secondary,
        inversePrimary = md_theme_dark_inversePrimary,
        onPrimaryContainer = md_theme_dark_onPrimaryContainer,
        onPrimary = md_theme_dark_onPrimary,
        primaryContainer = md_theme_dark_primaryContainer,
        primary = md_theme_dark_primary,
        scrim = md_theme_dark_scrim,
    )

val ColorScheme.surface1: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)

val ColorScheme.surface2: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)

val ColorScheme.surface3: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)

/**
 * CompositionLocal that holds the current theme's color scheme.
 */
val LocalIsDarkTheme = staticCompositionLocalOf { false }

/**
 * Wrapper composable that applies the editor's color scheme and typography to any child composable. Use this if you want to apply
 * the editor styling to any other composable of your app.
 */
@Composable
fun EditorTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        LaunchedEffect(Unit) {
            val window = (view.context as Activity).window
            // Starting from android 15 following calls have no effect as it is always edge to edge and we
            // are the ones responsible drawing behind status and navigation bars.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                window.statusBarColor = Color.Transparent.toArgb()
                // Below API 26 we cannot control navigation icon color theme that's why it is better to let the system
                // draw its own color instead of editor drawing own colors and potentially having it invisible.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    window.navigationBarColor = Color.Transparent.toArgb()
                }
            }
            WindowCompat.getInsetsController(window, view).run {
                isAppearanceLightStatusBars = !useDarkTheme
                // Has no effect below API 26
                isAppearanceLightNavigationBars = !useDarkTheme
            }

            // needed for listening to keyboard changes via WindowInsets.ime
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    SystemNavBar(MaterialTheme.colorScheme.surface)

    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.Window)
    CompositionLocalProvider(
        LocalIsDarkTheme provides useDarkTheme,
        LocalShimmerTheme provides
            defaultShimmerTheme.copy(
                animationSpec =
                    infiniteRepeatable(
                        animation =
                            tween(
                                800,
                                easing = LinearEasing,
                                delayMillis = 200,
                            ),
                        repeatMode = RepeatMode.Restart,
                    ),
            ),
        LocalShimmer provides shimmer,
        LocalExtendedColorScheme provides if (useDarkTheme) darkExtendedColorScheme() else lightExtendedColorScheme(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = EditorTypography,
            content = content,
        )
    }
}

val LocalShimmer = staticCompositionLocalOf<Shimmer?> { null }
