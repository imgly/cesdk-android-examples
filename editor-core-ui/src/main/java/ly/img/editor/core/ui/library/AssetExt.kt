package ly.img.editor.core.ui.library

import ly.img.engine.Asset

fun Asset.getThumbnailUri(): String = getMeta("thumbUri", getUri())

fun Asset.getUri(): String = getMeta("uri", "")

internal fun Asset.getDuration(): Double? = getMeta("duration")?.toDoubleOrNull()

internal fun Asset.getFormattedDuration(): String {
    val duration = getDuration()?.toInt() ?: return ""
    val minutes = duration / 60
    val seconds = duration % 60
    return String.format("%d:%02d", minutes, seconds)
}

fun Asset.getMeta(key: String): String? = meta?.get(key)

fun Asset.getMeta(
    key: String,
    default: String,
) = this.getMeta(key) ?: default
