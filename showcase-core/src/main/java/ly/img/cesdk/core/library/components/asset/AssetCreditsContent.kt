package ly.img.cesdk.core.library.components.asset

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ly.img.cesdk.core.R
import ly.img.cesdk.core.iconpack.Close
import ly.img.cesdk.core.iconpack.IconPack
import ly.img.cesdk.core.library.util.LibraryUiEvent
import ly.img.cesdk.core.ui.HyperlinkText
import ly.img.cesdk.core.ui.SheetHeader

@Composable
internal fun AssetCreditsContent(
    assetCreditsEvent: LibraryUiEvent.ShowAssetCredits,
    onCloseAssetDetails: () -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))
        SheetHeader(
            title = stringResource(id = R.string.cesdk_credits_title),
            onClose = onCloseAssetDetails,
            icon = IconPack.Close
        )
        Column(Modifier.padding(top = 8.dp, bottom = 32.dp)) {
            Text(
                assetCreditsEvent.assetLabel,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            val creditsLabel = if (assetCreditsEvent.assetCredits != null && assetCreditsEvent.assetSourceCredits != null) {
                stringResource(
                    R.string.cesdk_credits_artist_on_source,
                    assetCreditsEvent.assetCredits.name,
                    assetCreditsEvent.assetSourceCredits.name
                )
            } else if (assetCreditsEvent.assetCredits != null) {
                stringResource(
                    R.string.cesdk_credits_artist,
                    assetCreditsEvent.assetCredits.name
                )
            } else if (assetCreditsEvent.assetSourceCredits != null) {
                stringResource(
                    R.string.cesdk_credits_on_source,
                    assetCreditsEvent.assetSourceCredits.name
                )
            } else null

            if (creditsLabel != null) {
                HyperlinkText(
                    fullText = creditsLabel,
                    hyperLinks = mutableMapOf(
                        assetCreditsEvent.assetCredits?.name to assetCreditsEvent.assetCredits?.resourcePath,
                        assetCreditsEvent.assetSourceCredits?.name to assetCreditsEvent.assetSourceCredits?.resourcePath
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            val licenseLabel = if (assetCreditsEvent.assetLicense != null) {
                assetCreditsEvent.assetLicense.name
            } else if (assetCreditsEvent.assetSourceLicense != null) {
                assetCreditsEvent.assetSourceLicense.name
            } else null

            if (licenseLabel != null) {
                Divider()
                HyperlinkText(
                    fullText = licenseLabel,
                    hyperLinks = mutableMapOf(
                        assetCreditsEvent.assetLicense?.name to assetCreditsEvent.assetLicense?.resourcePath,
                        assetCreditsEvent.assetSourceLicense?.name to assetCreditsEvent.assetSourceLicense?.resourcePath
                    ),
                    textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }

    BackHandler {
        onCloseAssetDetails()
    }
}