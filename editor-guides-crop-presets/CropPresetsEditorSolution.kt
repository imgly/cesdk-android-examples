import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import kotlinx.coroutines.delay
import ly.img.editor.Editor
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberCrop
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.sheet.SheetType
import ly.img.engine.DesignBlockType
import ly.img.engine.FillType
import ly.img.engine.ShapeType

private const val CropPresetsImageBlockName = "crop-presets-image"

@Composable
fun CropPresetsEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                onCreate = {
                    val scene = editorContext.engine.scene.create()
                    val page = editorContext.engine.block.create(DesignBlockType.Page)
                    editorContext.engine.block.setWidth(page, value = 800F)
                    editorContext.engine.block.setHeight(page, value = 600F)
                    editorContext.engine.block.appendChild(parent = scene, child = page)

                    val imageBlock = editorContext.engine.block.create(DesignBlockType.Graphic)
                    editorContext.engine.block.setName(imageBlock, name = CropPresetsImageBlockName)
                    editorContext.engine.block.setShape(
                        imageBlock,
                        shape = editorContext.engine.block.createShape(ShapeType.Rect),
                    )
                    editorContext.engine.block.setWidth(imageBlock, value = 520F)
                    editorContext.engine.block.setHeight(imageBlock, value = 360F)
                    editorContext.engine.block.setPositionX(imageBlock, value = 140F)
                    editorContext.engine.block.setPositionY(imageBlock, value = 120F)

                    val imageFill = editorContext.engine.block.createFill(FillType.Image)
                    editorContext.engine.block.setString(
                        block = imageFill,
                        property = "fill/image/imageFileURI",
                        value = "https://img.ly/static/ubq_samples/sample_4.jpg",
                    )
                    editorContext.engine.block.setFill(imageBlock, fill = imageFill)
                    editorContext.engine.block.appendChild(parent = page, child = imageBlock)

                    // highlight-android-replace-crop-presets
                    val cropPresetSourceId = "ly.img.crop.presets"
                    val cropPresetContent = """
                        {
                          "version": "7.0.0",
                          "id": "ly.img.crop.presets",
                          "assets": [
                            {
                              "id": "aspect-ratio-free",
                              "label": { "en": "Free" },
                              "payload": { "transformPreset": { "type": "FreeAspectRatio" } },
                              "groups": ["fixed-ratio"]
                            },
                            {
                              "id": "aspect-ratio-16-9",
                              "label": { "en": "16:9" },
                              "payload": {
                                "transformPreset": {
                                  "type": "FixedAspectRatio",
                                  "width": 16,
                                  "height": 9
                                }
                              },
                              "groups": ["fixed-ratio"]
                            }
                          ]
                        }
                    """.trimIndent()

                    if (cropPresetSourceId in editorContext.engine.asset.findAllSources()) {
                        editorContext.engine.asset.removeSource(cropPresetSourceId)
                    }

                    editorContext.engine.asset.addLocalSourceFromJSON(
                        contentJSON = cropPresetContent,
                    )
                    // highlight-android-replace-crop-presets
                }
                onLoaded = {
                    // Wait for the first editor frame so the demo route opens directly in crop mode.
                    delay(500)
                    val imageBlock = editorContext.engine.block.findByName(CropPresetsImageBlockName).first()
                    editorContext.engine.block.select(imageBlock)
                    editorContext.eventHandler.send(
                        event = EditorEvent.Sheet.Open(
                            SheetType.Crop(mode = SheetType.Crop.Mode.ImageCrop),
                        ),
                    )
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
