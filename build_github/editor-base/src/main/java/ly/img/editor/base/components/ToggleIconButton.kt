package ly.img.editor.base.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ToggleIconButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    FilledIconToggleButton(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors =
            IconButtonDefaults.filledIconToggleButtonColors(
                containerColor = Color.Transparent,
                contentColor = LocalContentColor.current,
                disabledContainerColor = Color.Transparent,
            ),
        enabled = enabled,
        content = content,
    )
}

@Composable
fun ToggleIconButton(
    modifier: Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    checkedContainerColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable () -> Unit,
) {
    Surface(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        shape = IconButtonDefaults.filledShape,
        color =
            when {
                enabled && checked -> checkedContainerColor
                else -> Color.Transparent
            },
        contentColor =
            when {
                enabled && checked -> MaterialTheme.colorScheme.onPrimary
                else -> LocalContentColor.current
            },
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}
