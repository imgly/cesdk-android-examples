package ly.img.editor.base.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import ly.img.editor.base.R
import ly.img.editor.base.sheet.LibraryTabsSheetType
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.ui.iconpack.Add
import ly.img.editor.core.ui.iconpack.IconPack

@Composable
fun LibraryButton(
    modifier: Modifier,
    onEvent: (EditorEvent) -> Unit,
) {
    FloatingActionButton(
        modifier = modifier.testTag(tag = "LibraryButton"),
        onClick = {
            onEvent(EditorEvent.Sheet.Open(LibraryTabsSheetType()))
        },
    ) {
        Icon(IconPack.Add, stringResource(R.string.ly_img_editor_library))
    }
}
