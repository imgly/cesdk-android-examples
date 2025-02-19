@file:OptIn(ExperimentalMaterial3Api::class)

package ly.img.cesdk.apparel

import androidx.compose.material3.ExperimentalMaterial3Api
import ly.img.cesdk.core.components.bottomsheet.ModalBottomSheetValue
import java.io.File

/**
 * To communicate one-time events from the ViewModel to the UI. These are not part of the state.
 */
sealed interface SingleEvent {
    data class ShareExport(val file: File) : SingleEvent
    object Exit : SingleEvent
    data class ChangeSheetState(val state: ModalBottomSheetValue, val animate: Boolean = true) : SingleEvent
}