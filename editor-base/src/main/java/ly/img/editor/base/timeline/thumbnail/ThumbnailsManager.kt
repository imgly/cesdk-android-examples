package ly.img.editor.base.timeline.thumbnail

import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineScope
import ly.img.editor.base.timeline.clip.Clip
import ly.img.editor.base.timeline.clip.ClipType
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

class ThumbnailsManager(
    private val engine: Engine,
    private val scope: CoroutineScope,
) {
    private val providers = hashMapOf<DesignBlock, ThumbnailsProvider>()

    fun getProvider(designBlock: DesignBlock): ThumbnailsProvider? {
        return providers[designBlock]
    }

    fun destroyProvider(designBlock: DesignBlock) {
        val provider = providers.remove(designBlock)
        provider?.cancel()
    }

    fun refreshThumbnails(
        clip: Clip,
        width: Dp,
    ) {
        if (clip.clipType == ClipType.Audio) return

        val provider =
            providers.getOrPut(clip.id) {
                ThumbnailsProvider(engine, scope)
            }
        provider.loadThumbnails(clip, width)
    }
}
