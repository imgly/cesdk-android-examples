import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ly.img.editor.DesignEditor
import ly.img.editor.EditorDefaults
import ly.img.editor.EngineConfiguration
import ly.img.editor.HideLoading
import ly.img.editor.ShareFileEvent
import ly.img.editor.ShowCloseConfirmationDialogEvent
import ly.img.editor.ShowErrorDialogEvent
import ly.img.editor.ShowLoading
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.library.data.TextAssetSource
import ly.img.editor.core.library.data.TypefaceProvider
import ly.img.engine.MimeType
import ly.img.engine.addDefaultAssetSources
import ly.img.engine.addDemoAssetSources

// Add this composable to your NavHost
@Composable
fun CallbacksEditorSolution(navController: NavHostController) {
    val engineConfiguration =
        EngineConfiguration.remember(
            license = "<your license here>",
            // highlight-configuration-onCreate
            onCreate = {
                // Note that lambda is copied from EditorDefaults.onCreate
                coroutineScope {
                    // In case of process recovery, engine automatically recovers the scene that is why we need to check
                    if (editorContext.engine.scene.get() == null) {
                        editorContext.engine.scene.load(EngineConfiguration.defaultDesignSceneUri)
                    }
                    launch {
                        val baseUri = Uri.parse("https://cdn.img.ly/assets/v3")
                        editorContext.engine.addDefaultAssetSources(baseUri = baseUri)
                        val defaultTypeface = TypefaceProvider().provideTypeface(editorContext.engine, "Roboto")
                        requireNotNull(defaultTypeface)
                        editorContext.engine.asset.addSource(TextAssetSource(editorContext.engine, defaultTypeface))
                    }
                    launch {
                        editorContext.engine.addDemoAssetSources(
                            sceneMode = editorContext.engine.scene.getMode(),
                            withUploadAssetSources = true,
                            baseUri = Uri.parse("https://cdn.img.ly/assets/demo/v2"),
                        )
                    }
                    coroutineContext[Job]?.invokeOnCompletion {
                        editorContext.eventHandler.send(HideLoading)
                    }
                }
            },
            // highlight-configuration-onCreate
            // highlight-configuration-onExport
            onExport = {
                EditorDefaults.run {
                    editorContext.eventHandler.send(ShowLoading)
                    val blob =
                        editorContext.engine.block.export(
                            block = requireNotNull(editorContext.engine.scene.get()),
                            mimeType = MimeType.PDF,
                        ) {
                            scene.getPages().forEach {
                                block.setScopeEnabled(it, key = "layer/visibility", enabled = true)
                                block.setVisible(it, visible = true)
                            }
                        }
                    val tempFile = writeToTempFile(blob)
                    editorContext.eventHandler.send(HideLoading)
                    editorContext.eventHandler.send(ShareFileEvent(tempFile, MimeType.PDF.key))
                }
            },
            // highlight-configuration-onExport
            // highlight-configuration-onUpload
            onUpload = { assetDefinition, _ ->
                val meta = assetDefinition.meta ?: return@remember assetDefinition
                val sourceUri = Uri.parse(meta["uri"])
                val uploadedUri = sourceUri // todo upload the asset here and return remote uri
                val newMeta =
                    meta +
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
