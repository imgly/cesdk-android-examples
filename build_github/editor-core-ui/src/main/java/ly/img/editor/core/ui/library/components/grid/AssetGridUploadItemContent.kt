package ly.img.editor.core.ui.library.components.grid

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.editor.core.R
import ly.img.editor.core.library.AssetType
import ly.img.editor.core.library.data.UploadAssetSourceType
import ly.img.editor.core.ui.GradientCard
import ly.img.editor.core.ui.iconpack.Add
import ly.img.editor.core.ui.iconpack.IconPack

@Composable
internal fun AssetGridUploadItemContent(
    uploadAssetSource: UploadAssetSourceType,
    assetType: AssetType,
    onUriPick: (UploadAssetSourceType, Uri) -> Unit,
) {
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let { onUriPick(uploadAssetSource, it) }
        }
    if (assetType == AssetType.Audio) {
        ListItem(
            headlineContent = { Text(stringResource(R.string.ly_img_editor_add)) },
            leadingContent = {
                GradientCard(Modifier.size(56.dp)) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Icon(
                            modifier = Modifier.align(Alignment.Center),
                            imageVector = IconPack.Add,
                            contentDescription = null,
                        )
                    }
                }
            },
            modifier =
                Modifier.clickable {
                    launcher.launch(uploadAssetSource.mimeTypeFilter)
                },
        )
    } else {
        GradientCard(
            modifier = Modifier.aspectRatio(1f),
            onClick = {
                launcher.launch(uploadAssetSource.mimeTypeFilter)
            },
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = IconPack.Add,
                    contentDescription = null,
                )
                Text(
                    modifier = Modifier.padding(vertical = 2.dp),
                    text = stringResource(R.string.ly_img_editor_add),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    }
}
