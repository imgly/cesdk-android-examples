package ly.img.cesdk.components.color_picker

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ly.img.cesdk.core.ui.bottomsheet.ModalBottomSheetState
import ly.img.cesdk.core.ui.bottomsheet.ModalBottomSheetValue
import ly.img.cesdk.core.iconpack.Add
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.editorui.Event
import ly.img.cesdk.editorui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryButton(
    modifier: Modifier,
    uiScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    onEvent: (Event) -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = {
            uiScope.launch {
                onEvent(Event.OnAddLibraryClick)
                // FIXME: For reasons not completely clear, without this delay,
                //  the bottom sheet doesn't completely expands and only half expands.
                delay(16)
                bottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
            }
        }
    ) {
        Icon(IconPack.Add, stringResource(R.string.cesdk_library))
    }
}