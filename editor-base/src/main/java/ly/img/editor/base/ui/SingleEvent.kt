package ly.img.editor.base.ui

import ly.img.editor.compose.bottomsheet.ModalBottomSheetValue

/**
 * To communicate one-time events from the ViewModel to the UI. These are not part of the state.
 */
sealed interface SingleEvent {
    data object Exit : SingleEvent

    data class ChangeSheetState(val state: ModalBottomSheetValue, val animate: Boolean = true) : SingleEvent

    data object HideScrimSheet : SingleEvent
}
