package ly.img.editor.postcard.rootbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.ui.IconTextButton

@Composable
fun RootBarItem(
    data: RootBarItemData,
    onEvent: (EditorEvent) -> Unit,
) {
    IconTextButton(
        onClick = { onEvent(EditorEvent.Sheet.Open(data.sheetType)) },
        text = stringResource(data.labelStringRes),
        editorIcon = data.icon,
    )
}
