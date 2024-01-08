@file:OptIn(ExperimentalMaterial3Api::class)

package ly.img.editor.base.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import ly.img.editor.core.ui.bottomsheet.ModalBottomSheetValue
import java.io.File

/**
 * To communicate one-time events from the ViewModel to the UI. These are not part of the state.
 */
sealed interface SingleEvent {
    data class ShareExport(val file: File) : SingleEvent
    data object Exit : SingleEvent
    data class ChangeSheetState(val state: ModalBottomSheetValue, val animate: Boolean = true) : SingleEvent
    data object HideScrimSheet : SingleEvent
}