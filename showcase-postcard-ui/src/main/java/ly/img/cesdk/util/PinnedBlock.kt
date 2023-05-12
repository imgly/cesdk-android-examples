package ly.img.cesdk.util

import ly.img.engine.Engine

internal fun Engine.getPinnedBlock() = block.findByName("Greeting").firstOrNull()

internal fun Engine.requirePinnedBlock() = checkNotNull(getPinnedBlock())