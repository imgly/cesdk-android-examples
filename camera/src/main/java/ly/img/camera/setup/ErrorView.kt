package ly.img.camera.setup

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Warning
import ly.img.engine.EngineException

@Composable
internal fun ErrorView(
    error: Throwable,
    onClose: () -> Unit,
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
    ) {
        Icon(
            IconPack.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))
        val errorMessage = if (error is EngineException) error.message else null
        Text(
            text = errorMessage ?: stringResource(ly.img.editor.core.R.string.ly_img_editor_error_text),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
    Spacer(modifier = Modifier.height(56.dp))
    FilledTonalButton(
        onClick = onClose,
    ) {
        Text(text = stringResource(ly.img.editor.core.R.string.ly_img_editor_close))
    }
}
