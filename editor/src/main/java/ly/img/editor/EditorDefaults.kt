package ly.img.editor

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.SizeF
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ly.img.editor.base.R
import ly.img.editor.compose.foundation.gestures.detectTapGestures
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.library.data.TextAssetSource
import ly.img.editor.core.library.data.TypefaceProvider
import ly.img.editor.core.theme.LocalExtendedColorScheme
import ly.img.editor.core.ui.engine.isSceneModeVideo
import ly.img.editor.core.ui.iconpack.Checkcircleoutline
import ly.img.editor.core.ui.iconpack.Cloudalertoutline
import ly.img.editor.core.ui.iconpack.Erroroutline
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.WifiCancel
import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine
import ly.img.engine.EngineException
import ly.img.engine.MimeType
import ly.img.engine.addDefaultAssetSources
import ly.img.engine.addDemoAssetSources
import java.io.File
import java.nio.ByteBuffer
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException

/**
 * A helper class that provides implementations of some of the properties in [EngineConfiguration] and [EditorConfiguration].
 */
object EditorDefaults {
    /**
     * A helper implementation of [EngineConfiguration.onCreate]. The implementation does the following:
     * 1. Checks for an existing scene. If one does not exist, it loads the [sceneUri] into the scene
     * 2. Executes the provided [block] to allow custom modifications to the scene after loading
     * 3. Adds [ly.img.editor.core.library.data.TextAssetSource], default and demo asset sources. Check [Engine.addDefaultAssetSources]
     * and [Engine.addDemoAssetSources] documentation for more details.
     *
     * @param engine the engine that is used in the editor.
     * @param sceneUri the Uri that is used to load the scene file into the scene.
     * @param eventHandler the object that can send [EditorEvent]s.
     * @param block a suspend function that allows custom modifications to the scene after it has been loaded.
     * It receives the scene [DesignBlock] and [CoroutineScope] as parameters.
     */
    suspend fun onCreate(
        engine: Engine,
        sceneUri: Uri,
        eventHandler: EditorEventHandler,
        block: suspend (DesignBlock, CoroutineScope) -> Unit = { _, _ -> },
    ) = onCreateCommon(engine, eventHandler, block) {
        engine.scene.load(sceneUri)
    }

    /**
     * A helper implementation of [EngineConfiguration.onCreate]. The implementation does the following:
     * 1. Checks for an existing scene. If one does not exist, it creates a scene from the [imageUri] using [ly.img.engine.SceneApi.createFromImage] API.
     * 2. Executes the provided [block] to allow custom modifications to the scene after loading
     * 3. Adds [ly.img.editor.core.library.data.TextAssetSource], default and demo asset sources. Check [Engine.addDefaultAssetSources]
     * and [Engine.addDemoAssetSources] documentation for more details.
     *
     * @param engine the engine that is used in the editor.
     * @param imageUri the uri of the image that is used to create a scene with single page and image fill.
     * @param size the size that should be used to load the image. If null, original size of the image will be used.
     * @param eventHandler the object that can send [EditorEvent]s.
     * @param block a suspend function that allows custom modifications to the scene after it has been loaded.
     * It receives the scene [DesignBlock] and [CoroutineScope] as parameters.
     */
    suspend fun onCreateFromImage(
        engine: Engine,
        imageUri: Uri,
        eventHandler: EditorEventHandler,
        size: SizeF? = null,
        block: suspend (DesignBlock, CoroutineScope) -> Unit = { _, _ -> },
    ) = onCreateCommon(engine, eventHandler, block) {
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
        onSceneCreated: suspend (DesignBlock, CoroutineScope) -> Unit,
        createScene: suspend (CoroutineScope) -> Unit,
    ) = coroutineScope {
        // Loading is guaranteed to be showing here, no need to send eventHandler.send(ShowLoading)
        if (engine.scene.get() == null) {
            createScene(this)
        }
        val scene = checkNotNull(engine.scene.get())
        onSceneCreated(scene, this)
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
            eventHandler.send(OnSceneLoaded())
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
     * A helper function that opens a system dialog to share the [file].
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
        val uri = FileProvider.getUriForFile(activity, "${activity.packageName}.ly.img.editor.fileprovider", file)
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
     * A helper function to invoke when invoking [EngineConfiguration.onExport].
     *
     * @param engine the engine that is used in the editor.
     * @param eventHandler the object that can send [EditorEvent]s.
     */
    suspend fun onExport(
        engine: Engine,
        eventHandler: EditorEventHandler,
    ) {
        EditorDefaults.run {
            val mimeType: MimeType
            if (engine.isSceneModeVideo) {
                mimeType = MimeType.MP4
                val page = engine.scene.getCurrentPage() ?: engine.scene.getPages()[0]
                eventHandler.send(ShowVideoExportProgressEvent(0f))
                runCatching {
                    val buffer =
                        engine.block.exportVideo(
                            block = page,
                            timeOffset = 0.0,
                            duration = engine.block.getDuration(page),
                            mimeType = mimeType,
                            progressCallback = { progress ->
                                eventHandler.send(
                                    ShowVideoExportProgressEvent(progress.encodedFrames.toFloat() / progress.totalFrames),
                                )
                            },
                        )
                    writeToTempFile(buffer, mimeType)
                }.onSuccess { file ->
                    eventHandler.send(ShowVideoExportSuccessEvent(file, mimeType.key))
                }.onFailure {
                    if (it is CancellationException) {
                        eventHandler.send(DismissVideoExportEvent)
                    } else {
                        eventHandler.send(ShowVideoExportErrorEvent)
                    }
                }
            } else {
                eventHandler.send(ShowLoading)
                mimeType = MimeType.PDF
                val buffer =
                    engine.block.export(
                        block = requireNotNull(engine.scene.get()),
                        mimeType = mimeType,
                    ) {
                        scene.getPages().forEach {
                            block.setScopeEnabled(it, key = "layer/visibility", enabled = true)
                            block.setVisible(it, visible = true)
                        }
                    }
                eventHandler.send(HideLoading)
                eventHandler.send(ShareFileEvent(writeToTempFile(buffer, mimeType), mimeType.key))
            }
        }
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
            is OnSceneLoaded -> {
                state.copy(sceneIsLoaded = true)
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
                state.copy(videoExportStatus = VideoExportStatus.Idle)
            }

            is ShowVideoExportProgressEvent -> {
                state.copy(videoExportStatus = VideoExportStatus.Loading(event.progress))
            }

            is ShowVideoExportErrorEvent -> {
                state.copy(videoExportStatus = VideoExportStatus.Error)
            }

            is ShowVideoExportSuccessEvent -> {
                state.copy(videoExportStatus = VideoExportStatus.Success(event.file, event.mimeType))
            }

            is DismissVideoExportEvent -> {
                state.copy(videoExportStatus = VideoExportStatus.Idle)
            }

            else -> state
        }
    }

    /**
     * A helper composable function that handles the default [EditorUiState] of the editor and draws overlay components.
     * By default the composable function is invoked by the default implementation of the [EditorConfiguration].
     *
     * @param state the current state of the editor.
     * @param eventHandler the object that can send [EditorEvent]s.
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
        val exportStatus = state.videoExportStatus
        if (exportStatus != VideoExportStatus.Idle) {
            VideoExportStatusOverlay(status = exportStatus, eventHandler = eventHandler)
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
                Text(text = stringResource(R.string.ly_img_editor_error_internet_title))
            },
            text = {
                Text(text = stringResource(R.string.ly_img_editor_error_internet_text))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        eventHandler.send(HideErrorDialogEvent)
                        eventHandler.send(EditorEvent.CloseEditor(EditorException(EditorException.Code.NO_INTERNET)))
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
     * @param engineException the exception that was caught.
     * @param eventHandler the object that can send [EditorEvent]s.
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
                        eventHandler.send(EditorEvent.CloseEditor(engineException))
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
     * @param eventHandler the object that can send [EditorEvent]s.
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
                        eventHandler.send(EditorEvent.CloseEditor())
                    },
                ) {
                    Text(stringResource(R.string.ly_img_editor_exit))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { eventHandler.send(DismissCloseConfirmationDialogEvent) },
                ) {
                    Text(stringResource(ly.img.editor.core.R.string.ly_img_editor_cancel))
                }
            },
        )
    }

    /**
     * A helper composable function for displaying the video export status.
     *
     * @param eventHandler the object that can send [EditorEvent]s.
     */
    @Composable
    fun VideoExportStatusOverlay(
        status: VideoExportStatus,
        eventHandler: EditorEventHandler,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.32f))
                    .pointerInput(Unit) {
                        detectTapGestures {
                            // do nothing
                        }
                    },
        ) {
            Surface(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart),
                shape =
                    RoundedCornerShape(
                        topStart = 28.0.dp,
                        topEnd = 28.0.dp,
                        bottomEnd = 0.0.dp,
                        bottomStart = 0.0.dp,
                    ),
                shadowElevation = 16.dp,
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(all = 16.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        when (status) {
                            is VideoExportStatus.Loading -> {
                                var showCancelDialog by remember { mutableStateOf(false) }
                                VideoStatusOverlayContent(
                                    headlineText = R.string.ly_img_editor_export_progress_headline,
                                    labelText = R.string.ly_img_editor_export_progress_label,
                                    buttonText = ly.img.editor.core.R.string.ly_img_editor_cancel,
                                    buttonColor = MaterialTheme.colorScheme.error,
                                    onClick = {
                                        showCancelDialog = true
                                    },
                                    mainContent = {
                                        Box {
                                            ExportProgressIndicator(
                                                progress = status.progress,
                                            )
                                            Text(
                                                text = "${(status.progress * 100).toInt()}%",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.align(Alignment.Center),
                                            )
                                        }
                                    },
                                )

                                if (showCancelDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showCancelDialog = false },
                                        icon = {
                                            Icon(
                                                IconPack.Erroroutline,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error,
                                            )
                                        },
                                        title = {
                                            Text(text = stringResource(R.string.ly_img_editor_export_cancel_dialog_title))
                                        },
                                        text = {
                                            Text(text = stringResource(R.string.ly_img_editor_export_cancel_dialog_text))
                                        },
                                        confirmButton = {
                                            TextButton(
                                                onClick = {
                                                    showCancelDialog = false
                                                    eventHandler.send(EditorEvent.CancelExport())
                                                },
                                                colors =
                                                    ButtonDefaults.textButtonColors(
                                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                                    ),
                                            ) {
                                                Text(stringResource(R.string.ly_img_editor_export_cancel_dialog_confirm_text))
                                            }
                                        },
                                        dismissButton = {
                                            TextButton(
                                                onClick = {
                                                    showCancelDialog = false
                                                },
                                            ) {
                                                Text(stringResource(R.string.ly_img_editor_export_cancel_dialog_dismiss_text))
                                            }
                                        },
                                    )
                                }
                            }

                            VideoExportStatus.Error -> {
                                VideoStatusOverlayContent(
                                    headlineText = R.string.ly_img_editor_export_error_headline,
                                    labelText = R.string.ly_img_editor_export_error_label,
                                    buttonText = ly.img.editor.core.R.string.ly_img_editor_close,
                                    onClick = {
                                        eventHandler.send(DismissVideoExportEvent)
                                    },
                                    mainContent = {
                                        Icon(
                                            IconPack.Erroroutline,
                                            contentDescription = null,
                                            modifier = Modifier.size(144.dp),
                                            tint = MaterialTheme.colorScheme.error,
                                        )
                                    },
                                )
                            }

                            is VideoExportStatus.Success -> {
                                VideoStatusOverlayContent(
                                    headlineText = R.string.ly_img_editor_export_success_headline,
                                    labelText = R.string.ly_img_editor_export_success_label,
                                    buttonText = ly.img.editor.core.R.string.ly_img_editor_close,
                                    onClick = {
                                        eventHandler.send(ShareFileEvent(status.file, status.mimeType))
                                    },
                                    mainContent = {
                                        Icon(
                                            IconPack.Checkcircleoutline,
                                            contentDescription = null,
                                            modifier = Modifier.size(144.dp),
                                            tint = LocalExtendedColorScheme.current.green.color,
                                        )
                                    },
                                )
                            }

                            VideoExportStatus.Idle -> {
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun VideoStatusOverlayContent(
        @StringRes headlineText: Int,
        @StringRes labelText: Int,
        @StringRes buttonText: Int,
        buttonColor: Color = MaterialTheme.colorScheme.primary,
        onClick: () -> Unit,
        mainContent: @Composable () -> Unit,
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        mainContent()
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(headlineText),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(labelText),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(
            onClick = onClick,
            colors =
                ButtonDefaults.textButtonColors(
                    contentColor = buttonColor,
                ),
        ) {
            Text(stringResource(buttonText))
        }
    }
}
