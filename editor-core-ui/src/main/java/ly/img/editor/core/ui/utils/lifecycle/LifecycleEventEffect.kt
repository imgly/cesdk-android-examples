package ly.img.editor.core.ui.utils.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun LifecycleEventEffect(
    event: Lifecycle.Event,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: () -> Unit,
) {
    if (event == Lifecycle.Event.ON_DESTROY) {
        throw IllegalArgumentException(
            "LifecycleEventEffect cannot be used to " +
                "listen for Lifecycle.Event.ON_DESTROY, since Compose disposes of the " +
                "composition before ON_DESTROY observers are invoked.",
        )
    }

    // Safely update the current `onEvent` lambda when a new one is provided
    val currentOnEvent by rememberUpdatedState(onEvent)
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, e ->
                if (e == event) {
                    currentOnEvent()
                }
            }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
