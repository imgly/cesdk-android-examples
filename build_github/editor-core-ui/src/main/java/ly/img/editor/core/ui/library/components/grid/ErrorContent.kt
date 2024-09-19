package ly.img.editor.core.ui.library.components.grid

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.core.R
import ly.img.editor.core.ui.iconpack.Erroroutline
import ly.img.editor.core.ui.iconpack.IconPack

@Composable
internal fun ErrorContent(onRetryClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
        ) {
            Icon(IconPack.Erroroutline, contentDescription = null)
            Text(
                stringResource(R.string.ly_img_editor_error_text),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
            )
        }
        Button(onClick = onRetryClick) {
            Text(text = stringResource(R.string.ly_img_editor_retry))
        }
    }
}
