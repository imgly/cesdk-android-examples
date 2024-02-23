package ly.img.editor.base.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.core.theme.surface1
import ly.img.editor.core.ui.iconpack.Arrowdropdown
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.utils.ifTrue

@Composable
fun PropertyPicker(
    title: String,
    @StringRes propertyTextRes: Int,
    enabled: Boolean = true,
    properties: List<Property>,
    onPropertyPicked: (String) -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface1)
                .ifTrue(enabled) {
                    clickable { showMenu = true }
                }
                .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp),
        )
        CompositionLocalProvider(
            LocalContentColor provides
                if (enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                },
        ) {
            Row(
                modifier = Modifier.padding(ButtonDefaults.TextButtonContentPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(propertyTextRes),
                    modifier = Modifier.padding(horizontal = 10.dp),
                    style = MaterialTheme.typography.labelLarge,
                )
                Box {
                    Icon(IconPack.Arrowdropdown, contentDescription = null)
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                    ) {
                        properties.forEach {
                            CheckedTextRow(
                                isChecked = it.textRes == propertyTextRes,
                                text = stringResource(it.textRes),
                                icon = it.icon,
                                onClick = {
                                    onPropertyPicked(it.value)
                                    showMenu = false
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

data class Property(
    @StringRes val textRes: Int,
    val value: String,
    val icon: ImageVector? = null,
)
