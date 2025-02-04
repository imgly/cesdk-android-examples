package ly.img.cesdk.dock.options.fillstroke

import androidx.compose.ui.graphics.Color
import ly.img.cesdk.engine.getFillColor
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

data class FillUiState(
    val isFillEnabled: Boolean,
    val fillColor: Color
)

internal fun createFillUiState(designBlock: DesignBlock, engine: Engine): FillUiState {
    val isEnabled = engine.block.isFillEnabled(designBlock)
    return FillUiState(
        isFillEnabled = isEnabled,
        fillColor = checkNotNull(engine.getFillColor(designBlock))
    )
}
