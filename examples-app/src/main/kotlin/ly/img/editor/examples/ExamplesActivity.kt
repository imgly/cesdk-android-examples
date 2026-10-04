package ly.img.editor.examples

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import ly.img.engine.Engine

private const val DEEPLINK_DEDUPLICATION_WINDOW_MS = 2_000L

class ExamplesActivity : ComponentActivity() {
    private var navController: NavHostController? = null
    private var navControllerPopJob: Job? = null
    private var pendingDeeplinkIntent: Intent? = null
    private var lastHandledDeeplinkKey: String? = null
    private var lastHandledDeeplinkAtMillis = 0L
    private var touchEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Engine.init(application)
        // The launch intent is preserved across Activity recreation. Only replay
        // the initial deep link for a fresh Activity so restored navigation state
        // is not overwritten after configuration changes.
        if (savedInstanceState == null && intent.data != null) {
            pendingDeeplinkIntent = intent
        }
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                this.navController = navController
                LaunchedEffect(navController) {
                    pendingDeeplinkIntent?.let {
                        pendingDeeplinkIntent = null
                        handleDeeplinkIntent(it)
                    }
                }
                ExamplesApp(navController = navController)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val deeplinkKey = intent.deeplinkKey() ?: return
        // Appetize can deliver the launch URI again via onNewIntent. If the
        // same deep link is already queued or was just handled, consume it once.
        if (
            pendingDeeplinkIntent?.deeplinkKey() == deeplinkKey ||
            isRecentlyHandledDeeplink(deeplinkKey)
        ) {
            return
        }
        // On Appetize, onNewIntent is fired very soon after onCreate returns. Because
        // setContent only schedules composition (it doesn't run synchronously), the
        // first composition hasn't yet assigned navController by the time this runs.
        // Queue the intent and replay it from a LaunchedEffect once navController is ready.
        if (navController == null) {
            pendingDeeplinkIntent = intent
            return
        }
        handleDeeplinkIntent(intent)
    }

    private fun handleDeeplinkIntent(intent: Intent) {
        rememberHandledDeeplink(intent)
        touchEnabled = false
        navController?.popBackStack(route = Destination.Home.route, inclusive = false)
        // Pop backstack has animation, therefore we need to wait for it to finish before launching new screens
        navControllerPopJob?.cancel()
        navControllerPopJob = lifecycleScope.launch {
            navController
                ?.visibleEntries
                ?.takeWhile {
                    val found = it.firstOrNull()?.destination?.route == Destination.Home.route
                    if (found) {
                        try {
                            navController?.navigate(NavDeepLinkRequest(intent))
                        } catch (_: IllegalArgumentException) {
                            // Do nothing if we cannot handle this deeplink
                        }
                        touchEnabled = true
                    }
                    !found
                }?.collect()
        }
    }

    private fun rememberHandledDeeplink(intent: Intent) {
        lastHandledDeeplinkKey = intent.deeplinkKey()
        lastHandledDeeplinkAtMillis = SystemClock.uptimeMillis()
    }

    private fun isRecentlyHandledDeeplink(deeplinkKey: String): Boolean = lastHandledDeeplinkKey == deeplinkKey &&
        SystemClock.uptimeMillis() - lastHandledDeeplinkAtMillis <=
        DEEPLINK_DEDUPLICATION_WINDOW_MS

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (!touchEnabled) {
            return true
        }
        return super.dispatchTouchEvent(ev)
    }
}

private fun Intent.deeplinkKey(): String? = dataString ?: data?.toString()

@Composable
private fun ExamplesApp(navController: NavHostController) {
    Surface(color = MaterialTheme.colorScheme.background) {
        NavHost(
            navController = navController,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
            startDestination = Destination.Home.route,
            builder = { build(navController = navController) },
        )
    }
}
