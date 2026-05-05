import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import ly.img.editor.Editor
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.data.ForceCropConfiguration
import ly.img.editor.core.component.data.ForceCropMode
import ly.img.editor.core.component.data.ForceCropPresetCandidate
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberCrop
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.engine.populateAssetSource

// Add this composable to your NavHost
@Composable
fun ForceCropEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            val cropPresetsSourceId = "ly.img.crop.presets"
            EditorConfiguration.remember {
                onCreate = {
                    editorContext.engine.scene.createFromImage(
                        imageUri = "https://img.ly/static/ubq_samples/sample_4.jpg".toUri(),
                    )
                    // highlight-populate-asset-source
                    editorContext.engine.populateAssetSource(
                        id = cropPresetsSourceId,
                        jsonUri = "file:///android_asset/force_crop_content.json".toUri(),
                        replaceBaseUri = editorContext.baseUri,
                    )
                    // highlight-populate-asset-source
                }
                onLoaded = {
                    val page = requireNotNull(editorContext.engine.scene.getCurrentPage())
                    // highlight-create-force-crop
                    val configuration = ForceCropConfiguration(
                        sourceId = cropPresetsSourceId,
                        presetId = "aspect-ratio-1-1",
                        presetCandidates = listOf(
                            ForceCropPresetCandidate(
                                sourceId = cropPresetsSourceId,
                                presetId = "aspect-ratio-4-3",
                            ),
                            ForceCropPresetCandidate(
                                sourceId = cropPresetsSourceId,
                                presetId = "aspect-ratio-3-4",
                            ),
                        ),
                        mode = ForceCropMode.Silent,
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
                dock = {
                    Dock.remember {
                        horizontalArrangement = { Arrangement.Center }
                        listBuilder = {
                            Dock.ListBuilder.remember {
                                add { Dock.Button.rememberCrop() }
                            }
                        }
                    }
                }
            }
        },
        onClose = onClose,
    )
}
