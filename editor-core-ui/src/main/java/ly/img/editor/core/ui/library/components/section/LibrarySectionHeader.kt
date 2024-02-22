package ly.img.editor.core.ui.library.components.section

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ly.img.editor.core.R
import ly.img.editor.core.library.LibraryContent
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.iconpack.Add
import ly.img.editor.core.ui.iconpack.Arrowright
import ly.img.editor.core.ui.iconpack.IconPack

@Composable
internal fun LibrarySectionHeader(
    item: LibrarySectionItem.Header,
    onDrillDown: (LibraryContent) -> Unit,
    onUriPick: (UploadAssetSourceType, Uri) -> Unit,
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

        val uploadAssetSource = item.uploadAssetSource
        if (uploadAssetSource != null) {
            UploadButton(
                onUriPick = {
                    onUriPick(uploadAssetSource, it)
                },
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
    mimeTypeFilter: String,
    modifier: Modifier,
) {
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                onUriPick(it)
            }
        }
    TextButton(
        modifier = modifier,
        onClick = {
            launcher.launch(mimeTypeFilter)
        },
    ) {
        Icon(IconPack.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        Text(
            text = stringResource(R.string.ly_img_editor_add),
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
