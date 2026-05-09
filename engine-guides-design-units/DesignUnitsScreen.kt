import android.app.Activity
import android.view.SurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

@Composable
fun DesignUnitsScreen(license: String) {
    val activity = LocalContext.current as Activity
    val savedStateRegistryOwner = LocalSavedStateRegistryOwner.current
    val engine = remember { Engine.getInstance(id = "ly.img.engine.example.design-units-screen") }
    val surfaceView = remember { SurfaceView(activity) }
    var status by remember { mutableStateOf("Configuring millimeter scene...") }

    LaunchedEffect(engine, savedStateRegistryOwner) {
        runCatching {
            engine.start(
                license = license,
                userId = "design-units-example",
                savedStateRegistryOwner = savedStateRegistryOwner,
            )
            engine.bindSurfaceView(surfaceView)

            val page =
                if (engine.scene.get() == null || engine.block.findByType(DesignBlockType.Page).isEmpty()) {
                    configureDesignUnits(engine)
                } else {
                    engine.block.findByType(DesignBlockType.Page).first()
                }

            engine.scene.zoomToBlock(
                block = page,
                paddingLeft = 40F,
                paddingTop = 40F,
                paddingRight = 40F,
                paddingBottom = 40F,
            )
            status = "A4 scene configured with millimeters and 300 DPI."
        }.onFailure { throwable ->
            status = throwable.message ?: "Failed to configure the example."
        }
    }

    DisposableEffect(activity, engine) {
        onDispose {
            engine.unbind()
            if (!activity.isChangingConfigurations) {
                engine.stop()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { surfaceView },
        )
        Text(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.65f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            text = status,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
