import androidx.compose.runtime.Composable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ly.img.editor.Editor
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.engine.DesignBlockType
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.SizeMode
import ly.img.engine.UnstableEngineApi

@OptIn(UnstableEngineApi::class)
@Composable
fun EditorStateEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                onCreate = {
                    val scene = editorContext.engine.scene.create()
                    // highlight-android-editor-state-setup
                    val page = editorContext.engine.block.create(DesignBlockType.Page)
                    editorContext.engine.block.setWidth(page, value = 800F)
                    editorContext.engine.block.setHeight(page, value = 600F)
                    editorContext.engine.block.appendChild(parent = scene, child = page)

                    // Add an image block to demonstrate Crop mode
                    val imageBlock = editorContext.engine.block.create(DesignBlockType.Graphic)
                    editorContext.engine.block.setName(imageBlock, name = "editor-state-image")
                    editorContext.engine.block.setShape(
                        imageBlock,
                        shape = editorContext.engine.block.createShape(ShapeType.Rect),
                    )
                    editorContext.engine.block.setWidth(imageBlock, value = 350F)
                    editorContext.engine.block.setHeight(imageBlock, value = 250F)
                    editorContext.engine.block.setPositionX(imageBlock, value = 50F)
                    editorContext.engine.block.setPositionY(imageBlock, value = 175F)

                    val imageFill = editorContext.engine.block.createFill(FillType.Image)
                    editorContext.engine.block.setString(
                        block = imageFill,
                        property = "fill/image/imageFileURI",
                        value = "https://img.ly/static/ubq_samples/sample_1.jpg",
                    )
                    editorContext.engine.block.setFill(imageBlock, fill = imageFill)
                    editorContext.engine.block.appendChild(parent = page, child = imageBlock)

                    // Add a text block to demonstrate Text mode
                    val textBlock = editorContext.engine.block.create(DesignBlockType.Text)
                    editorContext.engine.block.setName(textBlock, name = "editor-state-text")
                    editorContext.engine.block.appendChild(parent = page, child = textBlock)
                    editorContext.engine.block.replaceText(textBlock, text = "Edit this text")
                    editorContext.engine.block.setTextFontSize(textBlock, fontSize = 48F)
                    editorContext.engine.block.setWidthMode(textBlock, mode = SizeMode.AUTO)
                    editorContext.engine.block.setHeightMode(textBlock, mode = SizeMode.AUTO)
                    editorContext.engine.block.setPositionX(textBlock, value = 450F)
                    editorContext.engine.block.setPositionY(textBlock, value = 275F)
                    // highlight-android-editor-state-setup
                }
                onLoaded = {
                    val engine = editorContext.engine
                    val imageBlock = engine.block.findByName(name = "editor-state-image").first()
                    val textBlock = engine.block.findByName(name = "editor-state-text").first()
                    val requiredSources = setOf("ly.img.crop.presets", "ly.img.page.presets")

                    coroutineScope {
                        val existingSources = engine.asset.findAllSources().toSet()
                        requiredSources
                            .filterNot { it in existingSources }
                            .forEach { sourceId ->
                                engine.asset.addLocalSourceFromJSON(
                                    contentUri = editorContext.baseUri.buildUpon()
                                        .appendPath(sourceId)
                                        .appendPath("content.json")
                                        .build(),
                                )
                            }

                        // highlight-android-editor-state-on-state-changed
                        launch {
                            engine.editor.onStateChanged()
                                .map { engine.editor.getEditMode() }
                                .distinctUntilChanged()
                                .collect { currentMode ->
                                    println("Edit mode changed to: $currentMode")
                                }
                        }
                        // highlight-android-editor-state-on-state-changed

                        // highlight-android-editor-state-get-edit-mode
                        val initialMode = engine.editor.getEditMode()
                        println("Initial edit mode: $initialMode")
                        // highlight-android-editor-state-get-edit-mode

                        engine.block.select(imageBlock)
                        // highlight-android-editor-state-set-edit-mode
                        engine.editor.setEditMode("Crop")
                        println(
                            "Edit mode changed to: ${engine.editor.getEditMode()} " +
                                "(requested before entering the crop-based demo state)",
                        )
                        // highlight-android-editor-state-set-edit-mode

                        // highlight-android-editor-state-custom-edit-mode
                        engine.editor.setEditMode(
                            editMode = "MyCustomCropMode",
                            baseMode = "Crop",
                        )
                        println(
                            "Edit mode changed to: ${engine.editor.getEditMode()} " +
                                "(steady state after launch)",
                        )
                        // highlight-android-editor-state-custom-edit-mode

                        engine.block.select(textBlock)
                        engine.editor.setEditMode("Text")

                        // highlight-android-editor-state-text-cursor-position
                        val textCursorX = engine.editor.getTextCursorPositionInScreenSpaceX()
                        val textCursorY = engine.editor.getTextCursorPositionInScreenSpaceY()
                        println(
                            "Text cursor position before placing a live caret: " +
                                "($textCursorX, $textCursorY)",
                        )
                        // highlight-android-editor-state-text-cursor-position

                        engine.block.select(imageBlock)
                        engine.editor.setEditMode(
                            editMode = "MyCustomCropMode",
                            baseMode = "Crop",
                        )
                        println(
                            "Edit mode changed to: ${engine.editor.getEditMode()} " +
                                "(restored after the text-cursor check)",
                        )

                        // highlight-android-editor-state-interaction-happening
                        val isInteracting = engine.editor.isInteractionHappening()
                        println("Is interaction happening: $isInteracting")
                        // highlight-android-editor-state-interaction-happening
                    }
                }
            }
        },
        onClose = onClose,
    )
}
