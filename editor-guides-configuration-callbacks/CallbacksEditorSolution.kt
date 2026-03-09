import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.editor.DesignEditor
import ly.img.editor.DismissVideoExportEvent
import ly.img.editor.EditorDefaults
import ly.img.editor.EngineConfiguration
import ly.img.editor.HideLoading
import ly.img.editor.OnSceneLoaded
import ly.img.editor.ShareUriEvent
import ly.img.editor.ShowCloseConfirmationDialogEvent
import ly.img.editor.ShowErrorDialogEvent
import ly.img.editor.ShowLoading
import ly.img.editor.ShowVideoExportErrorEvent
import ly.img.editor.ShowVideoExportProgressEvent
import ly.img.editor.ShowVideoExportSuccessEvent
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.library.data.TextAssetSource
import ly.img.editor.core.library.data.TypefaceProvider
import ly.img.engine.MimeType
import ly.img.engine.SceneMode
import ly.img.engine.addDefaultAssetSources
import ly.img.engine.addDemoAssetSources
import kotlin.coroutines.cancellation.CancellationException

// Add this composable to your NavHost
@Composable
fun CallbacksEditorSolution(navController: NavHostController) {
    val engineConfiguration = EngineConfiguration.remember(
        license = "<your license here>", // pass null or empty for evaluation mode with watermark
        // highlight-configuration-onCreate
        onCreate = {
            // Note that lambda is copied from EditorDefaults.onCreate
            coroutineScope {
                // In case of process recovery, engine automatically recovers the scene that is why we need to check
                if (editorContext.engine.scene.get() == null) {
                    editorContext.engine.scene.load(EngineConfiguration.defaultDesignSceneUri)
                }
                launch {
                    editorContext.engine.addDefaultAssetSources()
                    val defaultTypeface = TypefaceProvider().provideTypeface(editorContext.engine, "Roboto")
                    requireNotNull(defaultTypeface)
                    editorContext.engine.asset.addSource(TextAssetSource(editorContext.engine, defaultTypeface))
                }
                launch {
                    editorContext.engine.addDemoAssetSources(
                        sceneMode = editorContext.engine.scene.getMode(),
                        withUploadAssetSources = true,
                    )
                }
            }
            editorContext.eventHandler.send(HideLoading)
            editorContext.eventHandler.send(OnSceneLoaded())
        },
        // highlight-configuration-onCreate
        // highlight-configuration-onLoaded
        onLoaded = {
            editorContext.engine.editor.setSettingEnum("touch/pinchAction", "Scale")
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
                        .collect { Toast.makeText(editorContext.activity, "Edit mode is updated to $it!", Toast.LENGTH_SHORT).show() }
                }
            }
        },
        // highlight-configuration-onLoaded
        // highlight-configuration-onExport
        onExport = {
            val engine = editorContext.engine
            val eventHandler = editorContext.eventHandler
            val context = engine.applicationContext
            EditorDefaults.run {
                if (engine.scene.getMode() == SceneMode.VIDEO) {
                    val page = engine.scene.getCurrentPage() ?: engine.scene.getPages()[0]
                    var exportProgress = 0f
                    eventHandler.send(ShowVideoExportProgressEvent(exportProgress))
                    runCatching {
                        val buffer = engine.block.exportVideo(
                            block = page,
                            timeOffset = 0.0,
                            duration = engine.block.getDuration(page),
                            mimeType = MimeType.MP4,
                            progressCallback = { progress ->
                                val newProgress = progress.encodedFrames.toFloat() / progress.totalFrames
                                if (newProgress >= exportProgress + 0.01f) {
                                    exportProgress = newProgress
                                    eventHandler.send(ShowVideoExportProgressEvent(exportProgress))
                                }
                            },
                        )
                        val file = writeToTempFile(buffer, MimeType.MP4)
                        withContext(Dispatchers.IO) {
                            FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.ly.img.editor.fileprovider",
                                file,
                            )
                        }
                    }.onSuccess { uri ->
                        eventHandler.send(ShowVideoExportSuccessEvent(uri, MimeType.MP4.key))
                    }.onFailure {
                        if (it is CancellationException) {
                            eventHandler.send(DismissVideoExportEvent)
                        } else {
                            eventHandler.send(ShowVideoExportErrorEvent)
                        }
                    }
                } else {
                    eventHandler.send(ShowLoading)
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
                        val file = writeToTempFile(buffer, MimeType.PDF)
                        withContext(Dispatchers.IO) {
                            FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.ly.img.editor.fileprovider",
                                file,
                            )
                        }
                    }.onSuccess { uri ->
                        eventHandler.send(HideLoading)
                        eventHandler.send(ShareUriEvent(uri, MimeType.PDF.key))
                    }.onFailure {
                        eventHandler.send(HideLoading)
                        eventHandler.send(ShowErrorDialogEvent(error = it))
                    }
                }
            }
        },
        // highlight-configuration-onExport
        // highlight-configuration-onUpload
        onUpload = { assetDefinition, _ ->
            val meta = assetDefinition.meta ?: return@remember assetDefinition
            val sourceUri = Uri.parse(meta["uri"])
            val uploadedUri = sourceUri // todo upload the asset here and return remote uri
            val newMeta = meta +
                listOf(
                    "uri" to uploadedUri.toString(),
                    "thumbUri" to uploadedUri.toString(),
                )
            assetDefinition.copy(meta = newMeta)
        },
        // highlight-configuration-onUpload
        // highlight-configuration-onClose
        onClose = { hasUnsavedChanges ->
            if (hasUnsavedChanges) {
                editorContext.eventHandler.send(ShowCloseConfirmationDialogEvent)
            } else {
                editorContext.eventHandler.send(EditorEvent.CloseEditor())
            }
        },
        // highlight-configuration-onClose
        // highlight-configuration-onError
        onError = { error ->
            editorContext.eventHandler.send(ShowErrorDialogEvent(error))
        },
        // highlight-configuration-onError
    )
    DesignEditor(engineConfiguration = engineConfiguration) {
        // You can set result here
        navController.popBackStack()
    }
}
