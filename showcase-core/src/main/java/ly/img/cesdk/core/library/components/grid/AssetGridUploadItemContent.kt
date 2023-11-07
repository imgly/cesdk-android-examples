package ly.img.cesdk.core.library.components.grid

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.cesdk.core.R
import ly.img.cesdk.core.data.UploadAssetSource
import ly.img.cesdk.core.iconpack.Add
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.library.state.AssetSourceGroupType
import ly.img.cesdk.core.ui.GradientCard

@Composable
internal fun AssetGridUploadItemContent(
    uploadAssetSource: UploadAssetSource,
    assetSourceGroupType: AssetSourceGroupType,
    onUriPick: (UploadAssetSource, Uri) -> Unit,
) {
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { onUriPick(uploadAssetSource, it) }
        }
    if (assetSourceGroupType == AssetSourceGroupType.Audio) {
        TODO()
    } else {
        GradientCard(
            modifier = Modifier.aspectRatio(1f),
            onClick = {
                launcher.launch(uploadAssetSource.mimeTypeFilter)
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = IconPack.Add,
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.padding(vertical = 2.dp),
                    text = stringResource(R.string.cesdk_add),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}