package ly.img.editor.base.ui

import android.net.Uri
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import ly.img.editor.base.dock.OptionType
import ly.img.editor.base.dock.options.format.HorizontalAlignment
import ly.img.editor.base.dock.options.format.VerticalAlignment
import ly.img.editor.base.engine.AdjustmentState
import ly.img.editor.base.engine.EffectAndBlurOptions
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.core.ui.BaseEvent
import ly.img.engine.Asset
import ly.img.engine.BlendMode
import ly.img.engine.BlurType
import ly.img.engine.DesignBlock
import ly.img.engine.EffectType
import ly.img.engine.TextCase
import ly.img.engine.Typeface
import kotlin.time.Duration

/**
 * To communicate events from the UI to the ViewModel.
 */
interface Event : BaseEvent {
    data class OnError(val throwable: Throwable) : Event

    object OnBack : Event

    object OnBackPress : Event

    object OnCloseDock : Event

    object OnHideSheet : Event

    object OnExpandSheet : Event

    object OnHideScrimSheet : Event

    data class OnOptionClick(val optionType: OptionType) : Event

    object OnAddLibraryClick : Event

    data class OnAddLibraryCategoryClick(val libraryCategory: LibraryCategory, val addToBackgroundTrack: Boolean? = null) : Event

    object OnKeyboardClose : Event

    data class OnKeyboardHeightChange(val heightInDp: Float) : Event

    data class OnCanvasMove(val move: Boolean) : Event

    data class OnLoadScene(val height: Float, val insets: Rect, val inPortraitMode: Boolean) : Event

    object OnCanvasTouch : Event

    object OnResetZoom : Event

    data class OnUpdateBottomInset(val bottomInset: Float, val zoom: Boolean, val isExpanding: Boolean) : Event

    object OnUndoClick : Event

    object OnRedoClick : Event

    object OnExportClick : Event

    data class OnTogglePreviewMode(val isChecked: Boolean) : Event

    data class EnableHistory(val enable: Boolean) : Event

    data class OnBottomSheetHeightChange(val heightInDp: Float, val showTimeline: Boolean) : Event

    data class OnPage(val page: Int) : Event

    data object OnNextPage : Event

    data object OnPreviousPage : Event

    data object OnPause : Event

    data object OnReorder : Event

    data class OnAddPage(val index: Int) : Event

    data object OnTogglePagesMode : Event

    data class OnPagesModePageSelectionChange(val page: EditorPagesState.Page) : Event

    data class OnPagesModePageBind(val page: EditorPagesState.Page, val pageHeight: Int) : Event
}

interface BlockEvent : Event {
    object OnChangeFinish : BlockEvent

    // region Layer Events
    object OnForward : BlockEvent

    data class OnForwardNonSelected(val block: DesignBlock) : BlockEvent

    object OnBackward : BlockEvent

    data class OnBackwardNonSelected(val block: DesignBlock) : BlockEvent

    object OnDuplicate : BlockEvent

    data class OnDuplicateNonSelected(val block: DesignBlock) : BlockEvent

    object OnDelete : BlockEvent

    data class OnDeleteNonSelected(val block: DesignBlock) : BlockEvent

    object ToFront : BlockEvent

    object ToBack : BlockEvent

    data class OnChangeBlendMode(val blendMode: BlendMode) : BlockEvent

    data class OnChangeOpacity(val opacity: Float) : BlockEvent
    // endregion

    // region Fill Events
    object OnDisableFill : BlockEvent

    object OnEnableFill : BlockEvent

    data class OnChangeFillColor(val color: Color) : BlockEvent

    data class OnChangeGradientFillColors(val index: Int, val color: Color) : BlockEvent

    data class OnChangeLinearGradientParams(val startX: Float, val startY: Float, val endX: Float, val endY: Float) : BlockEvent

    data class OnChangeRadialGradientParams(val centerX: Float, val centerY: Float, val radius: Float) : BlockEvent

    data class OnChangeConicalGradientParams(val centerX: Float, val centerY: Float) : BlockEvent
    // endregion

    // region Stroke Events
    object OnDisableStroke : BlockEvent

    data class OnChangeStrokeColor(val color: Color) : BlockEvent

    data class OnChangeStrokeWidth(val width: Float) : BlockEvent

    data class OnChangeStrokeStyle(val style: String) : BlockEvent

    data class OnChangeFillStyle(val style: String) : BlockEvent

    data class OnChangeStrokePosition(val position: String) : BlockEvent

    data class OnChangeStrokeJoin(val join: String) : BlockEvent
    // endregion

    // region Shape Events
    data class OnChangeLineWidth(val width: Float) : BlockEvent

    data class OnChangePolygonSides(val sides: Float) : BlockEvent

    data class OnChangePolygonCornerRadius(val sides: Float) : BlockEvent

    data class OnChangeRectCornerRadius(
        val topLeft: Float,
        val topRight: Float,
        val bottomLeft: Float,
        val bottomRight: Float,
    ) : BlockEvent

    data class OnChangeStarPoints(val points: Float) : BlockEvent

    data class OnChangeStarInnerDiameter(val diameter: Float) : BlockEvent
    // endregion

    // region Text Format Events
    data class OnChangeLetterSpacing(val spacing: Float) : BlockEvent

    data class OnChangeParagraphSpacing(val spacing: Float) : BlockEvent

    data class OnChangeLineHeight(val height: Float) : BlockEvent

    data class OnChangeSizeMode(val sizeMode: String) : BlockEvent

    data class OnChangeClipping(val enabled: Boolean) : BlockEvent

    data object OnBoldToggle : BlockEvent

    data object OnItalicToggle : BlockEvent

    data class OnChangeHorizontalAlignment(val alignment: HorizontalAlignment) : BlockEvent

    data class OnChangeLetterCasing(val casing: TextCase) : BlockEvent

    data class OnChangeVerticalAlignment(val alignment: VerticalAlignment) : BlockEvent

    data class OnChangeFont(val fontUri: Uri, val typeface: Typeface) : BlockEvent

    data class OnChangeFontSize(val fontSize: Float) : BlockEvent

    data class OnChangeTypeface(val fallbackFontUri: Uri, val typeface: Typeface) : BlockEvent
    // endregion

    // region Adjustments Events
    data class OnReplaceColorFilter(
        val designBlock: DesignBlock,
        val assetSourceType: AssetSourceType?,
        val asset: Asset?,
    ) : BlockEvent

    data class OnReplaceFxEffect(val designBlock: DesignBlock, val effect: EffectType?) : BlockEvent

    data class OnReplaceBlurEffect(val designBlock: DesignBlock, val effect: BlurType?) : BlockEvent

    data class OnChangeEffectSettings(val adjustment: EffectAndBlurOptions, val value: AdjustmentState.Value) : BlockEvent
    // endregion

    // region Crop Events
    object OnFlipCropHorizontal : BlockEvent

    object OnResetCrop : BlockEvent

    data class OnCropRotate(val scaleRatio: Float) : BlockEvent

    data class OnCropStraighten(val angle: Float, val scaleRatio: Float) : BlockEvent
    // endregion

    // region Volume Events
    data class OnVolumeChange(val volume: Float) : BlockEvent

    object OnToggleMute : BlockEvent
    // endregion

    // region Timeline Events
    object OnDeselect : BlockEvent

    data class OnToggleSelectBlock(val block: DesignBlock) : BlockEvent

    data class OnUpdateTrim(val trimOffset: Duration, val timeOffset: Duration, val duration: Duration) : BlockEvent

    data class OnUpdateTimeOffset(val timeOffset: Duration) : BlockEvent

    data class OnUpdateDuration(val duration: Duration) : BlockEvent

    object OnSplit : BlockEvent

    object OnToggleBackgroundTrackAttach : BlockEvent

    data class OnReorder(val block: DesignBlock, val newIndex: Int) : BlockEvent
    // endregion
}
