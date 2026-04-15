import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.editor.Editor
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.remember
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.engine.DesignBlockType
import ly.img.engine.MimeType
import java.io.File
import java.nio.ByteBuffer
import java.util.UUID

// highlight-declare-state-class
data class State(
    val isLoading: Boolean = false,
    val isCloseConfirmationDialogVisible: Boolean = false,
    val error: Throwable? = null,
)
// highlight-declare-state-class

// Add this composable to your NavHost
@Composable
fun CallbacksEditorSolution(navController: NavHostController) {
    // highlight-declare-state
    var state by remember { mutableStateOf(State()) }
    // highlight-declare-state
    Editor(
        license = null, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                // highlight-configuration-onCreate
                onCreate = {
                    state = state.copy(isLoading = true)
                    try {
                        val scene = editorContext.engine.scene.create()
                        val page = editorContext.engine.block.create(DesignBlockType.Page)
                        editorContext.engine.block.setWidth(block = page, value = 1080F)
                        editorContext.engine.block.setHeight(block = page, value = 1080F)
                        editorContext.engine.block.appendChild(parent = scene, child = page)

                        editorContext.engine.editor.setSettingEnum(keypath = "touch/pinchAction", value = "Scale")
                    } finally {
                        state = state.copy(isLoading = false)
                    }
                }
                // highlight-configuration-onCreate
                // highlight-configuration-onLoaded
                onLoaded = {
                    coroutineScope {
                        launch {
                            editorContext.engine.editor
                                .onHistoryUpdated()
                                .collect { Toast.makeText(editorContext.activity, "History is updated!", Toast.LENGTH_SHORT).show() }
                        }
                        launch {
                            editorContext.engine.editor
                                .onStateChanged()
                                .map { editorContext.engine.editor.getEditMode() }
                                .distinctUntilChanged()
                                .collect {
                                    Toast.makeText(editorContext.activity, "Edit mode is updated to $it!", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
                // highlight-configuration-onLoaded
                // highlight-configuration-onExport
                onExport = {
                    val engine = editorContext.engine
                    state = state.copy(isLoading = true)
                    runCatching {
                        val buffer = engine.block.export(
                            block = requireNotNull(engine.scene.get()),
                            mimeType = MimeType.PDF,
                            onPreExport = {
                                scene.getPages().forEach { page ->
                                    block.setScopeEnabled(page, key = "layer/visibility", enabled = true)
                                    block.setVisible(page, visible = true)
                                }
                            },
                        )
                        writeToTempFile(buffer, MimeType.PDF)
                    }.onSuccess { file ->
                        state = state.copy(isLoading = false)
                        // Do something with the file
                    }.onFailure {
                        state = state.copy(isLoading = false, error = it)
                    }
                }
                // highlight-configuration-onExport
                // highlight-configuration-onUpload
                onUpload = onUpload@{ assetDefinition, _ ->
                    val meta = assetDefinition.meta ?: return@onUpload assetDefinition
                    val sourceUri = meta["uri"]?.toUri()
                    val uploadedUri = sourceUri // todo upload the asset here and return remote uri
                    val newMeta = meta +
                        listOf(
                            "uri" to uploadedUri.toString(),
                            "thumbUri" to uploadedUri.toString(),
                        )
                    assetDefinition.copy(meta = newMeta)
                }
                // highlight-configuration-onUpload
                // highlight-configuration-onClose
                onClose = {
                    if (editorContext.engine.editor.canUndo()) {
                        state = state.copy(isCloseConfirmationDialogVisible = true)
                    } else {
                        editorContext.eventHandler.send(EditorEvent.CloseEditor())
                    }
                }
                // highlight-configuration-onClose
                // highlight-configuration-onError
                onError = { error ->
                    state = state.copy(error = error)
                }
                // highlight-configuration-onError
                overlay = {
                    EditorComponent.remember {
                        // Render loading, export state, error dialog and close confirmation dialog here.
                    }
                }
            }
        },
    ) {
        // You can set result here
        navController.popBackStack()
    }
}

// highlight-write-to-temp-file
private suspend fun writeToTempFile(
    byteBuffer: ByteBuffer,
    mimeType: MimeType = MimeType.PDF,
): File = withContext(Dispatchers.IO) {
    val extension = mimeType.key.split("/").last()
    File
        .createTempFile(UUID.randomUUID().toString(), ".$extension")
        .apply {
            outputStream().channel.write(byteBuffer)
        }
}
// highlight-write-to-temp-file
