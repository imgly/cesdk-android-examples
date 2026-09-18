import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
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

@Composable
fun ForceCropEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    val context = LocalContext.current
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                onCreate = {
                    editorContext.engine.scene.createFromImage(
                        imageUri = "https://img.ly/static/ubq_samples/sample_4.jpg".toUri(),
                    )
                    // highlight-android-populate-asset-source
                    val cropPresetsSourceId = "ly.img.crop.presets"
                    val pagePresetsSourceId = "ly.img.page.presets"
                    if (editorContext.engine.asset.findAllSources().contains(cropPresetsSourceId)) {
                        editorContext.engine.asset.removeSource(cropPresetsSourceId)
                    }
                    if (editorContext.engine.asset.findAllSources().contains(pagePresetsSourceId)) {
                        editorContext.engine.asset.removeSource(pagePresetsSourceId)
                    }
                    editorContext.engine.asset.addLocalSourceFromJSON(
                        contentJSON = context.assets.open("force_crop_content.json").bufferedReader().use { it.readText() },
                        basePath = editorContext.baseUri.toString(),
                    )
                    editorContext.engine.asset.addLocalSourceFromJSON(
                        contentJSON = context.assets.open("force_crop_page_content.json").bufferedReader().use { it.readText() },
                        basePath = editorContext.baseUri.toString(),
                    )
                    // highlight-android-populate-asset-source
                }
                onLoaded = {
                    val page = requireNotNull(editorContext.engine.scene.getCurrentPage())
                    // highlight-android-apply-force-crop
                    val cropPresetsSourceId = "ly.img.crop.presets"
                    val pagePresetsSourceId = "ly.img.page.presets"
                    // Android treats the top-level preset and presetCandidates as one candidate list.
                    val configuration = ForceCropConfiguration(
                        sourceId = cropPresetsSourceId,
                        presetId = "instagram-portrait",
                        presetCandidates = listOf(
                            ForceCropPresetCandidate(
                                sourceId = pagePresetsSourceId,
                                presetId = "profile-photo",
                            ),
                            ForceCropPresetCandidate(
                                sourceId = cropPresetsSourceId,
                                presetId = "aspect-ratio-1-1",
                            ),
                        ),
                        mode = ForceCropMode.IfNeeded(),
                    )
                    editorContext.eventHandler.send(
                        event = EditorEvent.ApplyForceCrop(
                            designBlock = page,
                            configuration = configuration,
                        ),
                    )
                    // highlight-android-apply-force-crop
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
