package ly.img.camera.components.sidemenu.timer

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ly.img.camera.R
import ly.img.camera.components.sidemenu.SideMenuItem
import ly.img.camera.record.Timer

@Composable
internal fun TimerMenuItem(
    timer: Timer,
    expanded: Boolean,
    onClick: () -> Unit,
    onDropdownMenuDismiss: () -> Unit,
    setTimer: (Timer) -> Unit,
) {
    var isTimerSelected by remember { mutableStateOf(false) }

    SideMenuItem(
        imageVector = timer.icon,
        contentDescription = R.string.ly_img_camera_timer,
        label = if (timer != Timer.Off) timer.label else R.string.ly_img_camera_timer,
        checked = isTimerSelected || timer != Timer.Off,
        expanded = expanded,
        onClick = {
            isTimerSelected = true
            onClick()
        },
    )

    fun onDropdownMenuItemClick(timer: Timer) {
        isTimerSelected = false
        setTimer(timer)
        onDropdownMenuDismiss()
    }

    DropdownMenu(
        expanded = isTimerSelected,
        onDismissRequest = {
            isTimerSelected = false
            onDropdownMenuDismiss()
        },
    ) {
        val timers = listOf(Timer.Three, Timer.Ten, Timer.Off)
        timers.forEachIndexed { index, item ->
            if (index == timers.size - 1) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            TimerDropdownMenuItem(
                timer = item,
                currentTimer = timer,
                onClick = ::onDropdownMenuItemClick,
            )
        }
    }
}
