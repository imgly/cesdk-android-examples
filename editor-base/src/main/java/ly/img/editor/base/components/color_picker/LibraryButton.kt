package ly.img.editor.base.components.color_picker

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ly.img.editor.base.R
import ly.img.editor.base.ui.Event
import ly.img.editor.compose.bottomsheet.ModalBottomSheetState
import ly.img.editor.compose.bottomsheet.ModalBottomSheetValue
import ly.img.editor.core.ui.iconpack.Add
import ly.img.editor.core.ui.iconpack.IconPack

@Composable
fun LibraryButton(
    modifier: Modifier,
    uiScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    onEvent: (Event) -> Unit,
) {
    FloatingActionButton(
        modifier = modifier.testTag(tag = "LibraryButton"),
        onClick = {
            uiScope.launch {
                onEvent(Event.OnAddLibraryClick)
                // FIXME: For reasons not completely clear, without this delay,
                //  the bottom sheet doesn't completely expands and only half expands.
                delay(16)
                bottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
            }
        },
    ) {
        Icon(IconPack.Add, stringResource(R.string.ly_img_editor_library))
    }
}
