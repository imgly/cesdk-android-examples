package ly.img.editor.base.dock

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import ly.img.editor.base.dock.options.adjustment.AdjustmentUiState
import ly.img.editor.base.dock.options.effect.EffectUiState
import ly.img.editor.base.dock.options.fillstroke.FillStrokeUiState
import ly.img.editor.base.dock.options.format.FormatUiState
import ly.img.editor.base.dock.options.layer.LayerUiState
import ly.img.editor.base.dock.options.shapeoptions.ShapeOptionsUiState
import ly.img.editor.core.component.data.Height
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.ui.engine.BlockType
import ly.img.engine.DesignBlock
import ly.img.engine.Engine

interface BottomSheetContent {
    val isFloating: Boolean

    fun isHalfExpandingEnabled(): Boolean = false

    fun isInitialExpandHalf(): Boolean = false

    fun getType(): String {
        return javaClass.name
    }
}

class CustomBottomSheetContent(
    override val isFloating: Boolean,
    val maxHeight: Height?,
    val content: @Composable BoxScope.(Engine) -> Unit,
) : BottomSheetContent {
    override fun isInitialExpandHalf(): Boolean = false
}

class CustomFullScreenBottomSheetContent(
    val heightFraction: Float,
    private val isHalfExpandingEnabled: Boolean = true,
    private val isHalfExpandedInitially: Boolean = false,
    val content: @Composable BoxScope.(Engine) -> Unit,
) : BottomSheetContent {
    override val isFloating = true

    override fun isHalfExpandingEnabled() = isHalfExpandingEnabled

    override fun isInitialExpandHalf(): Boolean = isHalfExpandedInitially
}

object LibraryBottomSheetContent : BottomSheetContent {
    override val isFloating = true

    override fun isHalfExpandingEnabled() = true
}

class LibraryCategoryBottomSheetContent(
    override val isFloating: Boolean,
    private val isHalfExpandingEnabled: Boolean,
    private val isHalfExpandedInitially: Boolean?,
    val libraryCategory: LibraryCategory,
    val addToBackgroundTrack: Boolean,
) : BottomSheetContent {
    override fun isHalfExpandingEnabled() = isHalfExpandingEnabled

    override fun isInitialExpandHalf(): Boolean = isHalfExpandedInitially ?: libraryCategory.isHalfExpandedInitially
}

class ReplaceBottomSheetContent(
    override val isFloating: Boolean,
    val designBlock: DesignBlock,
    val blockType: BlockType,
) : BottomSheetContent {
    override fun isHalfExpandingEnabled() = true

    override fun isInitialExpandHalf(): Boolean = true
}

class LayerBottomSheetContent(
    override val isFloating: Boolean,
    val uiState: LayerUiState,
) : BottomSheetContent

class OptionsBottomSheetContent(
    override val isFloating: Boolean,
    val uiState: ShapeOptionsUiState,
) : BottomSheetContent

class FillStrokeBottomSheetContent(
    override val isFloating: Boolean,
    val uiState: FillStrokeUiState,
) : BottomSheetContent

class FormatBottomSheetContent(
    override val isFloating: Boolean,
    val uiState: FormatUiState,
) : BottomSheetContent

class AdjustmentSheetContent(
    override val isFloating: Boolean,
    val uiState: AdjustmentUiState,
) : BottomSheetContent

class EffectSheetContent(
    override val isFloating: Boolean,
    val uiState: EffectUiState,
) : BottomSheetContent
