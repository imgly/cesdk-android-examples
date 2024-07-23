package ly.img.editor.core.ui.tab_item

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.core.ui.ColorSchemeKeyToken
import ly.img.editor.core.ui.Environment
import ly.img.editor.core.ui.fromToken

@Composable
fun TabItem(
    onClick: () -> Unit,
    @StringRes textRes: Int,
    icon: TabIcon,
    enabled: Boolean = true,
    textColor: ColorSchemeKeyToken? = null,
    tabIconMappings: TabIconMappings = Environment.tabIconMappings,
) {
    Button(
        colors =
            ButtonDefaults.textButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5F),
            ),
        onClick = onClick,
        enabled = enabled,
        modifier =
            Modifier
                .widthIn(min = 64.dp)
                .height(64.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 8.dp, start = 4.dp, end = 4.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 4.dp),
        ) {
            tabIconMappings.TabIconContent(tabIcon = icon)
            Text(
                text = stringResource(id = textRes),
                color = if (textColor == null) Color.Unspecified else MaterialTheme.colorScheme.fromToken(textColor),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp),
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
