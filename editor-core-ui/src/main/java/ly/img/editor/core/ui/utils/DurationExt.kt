package ly.img.editor.core.ui.utils

import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun Duration.almostEquals(other: Duration): Boolean {
    return this - other <= EPS_DURATION
}

fun Duration.formatForPlayer(): String {
    return toComponents { minutes, seconds, _ ->
        String.format(locale = Locale.getDefault(), "%d:%02d", minutes, seconds)
    }
}

val EPS_DURATION = 0.001.seconds
