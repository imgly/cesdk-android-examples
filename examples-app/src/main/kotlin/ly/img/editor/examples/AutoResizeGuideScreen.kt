package ly.img.editor.examples

import AutoResizeMetrics
import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import autoResize
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import ly.img.engine.Engine

private val autoResizeEngineMutex = Mutex()

@Composable
fun AutoResizeGuideScreen(license: String?) {
    val context = LocalContext.current
    var state by remember { mutableStateOf<AutoResizeGuideState>(AutoResizeGuideState.Running) }

    LaunchedEffect(Unit) {
        state = autoResizeEngineMutex.withLock {
            val application = context.applicationContext as Application
            Engine.init(application)
            val engine = Engine.getInstance(id = "ly.img.engine.autoResize.preview")
            var engineStarted = false

            try {
                engineStarted = engine.start(license = license, userId = "guide-auto-resize")
                check(engineStarted) { "Unable to start the auto-resize guide Engine." }

                engine.bindOffscreen(width = 1080, height = 1920)
                AutoResizeGuideState.Success(autoResize(engine))
            } catch (throwable: Throwable) {
                AutoResizeGuideState.Error(throwable.message ?: throwable::class.simpleName.orEmpty())
            } finally {
                if (engineStarted) {
                    withContext(NonCancellable) {
                        engine.stop()
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Auto-Resize",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "This screen runs the guide example and reports the computed sizing values.",
            style = MaterialTheme.typography.bodyMedium,
        )
        when (val currentState = state) {
            AutoResizeGuideState.Running -> {
                CircularProgressIndicator()
                Text(
                    text = "Building the scene...",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            is AutoResizeGuideState.Success -> {
                Text(
                    text = "Title frame: ${currentState.metrics.titleWidth.toInt()} x ${currentState.metrics.titleHeight.toInt()}",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "Subtitle frame width: ${currentState.metrics.subtitleWidth.toInt()}",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "Title modes: ${currentState.metrics.titleWidthMode} / ${currentState.metrics.titleHeightMode}",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "Background modes: ${currentState.metrics.backgroundWidthMode} / ${currentState.metrics.backgroundHeightMode}",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "Use the back gesture or system back button to return to the guide list.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            is AutoResizeGuideState.Error -> {
                Text(
                    text = "The guide example failed: ${currentState.message}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                )
                Text(
                    text = "Use the back gesture or system back button to return to the guide list.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

private sealed interface AutoResizeGuideState {
    data object Running : AutoResizeGuideState

    data class Success(
        val metrics: AutoResizeMetrics,
    ) : AutoResizeGuideState

    data class Error(
        val message: String,
    ) : AutoResizeGuideState
}
