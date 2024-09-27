package ly.img.editor.base.timeline.clip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import ly.img.editor.core.theme.surface1

@Composable
fun ClipOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .alpha(0.75f)
                .background(MaterialTheme.colorScheme.surface1),
    )
}
