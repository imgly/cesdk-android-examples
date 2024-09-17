package ly.img.editor.base.timeline.ext

import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sign
import kotlin.time.Duration
import kotlin.time.DurationUnit

fun Duration.formatForPlayer(): String {
    return toComponents { minutes, seconds, _ ->
        String.format(locale = Locale.getDefault(), "%d:%02d", minutes, seconds)
    }
}

private const val THRESHOLD_FOR_FRACTIONAL_SECONDS = 10

fun Duration.formatForClip(showFractionalPart: Boolean = true): String {
    val seconds = toDouble(DurationUnit.SECONDS)

    val roundedSeconds =
        if (abs(seconds) >= THRESHOLD_FOR_FRACTIONAL_SECONDS) {
            floor(abs(seconds)) * seconds.sign
        } else {
            floor(abs(seconds) * 10) / 10 * seconds.sign
        }

    val absSeconds = abs(roundedSeconds)
    val minutes = (absSeconds / 60).toInt()
    val remainingSeconds = absSeconds % 60

    return buildString {
        if (seconds < 0) append("-")
        if (minutes > 0) {
            append("${minutes}m")
            if (remainingSeconds > 0) append(" ")
        }
        if (remainingSeconds > 0 || minutes == 0) {
            if (showFractionalPart && abs(seconds) < THRESHOLD_FOR_FRACTIONAL_SECONDS) {
                append(String.format(Locale.getDefault(), "%.1fs", remainingSeconds))
            } else {
                append("${remainingSeconds.toInt()}s")
            }
        }
    }
}
