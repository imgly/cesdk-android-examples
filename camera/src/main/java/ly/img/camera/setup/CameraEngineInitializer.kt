package ly.img.camera.setup

import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.concurrent.futures.await
import kotlinx.coroutines.async
import ly.img.camera.core.EngineConfiguration
import ly.img.engine.Engine

@Composable
internal fun CameraEngineInitializer(
    engine: Engine,
    engineConfiguration: EngineConfiguration,
    loadScene: () -> Unit,
    onResult: (Result<ProcessCameraProvider>) -> Unit,
) {
    val context = LocalContext.current
    val savedStateRegistryOwner = LocalSavedStateRegistryOwner.current
    LaunchedEffect(Unit) {
        val deferredCameraProvider =
            async {
                ProcessCameraProvider.getInstance(context).await()
            }
        runCatching {
            engine.start(
                license = engineConfiguration.license,
                userId = engineConfiguration.userId,
                savedStateRegistryOwner = savedStateRegistryOwner,
            )
            loadScene()
        }.onFailure {
            onResult(Result.failure(it))
        }.onSuccess {
            val cameraProvider = deferredCameraProvider.await()
            onResult(Result.success(cameraProvider))
        }
    }
}
