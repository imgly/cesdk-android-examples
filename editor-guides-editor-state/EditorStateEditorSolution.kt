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
                    // highlight-editorState-setup
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
                    // highlight-editorState-setup
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

                        // highlight-editorState-onStateChanged
                        launch {
                            engine.editor.onStateChanged()
                                .map { engine.editor.getEditMode() }
                                .distinctUntilChanged()
                                .collect { currentMode ->
                                    println("Edit mode changed to: $currentMode")
                                }
                        }
                        // highlight-editorState-onStateChanged

                        // highlight-editorState-getEditMode
                        val initialMode = engine.editor.getEditMode()
                        println("Initial edit mode: $initialMode")
                        // highlight-editorState-getEditMode

                        engine.block.select(imageBlock)
                        // highlight-editorState-setEditMode
                        engine.editor.setEditMode("Crop")
                        println(
                            "Edit mode changed to: ${engine.editor.getEditMode()} " +
                                "(requested before entering the crop-based demo state)",
                        )
                        // highlight-editorState-setEditMode

                        // highlight-editorState-customEditMode
                        engine.editor.setEditMode(
                            editMode = "MyCustomCropMode",
                            baseMode = "Crop",
                        )
                        println(
                            "Edit mode changed to: ${engine.editor.getEditMode()} " +
                                "(steady state after launch)",
                        )
                        // highlight-editorState-customEditMode

                        engine.block.select(textBlock)
                        engine.editor.setEditMode("Text")

                        // highlight-editorState-textCursorPosition
                        val textCursorX = engine.editor.getTextCursorPositionInScreenSpaceX()
                        val textCursorY = engine.editor.getTextCursorPositionInScreenSpaceY()
                        println(
                            "Text cursor position before placing a live caret: " +
                                "($textCursorX, $textCursorY)",
                        )
                        // highlight-editorState-textCursorPosition

                        engine.block.select(imageBlock)
                        engine.editor.setEditMode(
                            editMode = "MyCustomCropMode",
                            baseMode = "Crop",
                        )
                        println(
                            "Edit mode changed to: ${engine.editor.getEditMode()} " +
                                "(restored after the text-cursor check)",
                        )

                        // highlight-editorState-interactionHappening
                        val isInteracting = engine.editor.isInteractionHappening()
                        println("Is interaction happening: $isInteracting")
                        // highlight-editorState-interactionHappening
                    }
                }
            }
        },
        onClose = onClose,
    )
}
