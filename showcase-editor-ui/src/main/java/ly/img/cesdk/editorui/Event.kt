package ly.img.cesdk.editorui

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import ly.img.cesdk.dock.OptionType
import ly.img.cesdk.dock.options.format.HorizontalAlignment
import ly.img.cesdk.dock.options.format.VerticalAlignment
import ly.img.cesdk.library.data.font.FontData
import ly.img.engine.BlendMode
import ly.img.engine.FillType
import ly.img.engine.GradientType

/**
 * To communicate events from the UI to the ViewModel.
 */
interface Event {
    object OnBack : Event
    object OnBackPress : Event
    object OnExit : Event
    object OnCloseDock : Event
    object HideSheet : Event
    object ExpandSheet : Event
    data class OnOptionClick(val optionType: OptionType) : Event
    object OnAddLibraryClick : Event
    object OnKeyboardClose : Event
    data class OnKeyboardHeightChange(val heightInDp: Int) : Event
    data class OnCanvasMove(val move: Boolean) : Event
    data class OnLoadScene(val sceneUriString: String, val height: Float, val insets: Rect, val inPortraitMode: Boolean) : Event
    object OnUndoClick : Event
    object OnRedoClick : Event
    object OnExportClick : Event
    data class OnTogglePreviewMode(val isChecked: Boolean) : Event
    data class EnableHistory(val enable: Boolean) : Event
    data class OnBottomSheetHeightChange(val heightInDp: Int) : Event
}

interface BlockEvent : Event {
    object OnChangeFinish : BlockEvent

    // region Layer Events
    object OnForward : BlockEvent
    object OnBackward : BlockEvent
    object OnDuplicate : BlockEvent
    object OnDelete : BlockEvent
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

    data class OnChangeLinearGradientParams(val startX: Float, val startY : Float, val endX: Float, val endY: Float) : BlockEvent
    data class OnChangeRadialGradientParams(val centerX: Float, val centerY : Float, val radius: Float) : BlockEvent
    data class OnChangeConicalGradientParams(val centerX: Float, val centerY : Float) : BlockEvent
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
    data class OnChangeStarPoints(val points: Float) : BlockEvent
    data class OnChangeStarInnerDiameter(val diameter: Float) : BlockEvent
    // endregion

    // region Text Format Events
    data class OnChangeLetterSpacing(val spacing: Float) : BlockEvent
    data class OnChangeLineHeight(val height: Float) : BlockEvent
    data class OnChangeSizeMode(val sizeMode: String) : BlockEvent
    data class OnBold(val fontFamily: String, val bold: Boolean) : BlockEvent
    data class OnItalicize(val fontFamily: String, val italicize: Boolean) : BlockEvent
    data class OnChangeHorizontalAlignment(val alignment: HorizontalAlignment) : BlockEvent
    data class OnChangeVerticalAlignment(val alignment: VerticalAlignment) : BlockEvent
    data class OnChangeFont(val font: FontData) : BlockEvent
    data class OnChangeFontSize(val fontSize: Float) : BlockEvent
    // endregion

    // region Crop Events
    object OnFlipCropHorizontal : BlockEvent
    object OnResetCrop : BlockEvent
    data class OnCropRotate(val scaleRatio: Float) : BlockEvent
    data class OnCropStraighten(val angle: Float, val scaleRatio: Float) : BlockEvent
    // endregion
}
