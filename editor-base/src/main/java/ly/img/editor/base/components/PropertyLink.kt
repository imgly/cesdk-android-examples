package ly.img.editor.base.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ly.img.editor.core.ui.iconpack.Arrowright
import ly.img.editor.core.ui.iconpack.IconPack

@Composable
fun PropertyLink(
    title: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 16.dp))
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.primary,
        ) {
            Row(
                modifier = Modifier.padding(ButtonDefaults.TextButtonContentPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (value != null) {
                    Text(
                        value, modifier = Modifier.padding(horizontal = 10.dp), style = MaterialTheme.typography.labelLarge
                    )
                }
                Icon(IconPack.Arrowright, contentDescription = null)
            }
        }
    }
}