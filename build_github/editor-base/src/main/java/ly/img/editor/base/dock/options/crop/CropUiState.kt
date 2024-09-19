package ly.img.editor.base.dock.options.crop

import ly.img.editor.base.engine.canResetCrop
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

data class CropUiState(
    val straightenAngle: Float,
    val cropScaleRatio: Float,
    val canResetCrop: Boolean,
)

internal fun createCropUiState(
    designBlock: DesignBlock,
    engine: Engine,
    cropScaleRatio: Float? = null,
): CropUiState {
    return CropUiState(
        straightenAngle = getStraightenDegrees(engine, designBlock),
        cropScaleRatio = cropScaleRatio ?: engine.block.getCropScaleRatio(designBlock),
        canResetCrop = engine.canResetCrop(designBlock),
    )
}
