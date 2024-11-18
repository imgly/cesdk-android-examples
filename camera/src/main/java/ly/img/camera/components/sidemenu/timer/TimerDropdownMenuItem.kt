package ly.img.camera.components.sidemenu.timer

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ly.img.camera.record.Timer
import ly.img.editor.core.ui.iconpack.Check
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.utils.ifTrue

@Composable
internal fun TimerDropdownMenuItem(
    timer: Timer,
    currentTimer: Timer,
    onClick: (Timer) -> Unit,
) {
    val checked = timer == currentTimer
    DropdownMenuItem(
        modifier =
            Modifier.ifTrue(checked) {
                background(MaterialTheme.colorScheme.surfaceVariant)
            },
        text = { Text(stringResource(id = timer.label)) },
        onClick = {
            onClick(timer)
        },
        leadingIcon = {
            if (checked) {
                Icon(IconPack.Check, contentDescription = null)
            }
        },
        trailingIcon = { Icon(timer.icon, contentDescription = null) },
    )
}
