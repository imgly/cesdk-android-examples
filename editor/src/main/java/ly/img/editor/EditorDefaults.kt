package ly.img.editor

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.SizeF
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.editor.base.R
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.library.data.TextAssetSource
import ly.img.editor.core.library.data.TypefaceProvider
import ly.img.editor.core.ui.iconpack.Cloudalertoutline
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.WifiCancel
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.EngineException
import ly.img.engine.MimeType
import ly.img.engine.addDefaultAssetSources
import ly.img.engine.addDemoAssetSources
import java.io.File
import java.nio.ByteBuffer
import java.util.UUID

/**
 * A helper class that provides implementations of some of the properties in [EngineConfiguration] and [EditorConfiguration].
 */
object EditorDefaults {
    /**
     * A helper implementation of [EngineConfiguration.onCreate]. The implementation does the following:
     * 1. Checks for an existing scene. If one does not exist, it loads the [sceneUri] into the scene
     * 2. Adds [ly.img.editor.core.library.data.TextAssetSource], default and demo asset sources. Check [Engine.addDefaultAssetSources]
     * and [Engine.addDemoAssetSources] documentation for more details.
     *
     * @param engine the engine that is used in the editor.
     * @param sceneUri the Uri that is used to load the scene file into the scene.
     * @param eventHandler the object that can send [EditorEvent]s and close the editor.
     */
    suspend fun onCreate(
        engine: Engine,
        sceneUri: Uri,
        eventHandler: EditorEventHandler,
    ) = onCreateCommon(engine, eventHandler) {
        engine.scene.load(sceneUri)
    }

    /**
     * A helper implementation of [EngineConfiguration.onCreate]. The implementation does the following:
     * 1. Checks for an existing scene. If one does not exist, it creates a scene from the [imageUri] using [ly.img.engine.SceneApi.createFromImage] API.
     * 2. Adds [ly.img.editor.core.library.data.TextAssetSource], default and demo asset sources. Check [Engine.addDefaultAssetSources]
     * and [Engine.addDemoAssetSources] documentation for more details.
     *
     * @param engine the engine that is used in the editor.
     * @param imageUri the uri of the image that is used to create a scene with single page and image fill.
     * @param size the size that should be used to load the image. If null, original size of the image will be used.
     * @param eventHandler the object that can send [EditorEvent]s and close the editor.
     */
    suspend fun onCreateFromImage(
        engine: Engine,
        imageUri: Uri,
        eventHandler: EditorEventHandler,
        size: SizeF? = null,
    ) = onCreateCommon(engine, eventHandler) {
        engine.scene.createFromImage(imageUri)
        val graphicBlocks = engine.block.findByType(DesignBlockType.Graphic)
        require(graphicBlocks.size == 1) { "No image found." }
        val graphicBlock = graphicBlocks[0]
        val pages = engine.scene.getPages()
        require(pages.size == 1) { "No image found." }
        val page = pages[0]
        engine.block.setFill(page, engine.block.getFill(graphicBlock))
        engine.block.destroy(graphicBlock)
        size?.let {
            engine.block.setWidth(page, size.width)
            engine.block.setHeight(page, size.height)
        }
    }

    private suspend fun onCreateCommon(
        engine: Engine,
        eventHandler: EditorEventHandler,
        createScene: suspend () -> Unit,
    ) = coroutineScope {
        if (engine.scene.get() == null) {
            createScene()
        }
        launch {
            val baseUri = Uri.parse("https://cdn.img.ly/assets/v3")
            engine.addDefaultAssetSources(baseUri = baseUri)
            val defaultTypeface = TypefaceProvider().provideTypeface(engine, "Roboto")
            requireNotNull(defaultTypeface)
            engine.asset.addSource(TextAssetSource(engine, defaultTypeface))
        }
        launch {
            engine.addDemoAssetSources(
                sceneMode = engine.scene.getMode(),
                withUploadAssetSources = true,
                baseUri = Uri.parse("https://cdn.img.ly/assets/demo/v2"),
            )
        }
        coroutineContext[Job]?.invokeOnCompletion {
            eventHandler.send(HideLoading)
        }
    }

    /**
     * A helper function that writes [byteBuffer] into a temporary file. This can be helpful in the [EngineConfiguration.onExport]
     * callback.
     *
     * @param byteBuffer the data that should be written in the temporary file.
     * @param mimeType the mime type of the file. Note that it is used to derive the extension of the newly created file.
     * @return a temporary file with the content of [byteBuffer]
     */
    suspend fun writeToTempFile(
        byteBuffer: ByteBuffer,
        mimeType: MimeType = MimeType.PDF,
    ): File =
        withContext(Dispatchers.IO) {
            val extension = mimeType.key.split("/").last()
            File
                .createTempFile(UUID.randomUUID().toString(), ".$extension")
                .apply {
                    outputStream().channel.write(byteBuffer)
                }
        }

    /**
     * A helper function that opens a system dialog to share the [file]. Note that you should declare a [FileProvider] in the
     * AndroidManifest.xml file of your application module with the following configuration:
     *      android:authorities="${applicationId}.fileprovider"
     *
     * @param activity the activity that is used to construct the uri of the [file] and launch the activity of the system dialog.
     * @param file the file that should be shared.
     * @param mimeType the mime type of the file that is used to open the system dialog.
     */
    fun shareFile(
        activity: Activity,
        file: File,
        mimeType: String,
    ) {
        val uri = FileProvider.getUriForFile(activity, "${activity.packageName}.fileprovider", file)
        shareUri(activity, uri, mimeType)
    }

    /**
     * A helper function that opens a system dialog to share the [uri].
     *
     * @param activity the activity that is used to launch the activity of the system dialog.
     * @param uri the uri that should be shared.
     * @param mimeType the mime type of the content of the uri that is used to open the system dialog.
     */
    fun shareUri(
        activity: Activity,
        uri: Uri,
        mimeType: String,
    ) {
        val shareIntent =
            Intent().apply {
                action = Intent.ACTION_SEND
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
            }
        activity.startActivity(Intent.createChooser(shareIntent, null))
    }

    /**
     * A helper function that handles the default events specified in Events.kt file. By default the function is invoked
     * by the default implementation of the [EditorConfiguration].
     *
     * @param activity the activity where the editor is displayed.
     * @param state the mutable state of the editor.
     * @param event the event that should be handled.
     */
    fun onEvent(
        activity: Activity,
        state: EditorUiState,
        event: EditorEvent,
    ): EditorUiState {
        return when (event) {
            is ShowLoading -> {
                state.copy(showLoading = true)
            }
            is HideLoading -> {
                state.copy(showLoading = false)
            }
            is ShowErrorDialogEvent -> {
                state.copy(error = event.error)
            }
            is ShowCloseConfirmationDialogEvent -> {
                state.copy(showCloseConfirmationDialog = true)
            }
            is DismissCloseConfirmationDialogEvent -> {
                state.copy(showCloseConfirmationDialog = false)
            }
            is ShareFileEvent -> {
                shareFile(
                    activity = activity,
                    file = event.file,
                    mimeType = event.mimeType,
                )
                state
            }
            else -> state
        }
    }

    /**
     * A helper composable function that handles the default [EditorUiState] of the editor and draws overlay components.
     * By default the composable function is invoked by the default implementation of the [EditorConfiguration].
     *
     * @param state the current state of the editor.
     * @param eventHandler the object that can send [EditorEvent]s and close the editor.
     */
    @Composable
    fun Overlay(
        state: EditorUiState,
        eventHandler: EditorEventHandler,
    ) {
        if (state.showLoading) {
            Loading()
        }
        if (state.error is EngineException) {
            ErrorDialog(
                engineException = state.error,
                eventHandler = eventHandler,
            )
        } else if (state.error != null) {
            NoInternetDialog(eventHandler = eventHandler)
        }
        if (state.showCloseConfirmationDialog) {
            CloseConfirmationDialog(eventHandler = eventHandler)
        }
    }

    /**
     * A helper composable function for displaying a loading overlay.
     */
    @Composable
    fun Loading() {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        ) {
            CircularProgressIndicator(
                modifier = Modifier.testTag("MainLoading"),
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }

    /**
     * A helper composable function for displaying a dialog when there is not internet.
     */
    @Composable
    fun NoInternetDialog(eventHandler: EditorEventHandler) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(IconPack.WifiCancel, contentDescription = null)
            },
            title = {
                Text(text = stringResource(R.string.ly_img_editor_error_dialog_title))
            },
            text = {
                Text(text = stringResource(R.string.ly_img_editor_error_internet_text))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        eventHandler.send(HideErrorDialogEvent)
                        eventHandler.sendCloseEditorEvent()
                    },
                ) {
                    Text(stringResource(R.string.ly_img_editor_dismiss))
                }
            },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        )
    }

    /**
     * A helper composable function for displaying a dialog when the editor captures an error.
     *
     * @param engineException the exception that was caought.
     * @param eventHandler the object that can send [EditorEvent]s and close the editor.
     */
    @Composable
    fun ErrorDialog(
        engineException: EngineException,
        eventHandler: EditorEventHandler,
    ) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(text = stringResource(R.string.ly_img_editor_engine_error_dialog_title))
            },
            text = {
                Text(text = engineException.message ?: "")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        eventHandler.send(HideErrorDialogEvent)
                        eventHandler.sendCloseEditorEvent()
                    },
                ) {
                    Text(stringResource(R.string.ly_img_editor_dismiss))
                }
            },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        )
    }

    /**
     * A helper composable function for displaying a confirmation dialog when closing the editor.
     *
     * @param eventHandler the object that can send [EditorEvent]s and close the editor.
     */
    @Composable
    fun CloseConfirmationDialog(eventHandler: EditorEventHandler) {
        AlertDialog(
            onDismissRequest = { eventHandler.send(DismissCloseConfirmationDialogEvent) },
            icon = {
                Icon(IconPack.Cloudalertoutline, contentDescription = null)
            },
            title = {
                Text(text = stringResource(R.string.ly_img_editor_unsaved_changes_dialog_title))
            },
            text = {
                Text(text = stringResource(R.string.ly_img_editor_unsaved_changes_dialog_text))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        eventHandler.send(DismissCloseConfirmationDialogEvent)
                        eventHandler.sendCloseEditorEvent()
                    },
                ) {
                    Text(stringResource(R.string.ly_img_editor_exit))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { eventHandler.send(DismissCloseConfirmationDialogEvent) },
                ) {
                    Text(stringResource(R.string.ly_img_editor_cancel))
                }
            },
        )
    }
}
