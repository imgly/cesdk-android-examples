package ly.img.camera.components.sidemenu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import ly.img.camera.components.sidemenu.timer.TimerMenuItem
import ly.img.camera.record.Timer

@Composable
internal fun SideMenu(
    modifier: Modifier = Modifier,
    timer: Timer,
    setTimer: (Timer) -> Unit,
) {
    Column(
        modifier = modifier.width(IntrinsicSize.Max),
    ) {
        var expanded by remember { mutableStateOf(true) }
        var itemInteractedToggle by remember { mutableStateOf(false) }

        LaunchedEffect(itemInteractedToggle) {
            if (itemInteractedToggle) return@LaunchedEffect
            delay(2000)
            expanded = false
        }

        fun onItemClick() {
            itemInteractedToggle = true
            expanded = true
        }

        fun onDropdownMenuDismiss() {
            itemInteractedToggle = false
        }

        TimerMenuItem(
            timer = timer,
            expanded = expanded,
            onClick = ::onItemClick,
            onDropdownMenuDismiss = ::onDropdownMenuDismiss,
            setTimer = setTimer,
        )
    }
}
