package ly.img.editor

import ly.img.editor.core.event.EditorEvent
import ly.img.engine.MimeType
import java.io.File

/**
 * An event that is invoked in the default implementations of [EngineConfiguration.onCreate] and [EngineConfiguration.onExport]
 * for showing a loading overlay while load/export operations are running.
 */
object ShowLoading : EditorEvent

/**
 * An event that is invoked for hiding the loading overlay previously triggered by the [ShowLoading] event.
 */
object HideLoading : EditorEvent

/**
 * An event that is invoked in the default implementations of [EngineConfiguration.onError] for showing an error dialog.
 */
class ShowErrorDialogEvent(val error: Throwable) : EditorEvent

/**
 * An event that is invoked for hiding the error dialog previously triggered by the [ShowErrorDialogEvent] event.
 */
object HideErrorDialogEvent : EditorEvent

/**
 * An event that is invoked in the default implementation of [EngineConfiguration.onExport] after the export is done.
 * By default, the event is captured in [EditorConfiguration.onEvent] and a system level popup is displayed to share the [file]
 * that was exported.
 */
class ShareFileEvent(val file: File, val mimeType: String = MimeType.PDF.key) : EditorEvent

/**
 * An event that is invoked in the default implementations of [EngineConfiguration.onClose] for showing a confirmation dialog
 * when there are unsaved changes in the editor.
 */
object ShowCloseConfirmationDialogEvent : EditorEvent

/**
 * An event that is invoked for hiding the confirmation dialog previously triggered by the [ShowCloseConfirmationDialogEvent] event.
 */
object DismissCloseConfirmationDialogEvent : EditorEvent
