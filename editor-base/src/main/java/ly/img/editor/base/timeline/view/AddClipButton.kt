package ly.img.editor.base.timeline.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.base.R
import ly.img.editor.base.sheet.LibraryAddToBackgroundTrackSheetType
import ly.img.editor.base.ui.Event
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.iconpack.AddCameraBackground
import ly.img.editor.core.iconpack.AddGalleryBackground
import ly.img.editor.core.iconpack.IconPack
import ly.img.editor.core.iconpack.LibraryElements
import ly.img.editor.core.theme.surface3
import ly.img.editor.core.ui.library.LibraryViewModel
import ly.img.editor.core.ui.library.components.ClipMenuItem
import ly.img.editor.core.ui.library.resultcontract.GalleryMimeType
import ly.img.editor.core.ui.library.resultcontract.rememberGalleryLauncherForActivityResult

@Composable
fun AddClipButton(
    modifier: Modifier,
    onEvent: (EditorEvent) -> Unit,
) {
    var showClipMenu by remember { mutableStateOf(false) }
    val libraryViewModel = viewModel<LibraryViewModel>()
    Box(
        // zIndex of -1 ensures that the trim handles are drawn on top
        modifier = modifier.zIndex(-1f),
    ) {
        TimelineButton(
            id = R.string.ly_img_editor_add_clip,
            containerColor = MaterialTheme.colorScheme.surface3,
        ) {
            showClipMenu = true
        }
        DropdownMenu(
            expanded = showClipMenu,
            onDismissRequest = {
                showClipMenu = false
            },
        ) {
            // todo get rid of this in the future with mobile configuration extension
            var callback by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }
            callback?.invoke()
            callback = null

            ClipMenuItem(
                textResourceId = ly.img.editor.core.R.string.ly_img_editor_camera,
                icon = IconPack.AddCameraBackground,
            ) {
                showClipMenu = false
                onEvent(Event.OnVideoCameraClick { callback = it })
            }

            val galleryLauncher =
                rememberGalleryLauncherForActivityResult(addToBackgroundTrack = true) { event ->
                    showClipMenu = false
                    libraryViewModel.onEvent(event)
                }
            ClipMenuItem(
                textResourceId = ly.img.editor.core.R.string.ly_img_editor_gallery,
                icon = IconPack.AddGalleryBackground,
            ) {
                galleryLauncher.launch(GalleryMimeType.All)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            ClipMenuItem(
                textResourceId = R.string.ly_img_editor_library,
                icon = IconPack.LibraryElements,
            ) {
                showClipMenu = false
                onEvent(
                    EditorEvent.Sheet.Open(
                        LibraryAddToBackgroundTrackSheetType(
                            libraryCategory = libraryViewModel.assetLibrary.clips,
                        ),
                    ),
                )
            }
        }
    }
}
