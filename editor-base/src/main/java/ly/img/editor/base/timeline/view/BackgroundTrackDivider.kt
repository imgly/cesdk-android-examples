package ly.img.editor.base.timeline.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import ly.img.editor.base.timeline.state.TimelineConfiguration

@Composable
fun BackgroundTrackDivider(modifier: Modifier) {
    Box(
        modifier =
            modifier
                .height(TimelineConfiguration.backgroundTrackDividerHeight)
                .fillMaxWidth()
                .alpha(0.5f)
                .background(MaterialTheme.colorScheme.outlineVariant),
    )
}
