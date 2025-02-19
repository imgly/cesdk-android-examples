package ly.img.editor.showcases.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ly.img.editor.showcases.ShowcasesBuildConfig
import ly.img.editor.showcases.ui.preview.PreviewTheme

fun LazyListScope.versionFooterItem() = item { VersionFooter() }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VersionFooter(
    modifier: Modifier = Modifier,
    appVersion: String = ShowcasesBuildConfig.VERSION_NAME,
    sdkVersion: String = ShowcasesBuildConfig.ENGINE_VERSION,
) {
    Row(
        modifier =
            modifier
                .alpha(.4f)
                .fillMaxWidth()
                .padding(vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
    ) {
        Text(
            text = "App v$appVersion",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall,
            modifier =
                Modifier
                    .requiredWidthIn(max = 100.dp)
                    .basicMarquee(),
        )
        Box(
            modifier =
                Modifier
                    .size(2.dp)
                    .background(color = MaterialTheme.colorScheme.onSurface, shape = CircleShape),
        )
        Text(
            text = "SDK v$sdkVersion",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@PreviewLightDark
@Composable
private fun VersionFooterPreview() {
    PreviewTheme {
        VersionFooter(
            appVersion = "2025.30",
            sdkVersion = "1.45.1",
        )
    }
}

@PreviewLightDark
@Composable
private fun VersionFooterWithMarqueePreview() {
    PreviewTheme {
        VersionFooter(
            appVersion = "name/type-super-duper-extra-long-branch-name - 934t3459",
            sdkVersion = "1.45.1",
        )
    }
}
