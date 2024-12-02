package ly.img.editor.core.ui.library.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.compose.foundation.clickable

@Composable
fun ClipMenuItem(
    @StringRes textResourceId: Int,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(
                text = stringResource(id = textResourceId),
                modifier =
                    Modifier
                        .widthIn(min = 96.dp)
                        .offset(x = (-4).dp),
            )
        },
        trailingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.offset(x = 8.dp),
            )
        },
    )
}
