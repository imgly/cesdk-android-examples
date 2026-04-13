import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import ly.img.editor.Editor
import ly.img.editor.core.component.data.ForceCropConfiguration
import ly.img.editor.core.component.data.ForceCropMode
import ly.img.editor.core.component.data.ForceCropPresetCandidate
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.engine.populateAssetSource

// Add this composable to your NavHost
@Composable
fun ForceCropEditorSolution(navController: NavHostController) {
    Editor(
        license = null, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                onCreate = {
                    editorContext.engine.scene.createFromImage(
                        imageUri = "https://img.ly/static/ubq_samples/sample_4.jpg".toUri(),
                    )
                    val assetSource = "ly.img.crop.presets"
                    editorContext.engine.populateAssetSource(
                        id = assetSource,
                        jsonUri = "${editorContext.baseUri}/$assetSource/content.json".toUri(),
                        replaceBaseUri = editorContext.baseUri,
                    )
                }
                onLoaded = {
                    val page = requireNotNull(editorContext.engine.scene.getCurrentPage())
                    // highlight-create-force-crop
                    val configuration = ForceCropConfiguration(
                        sourceId = "ly.img.crop.presets",
                        presetId = "aspect-ratio-1-1",
                        presetCandidates = listOf(
                            ForceCropPresetCandidate(
                                sourceId = "ly.img.crop.presets",
                                presetId = "aspect-ratio-16-9",
                            ),
                            ForceCropPresetCandidate(
                                sourceId = "ly.img.crop.presets",
                                presetId = "aspect-ratio-9-16",
                            ),
                        ),
                        mode = ForceCropMode.IfNeeded(threshold = 0.01f),
                    )
                    // highlight-create-force-crop
                    // highlight-apply-force-crop
                    editorContext.eventHandler.send(
                        event = EditorEvent.ApplyForceCrop(
                            designBlock = page,
                            configuration = configuration,
                        ),
                    )
                    // highlight-apply-force-crop
                }
            }
        },
    ) {
        // You can set result here
        navController.popBackStack()
    }
}
