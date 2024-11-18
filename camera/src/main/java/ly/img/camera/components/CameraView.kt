package ly.img.camera.components

import androidx.activity.compose.BackHandler
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import ly.img.camera.CameraViewModel
import ly.img.camera.components.sidemenu.SideMenu
import ly.img.camera.preview.CameraEnginePreview
import ly.img.camera.record.RecordingManager
import ly.img.camera.record.Timer
import ly.img.camera.record.components.BottomDock
import ly.img.camera.record.components.DeleteAllRecordingsDialog
import ly.img.editor.core.ui.utils.activity
import ly.img.editor.core.ui.utils.lifecycle.LifecycleEventEffect

@Composable
internal fun BoxScope.CameraView(
    modifier: Modifier,
    cameraProvider: ProcessCameraProvider,
    viewModel: CameraViewModel,
) {
    val cameraState = viewModel.cameraState

    Column(modifier = modifier) {
        CameraEnginePreview(
            engine = viewModel.engine,
            cameraProvider = cameraProvider,
            cameraState = cameraState,
            setCameraPreview = viewModel::setCameraPreview,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        )

        CameraControls(
            isCameraReady = cameraState.isReady,
            isFlashEnabled = cameraState.isFlashEnabled,
            isFlashOn = cameraState.cameraFlash,
            toggleFlash = cameraState::toggleFlash,
            toggleCamera = viewModel::toggleCamera,
        )
    }

    val recordingManager = viewModel.recordingManager
    val context = LocalContext.current
    var showCloseDialog by remember { mutableStateOf(false) }

    fun close() {
        if (recordingManager.hasStartedRecording || recordingManager.state.recordings.isNotEmpty()) {
            showCloseDialog = true
        } else {
            checkNotNull(context.activity).finish()
        }
    }

    if (showCloseDialog) {
        DeleteAllRecordingsDialog(
            onDismiss = { showCloseDialog = false },
            onConfirm = {
                showCloseDialog = false
                recordingManager.close()
                checkNotNull(context.activity).finish()
            },
        )
    }

    BackHandler(onBack = ::close)

    LifecycleEventEffect(event = Lifecycle.Event.ON_STOP) {
        recordingManager.stop()
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(bottom = 84.dp),
    ) {
        Toolbar(
            isRecording = recordingManager.hasStartedRecording,
            duration = recordingManager.state.totalRecordedDuration,
            recordingColor = viewModel.cameraConfiguration.recordingColor,
            onCloseClick = ::close,
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            this@Column.AnimatedVisibility(
                visible = cameraState.isReady && recordingManager.state.status is RecordingManager.Status.Idle,
                modifier =
                    Modifier
                        .align(Alignment.CenterStart)
                        .padding(vertical = 32.dp, horizontal = 12.dp),
                enter = fadeIn() + slideInHorizontally(),
                exit = fadeOut() + slideOutHorizontally(),
            ) {
                SideMenu(
                    timer = recordingManager.state.timer,
                    setTimer = recordingManager::setTimer,
                )
            }

            val state = recordingManager.state
            if (state.timer != Timer.Off) {
                CountdownTimerView(
                    modifier = Modifier.align(Alignment.Center),
                    recordingColor = viewModel.cameraConfiguration.recordingColor,
                    recordingStatus = state.status,
                )
            }
        }

        BottomDock(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 20.dp),
            cameraState = cameraState,
            recordingManager = recordingManager,
            cameraConfiguration = viewModel.cameraConfiguration,
        )
    }
}
