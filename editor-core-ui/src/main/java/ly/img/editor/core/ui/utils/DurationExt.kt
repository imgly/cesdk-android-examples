package ly.img.editor.core.ui.utils

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun Duration.almostEquals(other: Duration): Boolean {
    return this - other <= EPS_DURATION
}

val EPS_DURATION = 0.001.seconds
