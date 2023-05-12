package ly.img.cesdk.library.components.assets

import ly.img.engine.Asset

fun Asset.getThumbnailUri(): String = meta?.find { it.first == "thumbUri" }?.second ?: getUri()

fun Asset.getUri(): String = meta?.find { it.first == "uri" }?.second ?: ""