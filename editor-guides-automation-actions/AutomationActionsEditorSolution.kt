import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.editor.Editor
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.remember
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.engine.DesignBlockType
import ly.img.engine.MimeType
import ly.img.engine.SizeMode
import java.io.File
import java.nio.ByteBuffer
import java.util.UUID

// highlight-actions-state-class
data class AutomationActionsState(
    val isExporting: Boolean = false,
    val exportedFileName: String? = null,
    val errorMessage: String? = null,
)
// highlight-actions-state-class

// highlight-actions-custom-events
object ShowExportProgress : EditorEvent

data class ExportFinished(
    val fileName: String,
) : EditorEvent

data class ExportFailed(
    val message: String,
) : EditorEvent
// highlight-actions-custom-events

@Composable
fun AutomationActionsEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-actions-state
    var state by remember { mutableStateOf(AutomationActionsState()) }
    // highlight-actions-state
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                // highlight-actions-onCreate
                onCreate = {
                    if (editorContext.engine.scene.get() == null) {
                        val scene = editorContext.engine.scene.create()
                        val page = editorContext.engine.block.create(DesignBlockType.Page)
                        editorContext.engine.block.setWidth(block = page, value = 1080F)
                        editorContext.engine.block.setHeight(block = page, value = 1080F)
                        editorContext.engine.block.appendChild(parent = scene, child = page)

                        val title = editorContext.engine.block.create(DesignBlockType.Text)
                        editorContext.engine.block.setWidthMode(title, mode = SizeMode.AUTO)
                        editorContext.engine.block.setHeightMode(title, mode = SizeMode.AUTO)
                        editorContext.engine.block.replaceText(title, text = "Quarterly Report")
                        editorContext.engine.block.appendChild(parent = page, child = title)

                        editorContext.engine.scene.zoomToBlock(page)
                    }
                }
                // highlight-actions-onCreate
                // highlight-actions-onExport
                onExport = {
                    editorContext.eventHandler.send(ShowExportProgress)
                    runCatching {
                        val buffer = editorContext.engine.block.export(
                            block = requireNotNull(editorContext.engine.scene.get()),
                            mimeType = MimeType.PDF,
                        )
                        writeToTempFile(
                            byteBuffer = buffer,
                            directory = { editorContext.activity.cacheDir },
                        )
                    }.onSuccess { file ->
                        editorContext.eventHandler.send(ExportFinished(file.name))
                    }.onFailure { throwable ->
                        editorContext.eventHandler.send(
                            ExportFailed(throwable.message ?: throwable.toString()),
                        )
                    }
                }
                // highlight-actions-onExport
                // highlight-actions-onEvent
                onEvent = { event ->
                    when (event) {
                        is EditorEvent.Export.Start -> {
                            state = state.copy(exportedFileName = null, errorMessage = null)
                        }

                        is ShowExportProgress -> {
                            state = state.copy(
                                isExporting = true,
                                exportedFileName = null,
                                errorMessage = null,
                            )
                        }

                        is ExportFinished -> {
                            state = state.copy(
                                isExporting = false,
                                exportedFileName = event.fileName,
                            )
                        }

                        is ExportFailed -> {
                            state = state.copy(
                                isExporting = false,
                                errorMessage = event.message,
                            )
                        }
                    }
                }
                // highlight-actions-onEvent
                // highlight-actions-overlay
                overlay = {
                    EditorComponent.remember {
                        decoration = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                // highlight-actions-trigger-export
                                Button(
                                    modifier =
                                        Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(24.dp),
                                    enabled = !state.isExporting,
                                    onClick = {
                                        editorContext.eventHandler.send(EditorEvent.Export.Start())
                                    },
                                ) {
                                    Text("Export Document")
                                }
                                // highlight-actions-trigger-export
                            }

                            if (state.isExporting) {
                                Dialog(
                                    onDismissRequest = {},
                                    properties =
                                        DialogProperties(
                                            dismissOnBackPress = false,
                                            dismissOnClickOutside = false,
                                        ),
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            state.exportedFileName?.let { fileName ->
                                AlertDialog(
                                    onDismissRequest = {
                                        state = state.copy(exportedFileName = null)
                                    },
                                    title = {
                                        Text("Automation complete")
                                    },
                                    text = {
                                        Text("Created $fileName in the app cache directory.")
                                    },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                state = state.copy(exportedFileName = null)
                                            },
                                        ) {
                                            Text("OK")
                                        }
                                    },
                                )
                            }

                            state.errorMessage?.let { errorMessage ->
                                AlertDialog(
                                    onDismissRequest = {
                                        state = state.copy(errorMessage = null)
                                    },
                                    title = {
                                        Text("Automation failed")
                                    },
                                    text = {
                                        Text(errorMessage)
                                    },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                state = state.copy(errorMessage = null)
                                            },
                                        ) {
                                            Text("OK")
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
                // highlight-actions-overlay
            }
        },
        onClose = onClose,
    )
}

// highlight-actions-write-to-temp-file
private suspend fun writeToTempFile(
    byteBuffer: ByteBuffer,
    directory: () -> File,
    mimeType: MimeType = MimeType.PDF,
): File = withContext(Dispatchers.IO) {
    val extension = mimeType.key.substringAfterLast('/')
    File
        .createTempFile(UUID.randomUUID().toString(), ".$extension", directory())
        .apply {
            outputStream().use { it.channel.write(byteBuffer) }
        }
}
// highlight-actions-write-to-temp-file
