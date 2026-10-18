package ly.img.editor.configuration.memories

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.configuration.memories.screen.ImageSelectionScreen

enum class AppScreen {
    ImageSelection,
    VideoEditor,
}

/**
 * The complete Memories experience: photo picker (device images and videos) →
 * loading/analysis → cinematic slideshow editor.
 *
 * This is the single entry point used by every host — the standalone app, the examples app, and
 * the showcases app — so the flow is identical everywhere.
 *
 * @param license CE.SDK license key, or null for evaluation mode (adds a watermark).
 * @param onExit invoked when the user navigates back from the picker (the root of the flow) —
 * e.g. finish the activity (standalone) or pop the back stack (demo apps).
 */
@Composable
fun MemoriesApp(
    license: String?,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MemoriesViewModel = viewModel(),
) {
    val context = LocalContext.current
    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.ImageSelection) }

    LaunchedEffect(viewModel) {
        viewModel.setContext(context)
        // After process death the saved screen can restore to the editor while the ViewModel is
        // recreated empty — don't land in the editor with no media; return to the picker instead.
        if (currentScreen == AppScreen.VideoEditor && viewModel.selectedImages.value.isEmpty()) {
            currentScreen = AppScreen.ImageSelection
        }
    }

    when (currentScreen) {
        AppScreen.ImageSelection -> {
            // Back from the picker leaves the flow. ImageSelectionScreen registers its own
            // (higher-priority) BackHandler for multi-select mode, so this only fires otherwise.
            BackHandler { onExit() }
            Scaffold { paddingValues ->
                ImageSelectionScreen(
                    viewModel = viewModel,
                    onProceedToEditor = {
                        viewModel.setEditorLoading(true)
                        currentScreen = AppScreen.VideoEditor
                    },
                    modifier = modifier.padding(paddingValues),
                )
            }
        }
        AppScreen.VideoEditor -> {
            MemoriesEditor(
                license = license,
                viewModel = viewModel,
                onCloseEditor = { currentScreen = AppScreen.ImageSelection },
                modifier = modifier,
            )
        }
    }
}
