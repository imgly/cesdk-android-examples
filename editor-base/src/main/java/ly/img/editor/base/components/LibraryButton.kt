package ly.img.editor.base.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import ly.img.editor.base.R
import ly.img.editor.base.ui.Event
import ly.img.editor.core.ui.iconpack.Add
import ly.img.editor.core.ui.iconpack.IconPack

@Composable
fun LibraryButton(
    modifier: Modifier,
    onEvent: (Event) -> Unit,
) {
    FloatingActionButton(
        modifier = modifier.testTag(tag = "LibraryButton"),
        onClick = {
            onEvent(Event.OnAddLibraryClick)
        },
    ) {
        Icon(IconPack.Add, stringResource(R.string.ly_img_editor_library))
    }
}
