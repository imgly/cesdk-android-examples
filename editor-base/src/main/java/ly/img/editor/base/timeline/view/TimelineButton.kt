package ly.img.editor.base.timeline.view

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.base.timeline.state.TimelineConfiguration
import ly.img.editor.core.ui.iconpack.Add
import ly.img.editor.core.ui.iconpack.IconPack

@Composable
fun TimelineButton(
    @StringRes id: Int,
    modifier: Modifier = Modifier,
    icon: ImageVector = IconPack.Add,
    containerColor: Color = Color.Transparent,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier.height(TimelineConfiguration.clipHeight),
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = containerColor,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        contentPadding = ButtonDefaults.TextButtonWithIconContentPadding,
        border =
            ButtonDefaults.outlinedButtonBorder.copy(
                brush = SolidColor(MaterialTheme.colorScheme.outlineVariant),
            ),
        shape = MaterialTheme.shapes.small,
        onClick = onClick,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            stringResource(id = id),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
