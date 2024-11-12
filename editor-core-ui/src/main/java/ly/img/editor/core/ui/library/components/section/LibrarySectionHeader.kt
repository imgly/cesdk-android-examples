package ly.img.editor.core.ui.library.components.section

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ly.img.editor.core.R
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.iconpack.Add
import ly.img.editor.core.ui.iconpack.Addcamerabackground
import ly.img.editor.core.ui.iconpack.Arrowright
import ly.img.editor.core.ui.iconpack.IconPack
import ly.img.editor.core.ui.iconpack.Photolibraryoutline
import ly.img.editor.core.ui.iconpack.Videolibraryoutline
import ly.img.editor.core.ui.library.components.ClipMenuItem

@Composable
internal fun LibrarySectionHeader(
    item: LibrarySectionItem.Header,
    onDrillDown: (LibraryContent) -> Unit,
    onUriPick: (UploadAssetSourceType, Uri) -> Unit,
    launchCamera: (Boolean, (Uri) -> Unit) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .padding(start = 16.dp, end = 8.dp)
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = item.titleRes),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        val uploadAssetSource = item.uploadAssetSourceType
        if (uploadAssetSource != null) {
            UploadButton(
                onUriPick = {
                    onUriPick(uploadAssetSource, it)
                },
                launchCamera = launchCamera,
                mimeTypeFilter = uploadAssetSource.mimeTypeFilter,
                modifier = Modifier.padding(end = 8.dp),
            )
        }

        if (item.expandContent != null) {
            TextButton(
                onClick = { onDrillDown(item.expandContent) },
            ) {
                val countText =
                    item.count?.let { count ->
                        if (count > 999) stringResource(id = R.string.ly_img_editor_more_count) else count.toString()
                    } ?: ""
                Text(
                    text = countText,
                    style = MaterialTheme.typography.labelLarge,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(IconPack.Arrowright, contentDescription = null)
            }
        }
    }
}

@Composable
private fun UploadButton(
    onUriPick: (Uri) -> Unit,
    launchCamera: (Boolean, (Uri) -> Unit) -> Unit,
    mimeTypeFilter: String,
    modifier: Modifier,
) {
    val isAudioMimeType = mimeTypeFilter.isAudioMimeType()
    var showUploadMenu by remember { mutableStateOf(false) }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                showUploadMenu = false
                onUriPick(it)
            }
        }

    Box {
        TextButton(
            modifier = modifier,
            onClick = {
                if (isAudioMimeType) {
                    launcher.launch(mimeTypeFilter)
                } else {
                    showUploadMenu = true
                }
            },
        ) {
            Icon(IconPack.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(
                text = stringResource(R.string.ly_img_editor_add),
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.labelLarge,
            )
        }

        if (!isAudioMimeType) {
            DropdownMenu(
                expanded = showUploadMenu,
                onDismissRequest = {
                    showUploadMenu = false
                },
            ) {
                val isVideoMimeType = mimeTypeFilter.isVideoMimeType()
                ClipMenuItem(
                    textResourceId = if (isVideoMimeType) R.string.ly_img_editor_choose_video else R.string.ly_img_editor_choose_photo,
                    icon = if (isVideoMimeType) IconPack.Videolibraryoutline else IconPack.Photolibraryoutline,
                ) {
                    launcher.launch(mimeTypeFilter)
                }
                ClipMenuItem(
                    textResourceId = if (isVideoMimeType) R.string.ly_img_editor_take_video else R.string.ly_img_editor_take_photo,
                    icon = IconPack.Addcamerabackground,
                    onClick = {
                        launchCamera(isVideoMimeType, onUriPick)
                        showUploadMenu = false
                    },
                )
            }
        }
    }
}

private fun String.isAudioMimeType() = startsWith("audio")

private fun String.isVideoMimeType() = startsWith("video")
