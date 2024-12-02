package ly.img.editor.core.event

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import ly.img.editor.core.EditorScope
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.sheet.SheetType
import ly.img.engine.DesignBlock
import kotlin.time.Duration

/**
 * An editor event that can be sent via [EditorEventHandler]. All events are forwarded to [ly.img.editor.EditorConfiguration.onEvent],
 * however if the event is an instance of [EditorEvent.Internal] then it is already handled internally. These events, however, can
 * be useful to update your state, do action tracking etc.
 * All event classes that are declared inside [EditorEvent] are internal i.e. [EditorEvent.CloseEditor], [EditorEvent.Sheet.Open].
 */
interface EditorEvent {
    /**
     * An editor event that can be sent via [EditorEventHandler] and handled internally by the editor.
     * Internal events are still forwarded to [ly.img.editor.EditorConfiguration.onEvent].
     */
    sealed interface Internal : EditorEvent

    /**
     * All sheet related events.
     */
    class Sheet {
        /**
         * An event for opening a new sheet.
         */
        class Open(
            val type: SheetType,
        ) : Internal

        /**
         * An event for expanding the sheet that is currently open.
         */
        class Expand(
            val animate: Boolean,
        ) : Internal

        /**
         * An event for half expanding the sheet that is currently open.
         */
        class HalfExpand(
            val animate: Boolean,
        ) : Internal

        /**
         * An event for closing the sheet that is currently open.
         */
        class Close(
            val animate: Boolean,
        ) : Internal

        /**
         * An event that is emitted when the sheet is fully expanded after calling [Expand] or the user manually does it.
         */
        class OnExpanded(
            val type: SheetType,
        ) : Internal

        /**
         * An event that is emitted when the sheet is fully expanded after calling [HalfExpand] or the user manually does it.
         */
        class OnHalfExpanded(
            val type: SheetType,
        ) : Internal

        /**
         * An event that is emitted when the sheet is fully expanded after calling [Close] or the user manually does it.
         */
        class OnClosed(
            val type: SheetType,
        ) : Internal
    }

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
     */
    class AddUriToScene(
        val uploadAssetSourceType: UploadAssetSourceType,
        val uri: Uri,
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
