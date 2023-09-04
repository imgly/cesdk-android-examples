package ly.img.cesdk.core.library.components.section

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.cesdk.core.R
import ly.img.cesdk.core.library.state.AssetSourceGroupType
import ly.img.cesdk.core.library.util.AssetLibraryUiConfig

@Composable
internal fun LibrarySectionErrorContent(assetSourceGroupType: AssetSourceGroupType) {
    Column {
        StringBox(assetSourceGroupType = assetSourceGroupType, id = R.string.cesdk_error_text)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StringBox(assetSourceGroupType: AssetSourceGroupType, @StringRes id: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(AssetLibraryUiConfig.contentRowHeight(assetSourceGroupType))
    ) {
        Text(
            stringResource(id = id),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}