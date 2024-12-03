package ly.img.editor.postcard.rootbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ly.img.editor.base.ui.Event
import ly.img.editor.core.ui.IconTextButton
import ly.img.editor.postcard.PostcardEvent

@Composable
fun RootBarItem(
    data: RootBarItemData,
    onEvent: (Event) -> Unit,
) {
    IconTextButton(
        onClick = { onEvent(PostcardEvent.OnRootBarItemClick(data.type)) },
        text = stringResource(data.labelStringRes),
        editorIcon = data.icon,
    )
}
