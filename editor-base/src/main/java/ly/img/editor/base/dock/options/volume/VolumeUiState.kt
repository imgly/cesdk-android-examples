package ly.img.editor.base.dock.options.volume

import ly.img.engine.DesignBlock
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

data class VolumeUiState(
    val volume: Float,
) {
    companion object Factory {
        fun create(
            designBlock: DesignBlock,
            engine: Engine,
        ): VolumeUiState {
            val volumeBlock =
                if (DesignBlockType.get(engine.block.getType(designBlock)) == DesignBlockType.Audio) {
                    designBlock
                } else {
                    engine.block.getFill(designBlock)
                }
            val isMuted = engine.block.isMuted(volumeBlock)
            return VolumeUiState(if (isMuted) 0f else engine.block.getVolume(volumeBlock))
        }
    }
}
