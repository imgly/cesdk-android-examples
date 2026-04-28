import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.editor.BasicConfigurationBuilder
import ly.img.editor.Editor
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.NavigationBar
import ly.img.editor.core.component.remember
import ly.img.editor.core.component.rememberCloseEditor
import ly.img.editor.core.component.rememberExport
import ly.img.editor.core.component.rememberSystemCamera
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.engine.DesignBlockType
import ly.img.engine.MimeType
import java.io.File
import java.io.IOException
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
fun CallbacksEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-declare-state
    var state by remember { mutableStateOf(State()) }
    // highlight-declare-state
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember(::BasicConfigurationBuilder) {
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
                        // Add ImageUploads asset source to demonstrate onUpload
                        editorContext.engine.asset.addLocalSource(
                            sourceId = AssetSourceType.ImageUploads.sourceId,
                            supportedMimeTypes = listOf(AssetSourceType.ImageUploads.mimeTypeFilter),
                        )
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
                        Toast.makeText(editorContext.activity, "Exported to $file!", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(editorContext.activity, "onUpload invoked!", Toast.LENGTH_SHORT).show()
                    assetDefinition.copy(meta = newMeta)
                }
                // highlight-configuration-onUpload
                // highlight-configuration-onClose
                this.onClose = {
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
                        decoration = {
                            // Capture system back button tap
                            BackHandler(true) {
                                editorContext.eventHandler.send(EditorEvent.OnClose())
                            }
                            if (state.isLoading) {
                                Loading()
                            }
                            if (state.isCloseConfirmationDialogVisible) {
                                CloseConfirmationDialog(
                                    onDismissRequest = {
                                        state = state.copy(isCloseConfirmationDialogVisible = false)
                                    },
                                )
                            }
                            val error = state.error
                            when {
                                error is IOException -> NoInternetDialog()
                                error != null -> ErrorDialog(throwable = error)
                            }
                        }
                    }
                }
                dock = {
                    Dock.remember {
                        listBuilder = {
                            Dock.ListBuilder.remember {
                                add { Dock.Button.rememberSystemCamera() }
                            }
                        }
                        horizontalArrangement = { Arrangement.Center }
                    }
                }
                navigationBar = {
                    NavigationBar.remember {
                        listBuilder = {
                            NavigationBar.ListBuilder.remember {
                                aligned(alignment = Alignment.Start) {
                                    add { NavigationBar.Button.rememberCloseEditor() }
                                }
                                aligned(alignment = Alignment.End) {
                                    add { NavigationBar.Button.rememberExport() }
                                }
                            }
                        }
                    }
                }
            }
        },
        onClose = onClose,
    )
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
