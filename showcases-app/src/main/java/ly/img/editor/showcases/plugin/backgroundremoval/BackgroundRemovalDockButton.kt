package ly.img.editor.showcases.plugin.backgroundremoval

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.core.ui.engine.getCurrentPage
import ly.img.editor.showcases.R
import ly.img.editor.showcases.icon.CustomFunctionalitiesBackgroundRemoval
import ly.img.editor.showcases.icon.IconPack
import ly.img.editor.showcases.plugin.backgroundremoval.api.BackgroundRemovalApi.removeBackground

@Composable
fun Dock.Companion.rememberBackgroundRemovalDockButton(): Dock.Button {
    val scope = rememberCoroutineScope()
    var currentJob: Job? by remember { mutableStateOf(null) }

    return Dock.Button.remember(
        id = EditorComponentId("ly.img.component.dock.button.backgroundRemoval"),
        icon = {
            Icon(
                imageVector = IconPack.CustomFunctionalitiesBackgroundRemoval,
                contentDescription = stringResource(R.string.ly_img_showcases_dock_remove_background),
                modifier = Modifier.size(24.dp),
            )
        },
        text = {
            Text(
                stringResource(R.string.ly_img_showcases_dock_remove_background),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp),
            )
        },
        onClick = {
            currentJob?.cancel()
            currentJob = scope.launch {
                try {
                    removeBackground(editorContext.engine.getCurrentPage())
                } catch (e: Exception) {
                    Log.e("BackgroundRemovalPlugin", "Failed to remove background: ${e.message}", e)
                } finally {
                    currentJob = null
                }
            }
        },
    )
}
