import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ly.img.editor.Editor
import ly.img.editor.core.EditorContext
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.component.data.Height
import ly.img.editor.core.component.remember
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.Preview
import ly.img.editor.core.sheet.SheetStyle
import ly.img.editor.core.sheet.SheetType
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.FillType
import ly.img.engine.ShapeType
import ly.img.engine.Color as EngineColor

private const val DEMO_BLOCK_NAME = "com.example.guides.customPanel.selection"

@Composable
fun CustomPanelSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                onLoaded = {
                    val engine = editorContext.engine
                    val scene = engine.scene.get() ?: engine.scene.create()
                    val page = engine.scene.getCurrentPage() ?: engine.scene.getPages().firstOrNull() ?: engine.block
                        .create(DesignBlockType.Page)
                        .also {
                            engine.block.setWidth(block = it, value = 1080F)
                            engine.block.setHeight(block = it, value = 1080F)
                            engine.block.appendChild(parent = scene, child = it)
                        }
                    val block = engine.block.findByName(DEMO_BLOCK_NAME).firstOrNull() ?: engine.block
                        .create(DesignBlockType.Graphic)
                        .also {
                            engine.block.setName(block = it, name = DEMO_BLOCK_NAME)
                            engine.block.setShape(
                                block = it,
                                shape = engine.block.createShape(ShapeType.Rect),
                            )
                            val fill = engine.block.createFill(FillType.Color)
                            engine.block.setFill(block = it, fill = fill)
                            engine.block.setFillSolidColor(
                                block = it,
                                color = EngineColor.fromRGBA(r = 0.32F, g = 0.58F, b = 0.94F, a = 1F),
                            )
                            engine.block.setWidth(block = it, value = 320F)
                            engine.block.setHeight(block = it, value = 220F)
                            engine.block.setPositionX(block = it, value = 380F)
                            engine.block.setPositionY(block = it, value = 360F)
                            engine.block.appendChild(parent = page, child = it)
                        }
                    engine.block.bringToFront(block = block)
                    engine.block.setSelected(block = block, selected = true)
                    engine.scene.zoomToBlock(page)

                    parentConfiguration?.onLoaded?.invoke(this)
                }
                inspectorBar = {
                    InspectorBar.remember {
                        listBuilder = {
                            InspectorBar.ListBuilder.remember {
                                add { openPropertyPanelButton }
                            }
                        }
                    }
                }
            }
        },
        onClose = onClose,
    )
}

val openPropertyPanelButton
    @Composable get() =
        // highlight-android-inspector-button
        InspectorBar.Button.remember {
            id = { EditorComponentId("open_property_panel") }
            text = { Text("Properties") }
            icon = { Icon(IconPack.Preview, contentDescription = "Open properties panel") }
            onClick = {
                val selectedBlock = editorContext.selection.designBlock
                editorContext.eventHandler.send(
                    EditorEvent.Sheet.Open(propertySheetType(selectedBlock)),
                )
            }
        }
// highlight-android-inspector-button

// highlight-android-open-custom-panel
fun propertySheetType(block: DesignBlock): SheetType = SheetType.Custom(
    style = SheetStyle(
        isFloating = false,
        minHeight = Height.Exactly(0.dp),
        maxHeight = Height.Fraction(0.7F),
        isHalfExpandingEnabled = false,
        isHalfExpandedInitially = false,
        animateInitialValue = true,
    ),
    content = {
        PropertyPanel(editorContext = editorContext, block = block)
    },
)
// highlight-android-open-custom-panel

// highlight-android-panel-state
@Composable
fun PropertyPanel(
    editorContext: EditorContext,
    block: DesignBlock,
) {
    val engine = editorContext.engine
    var name by remember(block) {
        mutableStateOf(runCatching { engine.block.getName(block) }.getOrDefault(""))
    }
    var opacity by remember(block) {
        mutableStateOf(runCatching { engine.block.getOpacity(block) }.getOrDefault(1F))
    }
// highlight-android-panel-state

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text("Block properties")
        Spacer(modifier = Modifier.height(16.dp))
        // highlight-android-panel-content
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("Opacity")
        Slider(
            value = opacity,
            onValueChange = { opacity = it },
            valueRange = 0F..1F,
        )
        // highlight-android-panel-content
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            modifier = Modifier.align(Alignment.End),
            onClick = {
                // highlight-android-apply
                engine.block.setName(block, name = name)
                engine.block.setOpacity(block = block, value = opacity)
                editorContext.eventHandler.send(EditorEvent.Sheet.Close(animate = true))
                // highlight-android-apply
            },
        ) {
            Text("Done")
        }
    }
}
