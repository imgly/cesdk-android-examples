package ly.img.editor.core.event

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import ly.img.editor.core.EditorScope
import ly.img.editor.core.component.data.Height
import ly.img.editor.core.library.LibraryCategory
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.engine.DesignBlock
import ly.img.engine.Engine
import kotlin.time.Duration

/**
 * An editor event that can be sent via [EditorEventHandler]. If the event is an instance of [EditorEvent.Internal] then it will
 * be handled by the editor automatically. All other events are forwarded to [ly.img.editor.EditorConfiguration.onEvent].
 */
interface EditorEvent {
    /**
     * An editor event that can be sent via [EditorEventHandler] and handled internally by the editor.
     */
    sealed interface Internal : EditorEvent

    /**
     * An event for closing the editor. This force closes the editor without entering the
     * [ly.img.editor.EngineConfiguration.onClose] callback.
     *
     * @param throwable an optional parameter in case the editor is closed due to an error.
     */
    class CloseEditor(val throwable: Throwable? = null) : Internal

    /**
     * An event for canceling the export job if it is running.
     */
    class CancelExport : Internal

    /**
     * An event for opening a sheet with custom [content].
     * Useful when integrated with [ly.img.editor.core.component.Dock].
     *
     * @param isFloating whether the sheet should be floating. If true the sheet will be rendered over the editor. If false the
     * canvas will be zoomed to adjust the sheet.
     * Default value is true.
     * @param maxHeight the maximum height of the sheet. If null, then there is no limit to the height. Once the maximum
     * height is reached, the [content] of the bottom sheet becomes vertically scrollable.
     * Default value is half the height of the editor.
     * @param content the content of the opened bottom sheet.
     */
    class OpenBottomSheet(
        val isFloating: Boolean = true,
        val maxHeight: Height? = Height.Fraction(0.5F),
        val content: @Composable BoxScope.(Engine) -> Unit,
    ) : Internal

    /**
     * An event for opening a sheet with custom [content].
     * Useful when integrated with [ly.img.editor.core.component.Dock].
     *
     * @param heightFraction the height of the sheet as a fraction of the height of the editor when the sheet is expanded.
     * Default value is 1F.
     * @param isHalfExpandingEnabled whether the sheet should have a half expanded state.
     * If false, then the sheet gets only 2 states: hidden and expanded.
     * Default value is true.
     * @param isHalfExpandedInitially whether the sheet should be opened as half expanded initially.
     * This flag takes effect only if [isHalfExpandingEnabled] is true.
     * Default value is false.
     * @param content the content of the opened bottom sheet.
     */
    class OpenFullScreenBottomSheet(
        @FloatRange(from = 0.0, to = 1.0) val heightFraction: Float = 1F,
        val isHalfExpandingEnabled: Boolean = true,
        val isHalfExpandedInitially: Boolean = false,
        val content: @Composable BoxScope.(Engine) -> Unit,
    ) : Internal

    /**
     * An event for closing the sheet that is currently open.
     */
    class CloseBottomSheet : Internal

    /**
     * An event for opening a library category. Useful when integrated with [ly.img.editor.core.component.Dock].
     *
     * @param libraryCategory the library category that should be opened as a bottom sheet.
     * @param isFloating whether the sheet should be floating. If true the sheet will be rendered over the editor. If false the
     * canvas will be zoomed to adjust the sheet.
     * Default value is true.
     * @param isHalfExpandingEnabled whether the sheet should have a half expanded state.
     * If false, then the sheet gets only 2 states: hidden and expanded.
     * Default value is true.
     * @param isHalfExpandedInitially whether the sheet should be opened as half expanded initially.
     * This flag takes effect only if [isHalfExpandingEnabled] is true.
     * Default value is false.
     */
    class OpenLibrarySheet(
        val libraryCategory: LibraryCategory,
        val isFloating: Boolean = true,
        val isHalfExpandingEnabled: Boolean = true,
        val isHalfExpandedInitially: Boolean = false,
        val addToBackgroundTrack: Boolean = false,
    ) : Internal

    /**
     * An event for launching any contract via [ActivityResultContract] API.
     * IMPORTANT: Do not capture any values in the [onOutput]. In case the activity of the editor is killed
     * when returning back it may cause issues.
     *
     * @param contract the contract that should be launched.
     * @param input the input parameter of the contract.
     * @param onOutput the callback that is invoked when the contract returns.
     */
    class LaunchContract<I, O>(
        val contract: ActivityResultContract<I, O>,
        val input: I,
        val onOutput: EditorScope.(O) -> Unit,
    ) : Internal {
        var launched = false

        companion object {
            var current: LaunchContract<*, *>? = null
        }
    }

    /**
     * An event for adding a uri to the scene.
     * In addition, the uri will be transformed into an [ly.img.engine.AssetDefinition] and will be added to the asset source
     * represented by [uploadAssetSourceType].
     *
     * @param uploadAssetSourceType the asset source where [uri] should be added.
     * Check [ly.img.editor.core.library.data.AssetSourceType] for available [UploadAssetSourceType]s.
     * @param uri the uri which content should be added to the scene.
     * @param addToBackgroundTrack whether the content should be added to the background track. If false, it will be added
     * to the scene like a regular graphic block. This flag is only applicable for scenes with mode [ly.img.engine.SceneMode.VIDEO].
     */
    class AddUriToScene(
        val uploadAssetSourceType: UploadAssetSourceType,
        val uri: Uri,
        val addToBackgroundTrack: Boolean = false,
    ) : Internal

    /**
     * An event for replacing the content of the [designBlock] with uri content.
     * In addition, the uri will be transformed into an [ly.img.engine.AssetDefinition] and will be added to the asset source
     * represented by [uploadAssetSourceType].
     *
     * @param uploadAssetSourceType the asset source where [uri] should be added.
     * Check [ly.img.editor.core.library.data.AssetSourceType] for available [UploadAssetSourceType]s.
     * @param uri the uri which content should be added to the scene.
     * @param designBlock the design block which content should be replaced with the [uri].
     */
    class ReplaceUriAtScene(
        val uploadAssetSourceType: UploadAssetSourceType,
        val uri: Uri,
        val designBlock: DesignBlock,
    ) : Internal

    /**
     * An event for adding camera recordings to the scene.
     * In addition, the recordings will be transformed into [ly.img.engine.AssetDefinition]s and will be added to the asset source
     * represented by [uploadAssetSourceType].
     *
     * @param uploadAssetSourceType the asset source where [recordings] should be added.
     * @param recordings the list of the recordings.
     */
    class AddCameraRecordingsToScene(
        val uploadAssetSourceType: UploadAssetSourceType,
        val recordings: List<Pair<Uri, Duration>>,
    ) : Internal

    /**
     * An event for opening reorder sheet.
     * This event is only applicable for scenes with mode [ly.img.engine.SceneMode.VIDEO].
     *
     * @param isFloating whether the sheet should be floating. If true the sheet will be rendered over the editor. If false the
     * canvas will be zoomed to adjust the sheet.
     * Default value is false.
     */
    class OpenReorderSheet(val isFloating: Boolean = false) : Internal

    /**
     * An event for opening adjustments sheet.
     *
     * @param isFloating whether the sheet should be floating. If true the sheet will be rendered over the editor. If false the
     * canvas will be zoomed to adjust the sheet.
     * Default value is false.
     */
    class OpenAdjustmentsSheet(val isFloating: Boolean = false) : Internal

    /**
     * An event for opening filter sheet.
     *
     * @param isFloating whether the sheet should be floating. If true the sheet will be rendered over the editor. If false the
     * canvas will be zoomed to adjust the sheet.
     * Default value is false.
     */
    class OpenFilterSheet(val isFloating: Boolean = false) : Internal

    /**
     * An event for opening effect sheet.
     *
     * @param isFloating whether the sheet should be floating. If true the sheet will be rendered over the editor. If false the
     * canvas will be zoomed to adjust the sheet.
     * Default value is false.
     */
    class OpenEffectSheet(val isFloating: Boolean = false) : Internal

    /**
     * An event for opening blur sheet.
     *
     * @param isFloating whether the sheet should be floating. If true the sheet will be rendered over the editor. If false the
     * canvas will be zoomed to adjust the sheet.
     * Default value is false.
     */
    class OpenBlurSheet(val isFloating: Boolean = false) : Internal

    /**
     * An event for opening crop sheet.
     *
     * @param isFloating whether the sheet should be floating. If true the sheet will be rendered over the editor. If false the
     * canvas will be zoomed to adjust the sheet.
     * Default value is false.
     */
    class OpenCropSheet(val isFloating: Boolean = false) : Internal
}

/**
 * An interface for sending editor events that can be captured in [ly.img.editor.EditorConfiguration.onEvent].
 */
interface EditorEventHandler {
    /**
     * A function for sending [EditorEvent]s. If the event is an instance of [EditorEvent.Internal] then it will be handled
     * by the editor automatically. All other events are forwarded to [ly.img.editor.EditorConfiguration.onEvent].
     */
    fun send(event: EditorEvent)

    /**
     * A special function for closing the editor. This force closes the editor without entering the
     * [ly.img.editor.EngineConfiguration.onClose] callback.
     *
     * @param throwable an optional parameter in case the editor is closed due to an error.
     */
    @Deprecated(
        message = "Use EditorEventHandler.send(EditorEvent.CloseEditor(throwable) instead",
        replaceWith = ReplaceWith("send(EditorEvent.CloseEditor(throwable))"),
    )
    fun sendCloseEditorEvent(throwable: Throwable? = null) = send(EditorEvent.CloseEditor(throwable))

    /**
     * A special function for canceling the export job if it is running.
     */
    @Deprecated(
        message = "Use EditorEventHandler.send(EditorEvent.CancelExport() instead",
        replaceWith = ReplaceWith("send(EditorEvent.CancelExport())"),
    )
    fun sendCancelExportEvent() = send(EditorEvent.CancelExport())
}
