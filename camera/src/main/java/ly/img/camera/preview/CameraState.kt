package ly.img.camera.preview

import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.TorchState
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class CameraState(
    private val previewUseCase: UseCase,
    private val videoCaptureUseCase: UseCase,
) {
    val isFlashEnabled: Boolean
        get() = !showFrontCamera

    var showFrontCamera by mutableStateOf(false)
        private set

    var cameraFlash by mutableStateOf(false)
        private set

    var isReady by mutableStateOf(false)

    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var lifecycleOwner: LifecycleOwner? = null

    fun toggleCamera() {
        showFrontCamera = !showFrontCamera
        if (showFrontCamera) {
            cameraFlash = false
        }
        rebind()
    }

    fun toggleFlash() {
        cameraFlash = !cameraFlash
        camera?.cameraControl?.enableTorch(cameraFlash)
    }

    fun bind(
        cameraProvider: ProcessCameraProvider,
        lifecycleOwner: LifecycleOwner,
        uiScope: CoroutineScope,
    ) {
        this.cameraProvider = cameraProvider
        this.lifecycleOwner = lifecycleOwner
        uiScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cameraFlash = camera?.cameraInfo?.torchState?.value == TorchState.ON
            }
        }
        rebind()
    }

    fun setZoomRatio(zoom: Float) {
        val zoomState = camera?.cameraInfo?.zoomState?.value ?: return
        val currentZoomRatio = zoomState.zoomRatio
        val newZoomRatio = currentZoomRatio * zoom
        val clampedZoomRatio = newZoomRatio.coerceIn(zoomState.minZoomRatio, zoomState.maxZoomRatio)
        camera?.cameraControl?.setZoomRatio(clampedZoomRatio)
    }

    private fun rebind() {
        val cameraProvider = cameraProvider ?: return
        val lifecycleOwner = lifecycleOwner ?: return
        cameraProvider.unbindAll()
        val cameraSelector = if (showFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
        camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, previewUseCase, videoCaptureUseCase)
    }
}
