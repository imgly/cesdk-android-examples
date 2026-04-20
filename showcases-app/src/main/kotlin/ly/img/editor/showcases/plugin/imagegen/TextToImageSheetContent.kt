package ly.img.editor.showcases.plugin.imagegen

import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import ly.img.editor.showcases.plugin.imagegen.api.FalAIApi
import ly.img.editor.showcases.plugin.imagegen.api.FalImageStylesImage
import ly.img.editor.showcases.plugin.imagegen.api.FalImageStylesVector
import ly.img.editor.showcases.plugin.imagegen.component.AIImageGenerationContent
import ly.img.editor.showcases.plugin.imagegen.component.FormatPickerDialog
import ly.img.editor.showcases.plugin.imagegen.component.FullScreenStyleDialog
import ly.img.editor.showcases.ui.preview.PreviewTheme

@Composable
fun TextToImageSheetContent(
    apiConfig: String,
    initialImageUri: String? = null,
    handleImageGeneration: (AIImageGenerationState, suspend () -> List<String>) -> Unit = { _, _ -> },
    onCloseSheet: () -> Unit = {},
) {
    var state by remember { mutableStateOf(AIImageGenerationState(imageUri = initialImageUri)) }
    var showFormatDialog by remember { mutableStateOf(false) }
    var showStyleDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { mediaUri ->
        mediaUri?.let { uri ->
            state = state.copy(
                imageUri = uri.toString(),
                isImageSelected = true,
            )
        }
    }

    BackHandler {
        onCloseSheet()
    }

    AIImageGenerationContent(
        state = state,
        onPromptChange = { state = state.copy(prompt = it) },
        onAddImageClick = {
            imageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onRemoveImageClick = {
            state = state.copy(
                imageUri = null,
                isImageSelected = false,
            )
        },
        onMakeItClick = {
            if (state.prompt.isNotBlank()) {
                handleImageGeneration(state) {
                    FalAIApi.generateImageWithFalAI(
                        prompt = state.prompt,
                        style = state.selectedStyle,
                        imageSize = if (state.selectedFormat == Format.CUSTOM) {
                            mapOf<String, Any>(
                                "width" to (state.customWidth.toIntOrNull() ?: 1920),
                                "height" to (state.customHeight.toIntOrNull() ?: 1080),
                            )
                        } else {
                            state.selectedFormat.falImageSize
                        },
                        inputImageUri = state.imageUri.orEmpty(),
                        context = context,
                        apiConfig = apiConfig,
                    )
                }
                true
            } else {
                Log.w("FalAIImageGenPlugin", "Prompt is empty, cannot generate image")
                false
            }
        },
        onStyleClick = { showStyleDialog = true },
        onAspectRatioClick = { showFormatDialog = true },
        onCloseSheet = onCloseSheet,
        onOutputSelected = { output ->
            val newStyle = when (output) {
                OutputType.IMAGE -> {
                    if (state.selectedStyle in FalImageStylesVector) {
                        FalImageStylesImage.first()
                    } else {
                        state.selectedStyle
                    }
                }
                OutputType.VECTOR -> {
                    if (state.selectedStyle in FalImageStylesImage) {
                        FalImageStylesVector.first()
                    } else {
                        state.selectedStyle
                    }
                }
            }
            state = state.copy(selectedOutput = output, selectedStyle = newStyle)
        },
    )

    if (showFormatDialog) {
        FormatPickerDialog(
            selectedFormat = state.selectedFormat,
            customWidth = state.customWidth,
            customHeight = state.customHeight,
            onFormatSelected = { format ->
                state = state.copy(selectedFormat = format)
                if (format != Format.CUSTOM) {
                    showFormatDialog = false
                }
            },
            onCustomWidthChange = { width ->
                state = state.copy(customWidth = width)
            },
            onCustomHeightChange = { height ->
                state = state.copy(customHeight = height)
            },
            onDismiss = { showFormatDialog = false },
        )
    }

    if (showStyleDialog) {
        FullScreenStyleDialog(
            selectedStyle = state.selectedStyle,
            selectedOutput = state.selectedOutput,
            onStyleSelected = { style ->
                state = state.copy(selectedStyle = style)
                showStyleDialog = false
            },
            onDismiss = { showStyleDialog = false },
        )
    }
}

@Composable
@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun TextToImageSheetContentPreview() {
    PreviewTheme {
        Surface {
            TextToImageSheetContent(
                apiConfig = "",
                onCloseSheet = {},
            )
        }
    }
}
