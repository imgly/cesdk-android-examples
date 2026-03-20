package ly.img.editor.showcases.plugin.imagegen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.EditorComponentId
import ly.img.editor.showcases.R
import ly.img.editor.showcases.icon.DockCreateWithAi
import ly.img.editor.showcases.icon.IconPack
import ly.img.editor.showcases.plugin.imagegen.utils.createTextToImageSheetOpenEvent

@Composable
fun Dock.Button.Companion.rememberCreateWithAIDockButton(apiConfig: String): Dock.Button {
    val scope = rememberCoroutineScope()
    return Dock.Button.remember(
        id = EditorComponentId("ly.img.component.dock.button.textToImage"),
        icon = {
            Icon(
                imageVector = IconPack.DockCreateWithAi,
                contentDescription = stringResource(R.string.ly_img_showcases_dock_generate_image_content_description),
                modifier = Modifier.size(24.dp),
            )
        },
        text = {
            Text(
                stringResource(R.string.ly_img_showcases_dock_generate),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp),
            )
        },
        onClick = {
            editorContext.eventHandler.send(
                event = createTextToImageSheetOpenEvent(
                    apiConfig = apiConfig,
                    coroutineScope = scope,
                ),
            )
        },
    )
}
