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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ly.img.editor.Editor
import ly.img.editor.core.component.EditorComponent
import ly.img.editor.core.component.remember
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.engine.AssetDefinition
import ly.img.engine.Engine
import ly.img.engine.MimeType
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException

// highlight-android-action-state
data class ActionsExportState(
    val isExporting: Boolean = false,
    val exportedFileName: String? = null,
    val errorMessage: String? = null,
)
// highlight-android-action-state

// highlight-android-custom-events
object ActionsExportStarted : EditorEvent

data class ActionsExportCompleted(
    val fileName: String,
) : EditorEvent

data class ActionsExportFailed(
    val message: String,
) : EditorEvent
// highlight-android-custom-events

@Composable
fun ActionsEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license,
        configuration = {
            EditorConfiguration.remember {
                // highlight-android-remember-state
                var exportState by editorContext.mutableStateOf(
                    key = "actions.export.state",
                    initial = ActionsExportState(),
                )
                // highlight-android-remember-state
                // highlight-android-handle-export
                onExport = {
                    editorContext.eventHandler.send(ActionsExportStarted)
                    try {
                        val file = exportCurrentSceneToCache(
                            engine = editorContext.engine,
                            directory = editorContext.activity.cacheDir,
                        )
                        editorContext.eventHandler.send(ActionsExportCompleted(file.name))
                    } catch (throwable: CancellationException) {
                        throw throwable
                    } catch (throwable: Throwable) {
                        editorContext.eventHandler.send(
                            ActionsExportFailed(throwable.message ?: throwable.toString()),
                        )
                    }
                }
                // highlight-android-handle-export
                // highlight-android-handle-events
                onEvent = { event ->
                    exportState = when (event) {
                        is ActionsExportStarted -> {
                            ActionsExportState(isExporting = true)
                        }

                        is ActionsExportCompleted -> {
                            ActionsExportState(exportedFileName = event.fileName)
                        }

                        is ActionsExportFailed -> {
                            ActionsExportState(errorMessage = event.message)
                        }

                        is EditorEvent.Export.Cancel -> {
                            ActionsExportState()
                        }

                        else -> exportState
                    }
                }
                // highlight-android-handle-events
                // highlight-android-handle-upload
                onUpload = { assetDefinition, uploadSource ->
                    uploadTransientResource(
                        assetDefinition = assetDefinition,
                        uploadSource = uploadSource,
                    )
                }
                // highlight-android-handle-upload
                overlay = {
                    EditorComponent.remember {
                        decoration = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                // highlight-android-trigger-export
                                Button(
                                    modifier =
                                        Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(24.dp),
                                    enabled = !exportState.isExporting,
                                    onClick = {
                                        editorContext.eventHandler.send(EditorEvent.Export.Start())
                                    },
                                ) {
                                    Text("Export")
                                }
                                // highlight-android-trigger-export
                            }

                            if (exportState.isExporting) {
                                AlertDialog(
                                    onDismissRequest = {},
                                    title = {
                                        Text("Exporting")
                                    },
                                    text = {
                                        CircularProgressIndicator()
                                    },
                                    confirmButton = {},
                                    dismissButton = {
                                        // highlight-android-cancel-export
                                        TextButton(
                                            onClick = {
                                                editorContext.eventHandler.send(EditorEvent.Export.Cancel())
                                            },
                                        ) {
                                            Text("Cancel")
                                        }
                                        // highlight-android-cancel-export
                                    },
                                    properties =
                                        DialogProperties(
                                            dismissOnBackPress = false,
                                            dismissOnClickOutside = false,
                                        ),
                                )
                            }

                            exportState.exportedFileName?.let { fileName ->
                                AlertDialog(
                                    onDismissRequest = {
                                        exportState = ActionsExportState()
                                    },
                                    title = {
                                        Text("Export complete")
                                    },
                                    text = {
                                        Text("Created $fileName in the app cache directory.")
                                    },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                exportState = ActionsExportState()
                                            },
                                        ) {
                                            Text("OK")
                                        }
                                    },
                                )
                            }

                            exportState.errorMessage?.let { errorMessage ->
                                AlertDialog(
                                    onDismissRequest = {
                                        exportState = ActionsExportState()
                                    },
                                    title = {
                                        Text("Export failed")
                                    },
                                    text = {
                                        Text(errorMessage)
                                    },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                exportState = ActionsExportState()
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
            }
        },
        onClose = onClose,
    )
}

// highlight-android-export-helper
private suspend fun exportCurrentSceneToCache(
    engine: Engine,
    directory: File,
): File {
    val scene = requireNotNull(engine.scene.get()) { "No scene loaded for export." }
    val buffer = engine.block.export(
        block = scene,
        mimeType = MimeType.PDF,
    )
    return writeToCacheFile(
        byteBuffer = buffer,
        directory = directory,
        mimeType = MimeType.PDF,
    )
}
// highlight-android-export-helper

// highlight-android-write-cache-file
private suspend fun writeToCacheFile(
    byteBuffer: ByteBuffer,
    directory: File,
    mimeType: MimeType,
): File = withContext(Dispatchers.IO) {
    val extension = when (mimeType) {
        MimeType.PNG -> "png"
        MimeType.JPEG -> "jpg"
        MimeType.TGA -> "tga"
        MimeType.SVG -> "svg"
        MimeType.MP4 -> "mp4"
        MimeType.BINARY -> "bin"
        MimeType.PDF -> "pdf"
    }
    val outputFile = File.createTempFile(UUID.randomUUID().toString(), ".$extension", directory)
    val sourceBuffer = byteBuffer.asReadOnlyBuffer()

    FileOutputStream(outputFile).channel.use { channel ->
        while (sourceBuffer.hasRemaining()) {
            channel.write(sourceBuffer)
        }
    }

    check(outputFile.length() > 0L) { "Exported file is empty." }
    outputFile
}
// highlight-android-write-cache-file

// highlight-android-upload-helper
private suspend fun uploadTransientResource(
    assetDefinition: AssetDefinition,
    uploadSource: UploadAssetSourceType,
): AssetDefinition {
    val meta = assetDefinition.meta ?: return assetDefinition
    val localUri = meta["uri"] ?: return assetDefinition
    val permanentUri = uploadToPermanentStorage(
        uri = localUri,
        sourceId = uploadSource.sourceId,
    )
    val permanentMeta = meta.toMutableMap()
    permanentMeta["uri"] = permanentUri
    meta["thumbUri"]?.let { thumbnailUri ->
        permanentMeta["thumbUri"] = if (thumbnailUri == localUri) {
            permanentUri
        } else {
            uploadToPermanentStorage(
                uri = thumbnailUri,
                sourceId = uploadSource.sourceId,
            )
        }
    }
    return assetDefinition.copy(
        meta = permanentMeta,
    )
}

private suspend fun uploadToPermanentStorage(
    uri: String,
    sourceId: String,
): String {
    check(sourceId.isNotBlank()) { "Upload source id is required." }
    // Replace this with your app's storage client and return its permanent URI.
    return uri
}
// highlight-android-upload-helper
