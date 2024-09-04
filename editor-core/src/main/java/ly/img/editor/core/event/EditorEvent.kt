package ly.img.editor.core.event

/**
 * An editor event that can be sent via [EditorEventHandler] and can be captured in [ly.img.editor.EditorConfiguration.onEvent].
 */
interface EditorEvent

/**
 * An interface for sending editor events that can be captured in [ly.img.editor.EditorConfiguration.onEvent].
 */
interface EditorEventHandler {
    /**
     * A function for sending [EditorEvent]s,
     */
    fun send(event: EditorEvent)

    /**
     * A special function for closing the editor. This force closes the editor without entering the
     * [ly.img.editor.EngineConfiguration.onClose] callback.
     *
     * @param throwable an optional parameter in case the editor is closed due to an error.
     */
    fun sendCloseEditorEvent(throwable: Throwable? = null)
}
