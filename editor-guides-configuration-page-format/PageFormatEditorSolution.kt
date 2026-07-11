import androidx.compose.runtime.Composable
import ly.img.editor.Editor
import ly.img.editor.configuration.design.DesignConfigurationBuilder
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.engine.AssetDefinition
import ly.img.engine.AssetPayload
import ly.img.engine.AssetTransformPreset
import ly.img.engine.DesignBlockType
import ly.img.engine.DesignUnit

@Composable
fun PageFormatEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember(::DesignConfigurationBuilder) {
                onCreate = {
                    // highlight-android-register-page-source
                    val pagePresetsSourceId = "ly.img.page.presets"
                    if (editorContext.engine.asset.findAllSources().contains(pagePresetsSourceId)) {
                        editorContext.engine.asset.removeSource(pagePresetsSourceId)
                    }
                    editorContext.engine.asset.addLocalSource(
                        sourceId = pagePresetsSourceId,
                        supportedMimeTypes = emptyList(),
                    )
                    // highlight-android-register-page-source

                    // highlight-android-add-print-formats
                    editorContext.engine.asset.addAsset(
                        sourceId = pagePresetsSourceId,
                        asset = AssetDefinition(
                            id = "a4-portrait",
                            label = mapOf("en" to "A4 Portrait"),
                            groups = listOf("print"),
                            payload = AssetPayload(
                                transformPreset = AssetTransformPreset.FixedSize(
                                    width = 210F,
                                    height = 297F,
                                    designUnit = DesignUnit.MILLIMETER,
                                ),
                            ),
                        ),
                    )
                    editorContext.engine.asset.addAsset(
                        sourceId = pagePresetsSourceId,
                        asset = AssetDefinition(
                            id = "a5-landscape",
                            label = mapOf("en" to "A5 Landscape"),
                            groups = listOf("print"),
                            payload = AssetPayload(
                                transformPreset = AssetTransformPreset.FixedSize(
                                    width = 210F,
                                    height = 148F,
                                    designUnit = DesignUnit.MILLIMETER,
                                ),
                            ),
                        ),
                    )
                    // highlight-android-add-print-formats

                    // highlight-android-add-pixel-format
                    editorContext.engine.asset.addAsset(
                        sourceId = pagePresetsSourceId,
                        asset = AssetDefinition(
                            id = "square-social",
                            label = mapOf("en" to "Square Social"),
                            groups = listOf("digital"),
                            payload = AssetPayload(
                                transformPreset = AssetTransformPreset.FixedSize(
                                    width = 1080F,
                                    height = 1080F,
                                    designUnit = DesignUnit.PIXEL,
                                ),
                            ),
                        ),
                    )
                    // highlight-android-add-pixel-format

                    // highlight-android-add-inch-format
                    editorContext.engine.asset.addAsset(
                        sourceId = pagePresetsSourceId,
                        asset = AssetDefinition(
                            id = "letter",
                            label = mapOf("en" to "US Letter"),
                            groups = listOf("print"),
                            payload = AssetPayload(
                                transformPreset = AssetTransformPreset.FixedSize(
                                    width = 8.5F,
                                    height = 11F,
                                    designUnit = DesignUnit.INCH,
                                ),
                            ),
                        ),
                    )
                    // highlight-android-add-inch-format

                    // highlight-android-create-scene
                    val scene = editorContext.engine.scene.create(designUnit = DesignUnit.MILLIMETER)
                    val page = editorContext.engine.block.create(DesignBlockType.Page)
                    editorContext.engine.block.setWidth(block = page, value = 210F)
                    editorContext.engine.block.setHeight(block = page, value = 297F)
                    editorContext.engine.block.appendChild(parent = scene, child = page)
                    // highlight-android-create-scene
                }
            }
        },
        onClose = onClose,
    )
}
