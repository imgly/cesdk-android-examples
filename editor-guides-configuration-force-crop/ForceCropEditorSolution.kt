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
import ly.img.engine.DefaultAssetSource
import ly.img.engine.populateAssetSource

@Composable
fun ForceCropEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                onCreate = {
                    editorContext.engine.scene.createFromImage(
                        imageUri = "https://img.ly/static/ubq_samples/sample_4.jpg".toUri(),
                    )
                    // highlight-android-populate-asset-source
                    val cropPresetsSourceId = DefaultAssetSource.CROP_PRESETS.key
                    val pagePresetsSourceId = DefaultAssetSource.PAGE_PRESETS.key
                    if (editorContext.engine.asset.findAllSources().contains(cropPresetsSourceId)) {
                        editorContext.engine.asset.removeSource(cropPresetsSourceId)
                    }
                    if (editorContext.engine.asset.findAllSources().contains(pagePresetsSourceId)) {
                        editorContext.engine.asset.removeSource(pagePresetsSourceId)
                    }
                    editorContext.engine.populateAssetSource(
                        id = cropPresetsSourceId,
                        jsonUri = "file:///android_asset/force_crop_content.json".toUri(),
                        replaceBaseUri = editorContext.baseUri,
                    )
                    editorContext.engine.populateAssetSource(
                        id = pagePresetsSourceId,
                        jsonUri = "file:///android_asset/force_crop_page_content.json".toUri(),
                        replaceBaseUri = editorContext.baseUri,
                    )
                    // highlight-android-populate-asset-source
                }
                onLoaded = {
                    val page = requireNotNull(editorContext.engine.scene.getCurrentPage())
                    // highlight-android-apply-force-crop
                    val cropPresetsSourceId = DefaultAssetSource.CROP_PRESETS.key
                    val pagePresetsSourceId = DefaultAssetSource.PAGE_PRESETS.key
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
