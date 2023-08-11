package ly.img.cesdk.core.data

import ly.img.engine.Asset

internal fun Asset.getThumbnailUri(): String = getMeta("thumbUri", getUri())!!

internal fun Asset.getUri(): String = getMeta("uri", "")!!

internal fun Asset.getDuration(): String {
    val duration = getMeta("duration", "")!!.toFloatOrNull()?.toInt() ?: return ""
    val minutes = duration / 60
    val seconds = duration % 60
    return String.format("%d:%02d", minutes, seconds)
}

internal fun Asset.getMeta(key: String, default: String?) = meta?.find { it.first == key }?.second ?: default