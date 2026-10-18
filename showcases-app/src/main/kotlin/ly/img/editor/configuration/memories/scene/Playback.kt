package ly.img.editor.configuration.memories.scene

import ly.img.engine.Engine

/** Playback engine ops on the slideshow page. The ViewModel holds the matching UI state. */

/** Flip the page's loop setting and return the new value (null if there's no page yet). */
internal fun togglePageLoop(engine: Engine): Boolean? {
    val page = engine.scene.getCurrentPage() ?: return null
    val next = !engine.block.isLooping(page)
    engine.block.setLooping(page, next)
    return next
}

internal fun setPageLooping(
    engine: Engine,
    looping: Boolean,
) {
    engine.scene.getCurrentPage()?.let { engine.block.setLooping(it, looping) }
}

internal fun setPageVolume(
    engine: Engine,
    volume: Float,
) {
    engine.scene.getCurrentPage()?.let { engine.block.setVolume(it, volume) }
}

internal fun getPageVolume(engine: Engine): Float? = engine.scene.getCurrentPage()?.let { engine.block.getVolume(it) }
