import android.app.Activity
import android.view.SurfaceView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ly.img.engine.Color
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.FillType
import ly.img.engine.MimeType
import ly.img.engine.ShapeType
import java.nio.ByteBuffer

@Composable
fun BuildYourOwnUIScreen(
    license: String,
    onClose: () -> Unit,
) {
    // highlight-android-state
    var page by remember { mutableStateOf<DesignBlock?>(null) }
    var selectedBlock by remember { mutableStateOf<DesignBlock?>(null) }
    var selectedType by remember { mutableStateOf("No block selected") }
    var positionX by remember { mutableStateOf(0F) }
    var positionY by remember { mutableStateOf(0F) }
    var width by remember { mutableStateOf(0F) }
    var height by remember { mutableStateOf(0F) }
    var rotationDegrees by remember { mutableStateOf(0F) }
    var exportStatus by remember { mutableStateOf("Ready to export") }

    fun applySelectionState(selection: BuildYourOwnUISelection) {
        selectedBlock = selection.block
        selectedType = selection.type ?: "No block selected"
        positionX = selection.positionX
        positionY = selection.positionY
        width = selection.width
        height = selection.height
        rotationDegrees = selection.rotationDegrees
    }
    // highlight-android-state

    val activity = LocalContext.current as Activity
    val savedStateRegistryOwner = LocalSavedStateRegistryOwner.current
    val coroutineScope = rememberCoroutineScope()

    // highlight-android-engine-instance
    val engine = remember(activity.application) {
        Engine.init(activity.application)
        Engine.getInstance(id = "ly.img.engine.examples.build-your-own-ui")
    }
    val surfaceView = remember { SurfaceView(activity) }
    // highlight-android-engine-instance

    BackHandler(onBack = onClose)

    Column(modifier = Modifier.fillMaxSize()) {
        // highlight-android-render-target
        Box(modifier = Modifier.weight(1F)) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .semantics { contentDescription = "Custom CE.SDK canvas" },
                factory = { surfaceView },
            )
        }
        // highlight-android-render-target
        Divider()
        BuildYourOwnUIControls(
            selectedType = selectedType,
            positionX = positionX,
            positionY = positionY,
            width = width,
            height = height,
            rotationDegrees = rotationDegrees,
            exportStatus = exportStatus,
            onPositionXChange = { positionX = it },
            onPositionYChange = { positionY = it },
            onWidthChange = { width = it },
            onHeightChange = { height = it },
            onRotationChange = { rotationDegrees = it },
            onPositionXFinished = {
                selectedBlock?.let { engine.block.setPositionX(block = it, value = positionX) }
            },
            onPositionYFinished = {
                selectedBlock?.let { engine.block.setPositionY(block = it, value = positionY) }
            },
            onWidthFinished = {
                selectedBlock?.let { engine.block.setWidth(block = it, value = width) }
            },
            onHeightFinished = {
                selectedBlock?.let { engine.block.setHeight(block = it, value = height) }
            },
            onRotationFinished = {
                selectedBlock?.let {
                    engine.block.setRotation(
                        block = it,
                        radians = Math.toRadians(rotationDegrees.toDouble()).toFloat(),
                    )
                }
            },
            onAddText = {
                page?.let {
                    addTextBlock(engine = engine, page = it)
                    applySelectionState(readSelection(engine))
                }
            },
            onAddShape = {
                page?.let {
                    addShapeBlock(engine = engine, page = it)
                    applySelectionState(readSelection(engine))
                }
            },
            onExport = {
                val pageToExport = page
                if (pageToExport != null) {
                    coroutineScope.launch {
                        val png = exportPagePng(engine = engine, page = pageToExport)
                        exportStatus = "Exported ${png.remaining()} PNG bytes"
                    }
                }
            },
        )
    }

    LaunchedEffect(engine, surfaceView, savedStateRegistryOwner) {
        // highlight-android-engine-start
        engine.start(
            license = license.takeIf { it.isNotBlank() },
            userId = "build-your-own-ui",
            savedStateRegistryOwner = savedStateRegistryOwner,
        )
        engine.bindSurfaceView(surfaceView)
        // highlight-android-engine-start

        val initialPage = findOrCreateInitialPage(engine)
        page = initialPage
        engine.scene.zoomToBlock(
            block = initialPage,
            paddingLeft = 40F,
            paddingTop = 40F,
            paddingRight = 40F,
            paddingBottom = 40F,
        )
        collectSelectionEvents(engine = engine, onSelectionChanged = ::applySelectionState)
    }

    // highlight-android-engine-cleanup
    DisposableEffect(activity, engine) {
        onDispose {
            if (engine.isEngineRunning()) {
                engine.unbind()
                if (!activity.isChangingConfigurations) {
                    engine.stop()
                }
            }
        }
    }
    // highlight-android-engine-cleanup
}

// highlight-android-create-initial-content
private fun findOrCreateInitialPage(engine: Engine): DesignBlock {
    val existingScene = engine.scene.get()
    if (existingScene != null) {
        val existingPage = engine.block.findByType(DesignBlockType.Page).firstOrNull {
            engine.block.isValid(it)
        }
        if (existingPage != null) return existingPage
    }

    return createInitialContent(engine = engine, scene = existingScene)
}

private fun createInitialContent(
    engine: Engine,
    scene: DesignBlock?,
): DesignBlock {
    val targetScene = scene ?: engine.scene.create()
    val page = engine.block.create(DesignBlockType.Page)
    engine.block.setWidth(block = page, value = 800F)
    engine.block.setHeight(block = page, value = 600F)
    engine.block.appendChild(parent = targetScene, child = page)

    val textBlock = engine.block.create(DesignBlockType.Text)
    engine.block.setString(block = textBlock, property = "text/text", value = "Tap to Edit")
    engine.block.setPositionX(block = textBlock, value = 80F)
    engine.block.setPositionY(block = textBlock, value = 80F)
    engine.block.setWidth(block = textBlock, value = 300F)
    engine.block.setHeight(block = textBlock, value = 80F)
    engine.block.appendChild(parent = page, child = textBlock)

    val shapeBlock = engine.block.create(DesignBlockType.Graphic)
    val shape = engine.block.createShape(type = ShapeType.Rect)
    val fill = engine.block.createFill(fillType = FillType.Color)
    engine.block.setShape(block = shapeBlock, shape = shape)
    engine.block.setFill(block = shapeBlock, fill = fill)
    engine.block.setFillSolidColor(
        block = shapeBlock,
        color = Color.fromRGBA(r = 0.2F, g = 0.6F, b = 0.9F, a = 1F),
    )
    engine.block.setPositionX(block = shapeBlock, value = 450F)
    engine.block.setPositionY(block = shapeBlock, value = 200F)
    engine.block.setWidth(block = shapeBlock, value = 150F)
    engine.block.setHeight(block = shapeBlock, value = 150F)
    engine.block.appendChild(parent = page, child = shapeBlock)

    engine.block.select(textBlock)
    return page
}
// highlight-android-create-initial-content

// highlight-android-handle-events
private data class BuildYourOwnUISelection(
    val block: DesignBlock?,
    val type: String?,
    val positionX: Float,
    val positionY: Float,
    val width: Float,
    val height: Float,
    val rotationDegrees: Float,
)

private suspend fun collectSelectionEvents(
    engine: Engine,
    onSelectionChanged: (BuildYourOwnUISelection) -> Unit,
) {
    onSelectionChanged(readSelection(engine))
    engine.event.subscribe().collect {
        onSelectionChanged(readSelection(engine))
    }
}

private fun readSelection(engine: Engine): BuildYourOwnUISelection {
    val selected = engine.block.findAllSelected().firstOrNull()
    if (selected == null || !engine.block.isValid(selected)) {
        return BuildYourOwnUISelection(
            block = null,
            type = null,
            positionX = 0F,
            positionY = 0F,
            width = 0F,
            height = 0F,
            rotationDegrees = 0F,
        )
    }

    return BuildYourOwnUISelection(
        block = selected,
        type = engine.block.getType(selected),
        positionX = engine.block.getPositionX(selected),
        positionY = engine.block.getPositionY(selected),
        width = engine.block.getWidth(selected),
        height = engine.block.getHeight(selected),
        rotationDegrees = Math.toDegrees(engine.block.getRotation(selected).toDouble()).toFloat(),
    )
}
// highlight-android-handle-events

// highlight-android-ui-controls
@Composable
private fun BuildYourOwnUIControls(
    selectedType: String,
    positionX: Float,
    positionY: Float,
    width: Float,
    height: Float,
    rotationDegrees: Float,
    exportStatus: String,
    onPositionXChange: (Float) -> Unit,
    onPositionYChange: (Float) -> Unit,
    onWidthChange: (Float) -> Unit,
    onHeightChange: (Float) -> Unit,
    onRotationChange: (Float) -> Unit,
    onPositionXFinished: () -> Unit,
    onPositionYFinished: () -> Unit,
    onWidthFinished: () -> Unit,
    onHeightFinished: () -> Unit,
    onRotationFinished: () -> Unit,
    onAddText: () -> Unit,
    onAddShape: () -> Unit,
    onExport: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onAddText) {
                Text("Add Text")
            }
            Button(onClick = onAddShape) {
                Text("Add Shape")
            }
            OutlinedButton(onClick = onExport) {
                Text("Export PNG")
            }
        }

        Text(text = "Selected: $selectedType", style = MaterialTheme.typography.labelMedium)
        Text(text = exportStatus, style = MaterialTheme.typography.bodySmall)

        PropertyPanel(
            positionX = positionX,
            positionY = positionY,
            width = width,
            height = height,
            rotationDegrees = rotationDegrees,
            onPositionXChange = onPositionXChange,
            onPositionYChange = onPositionYChange,
            onWidthChange = onWidthChange,
            onHeightChange = onHeightChange,
            onRotationChange = onRotationChange,
            onPositionXFinished = onPositionXFinished,
            onPositionYFinished = onPositionYFinished,
            onWidthFinished = onWidthFinished,
            onHeightFinished = onHeightFinished,
            onRotationFinished = onRotationFinished,
        )
    }
}
// highlight-android-ui-controls

// highlight-android-add-blocks
private fun addTextBlock(
    engine: Engine,
    page: DesignBlock,
) {
    val textBlock = engine.block.create(DesignBlockType.Text)
    engine.block.setString(block = textBlock, property = "text/text", value = "Custom headline")
    engine.block.setPositionX(block = textBlock, value = 80F)
    engine.block.setPositionY(block = textBlock, value = 80F)
    engine.block.setWidth(block = textBlock, value = 320F)
    engine.block.setHeight(block = textBlock, value = 100F)
    engine.block.appendChild(parent = page, child = textBlock)
    engine.block.select(textBlock)
}

private fun addShapeBlock(
    engine: Engine,
    page: DesignBlock,
) {
    val shapeBlock = engine.block.create(DesignBlockType.Graphic)
    val shape = engine.block.createShape(type = ShapeType.Rect)
    val fill = engine.block.createFill(fillType = FillType.Color)
    engine.block.setShape(block = shapeBlock, shape = shape)
    engine.block.setFill(block = shapeBlock, fill = fill)
    engine.block.setFillSolidColor(
        block = shapeBlock,
        color = Color.fromRGBA(r = 0.09F, g = 0.7F, b = 0.55F, a = 1F),
    )
    engine.block.setPositionX(block = shapeBlock, value = 120F)
    engine.block.setPositionY(block = shapeBlock, value = 220F)
    engine.block.setWidth(block = shapeBlock, value = 180F)
    engine.block.setHeight(block = shapeBlock, value = 180F)
    engine.block.appendChild(parent = page, child = shapeBlock)
    engine.block.select(shapeBlock)
}
// highlight-android-add-blocks

// highlight-android-property-panel
@Composable
private fun PropertyPanel(
    positionX: Float,
    positionY: Float,
    width: Float,
    height: Float,
    rotationDegrees: Float,
    onPositionXChange: (Float) -> Unit,
    onPositionYChange: (Float) -> Unit,
    onWidthChange: (Float) -> Unit,
    onHeightChange: (Float) -> Unit,
    onRotationChange: (Float) -> Unit,
    onPositionXFinished: () -> Unit,
    onPositionYFinished: () -> Unit,
    onWidthFinished: () -> Unit,
    onHeightFinished: () -> Unit,
    onRotationFinished: () -> Unit,
) {
    TransformSlider("X", positionX, 0F..800F, onPositionXChange, onPositionXFinished)
    TransformSlider("Y", positionY, 0F..600F, onPositionYChange, onPositionYFinished)
    TransformSlider("W", width, 1F..800F, onWidthChange, onWidthFinished)
    TransformSlider("H", height, 1F..600F, onHeightChange, onHeightFinished)
    TransformSlider("Rot", rotationDegrees, -180F..180F, onRotationChange, onRotationFinished)
}

@Composable
private fun TransformSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.width(40.dp),
            text = label,
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Slider(
            modifier = Modifier.weight(1F),
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = range,
        )
    }
}
// highlight-android-property-panel

// highlight-android-export
private suspend fun exportPagePng(
    engine: Engine,
    page: DesignBlock,
): ByteBuffer = engine.block.export(block = page, mimeType = MimeType.PNG)
// highlight-android-export
