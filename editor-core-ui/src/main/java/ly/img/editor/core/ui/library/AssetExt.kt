package ly.img.editor.core.ui.library

import ly.img.engine.Asset

fun Asset.getThumbnailUri(): String = getMeta("thumbUri", getUri())

fun Asset.getUri(): String = getMeta("uri", "")

internal fun Asset.getDuration(): String {
    val duration = getMeta("duration", "").toFloatOrNull()?.toInt() ?: return ""
    val minutes = duration / 60
    val seconds = duration % 60
    return String.format("%d:%02d", minutes, seconds)
}

fun Asset.getMeta(key: String) = meta?.find { it.first == key }?.second
fun Asset.getMeta(key: String, default: String) = this.getMeta(key) ?: default