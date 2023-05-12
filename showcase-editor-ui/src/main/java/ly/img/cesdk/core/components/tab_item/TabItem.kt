package ly.img.cesdk.core.components.tab_item

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.cesdk.Environment

@Composable
fun TabItem(
    onClick: () -> Unit,
    @StringRes textRes: Int,
    icon: TabIcon,
    tabIconMappings: TabIconMappings = Environment.tabIconMappings
) {
    TextButton(
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
        onClick = onClick,
        modifier = Modifier.widthIn(min = 80.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            tabIconMappings.TabIconContent(tabIcon = icon)
            Text(
                text = stringResource(id = textRes),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

interface TabIcon

// https://issuetracker.google.com/issues/252471936
abstract class TabIconComposable<T : TabIcon> {

    @Composable
    fun Content(icon: T) {
        IconContent(icon)
    }

    @Composable
    protected abstract fun IconContent(icon: T)
}