package ly.img.camera

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.camera.components.CameraView
import ly.img.camera.components.TOOLBAR_HEIGHT
import ly.img.camera.setup.CameraEngineInitializer
import ly.img.camera.setup.SetupView
import ly.img.editor.core.navbar.SystemNavBar
import ly.img.editor.core.theme.EditorTheme
import ly.img.editor.core.theme.LocalExtendedColorScheme
import ly.img.editor.core.ui.permissions.PermissionManager.Companion.hasCameraPermission
import ly.img.editor.core.ui.permissions.PermissionManager.Companion.hasMicPermission
import ly.img.engine.Engine

open class CameraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Engine.init(application)
        setContent {
            EditorTheme(useDarkTheme = true) {
                val viewModel = viewModel<CameraViewModel>()

                // fail early
                val engineConfig =
                    viewModel.engineConfiguration ?: run {
                        Log.i("CameraActivity", "EngineConfiguration not found in intent, finishing...")
                        finish()
                        return@EditorTheme
                    }

                SystemNavBar(LocalExtendedColorScheme.current.black)

                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(LocalExtendedColorScheme.current.black)
                            .systemBarsPadding()
                            .background(MaterialTheme.colorScheme.surface),
                ) {
                    val context = LocalContext.current

                    var hasRequiredPermissions by remember {
                        mutableStateOf(
                            context.hasMicPermission() && context.hasCameraPermission(),
                        )
                    }

                    var initResult by remember { mutableStateOf<Result<ProcessCameraProvider>?>(null) }
                    val cameraProvider = initResult?.getOrNull()

                    if (hasRequiredPermissions && cameraProvider != null) {
                        CameraView(
                            modifier = Modifier.fillMaxSize(),
                            cameraProvider = cameraProvider,
                            viewModel = viewModel,
                        )
                    } else {
                        SetupView(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .align(Alignment.TopCenter)
                                    .padding(top = TOOLBAR_HEIGHT, bottom = 196.dp),
                            hasRequiredPermissions = hasRequiredPermissions,
                            isLoading = initResult == null,
                            error = initResult?.exceptionOrNull(),
                            onClose = {
                                finish()
                            },
                            onAllPermissionsGranted = {
                                hasRequiredPermissions = true
                            },
                        )
                    }

                    // While user is granting permissions,
                    // we use the time to get the camera and the engine ready in the background
                    CameraEngineInitializer(
                        engine = viewModel.engine,
                        engineConfiguration = engineConfig,
                        loadScene = viewModel::loadScene,
                        onResult = {
                            initResult = it
                        },
                    )
                }
            }
        }
    }
}
