package ly.img.editor.base.dock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ly.img.editor.core.ui.utils.ifTrue

@Composable
fun ConfigurableHeightContainer(
    minHeight: Dp = 0.0f.dp,
    maxHeightProvider: (Dp) -> Dp = { it / 2 },
    content: @Composable BoxScope.() -> Unit,
) {
    BoxWithConstraints {
        val maxHeight = remember { maxHeightProvider(maxHeight) }
        Box(Modifier.ifTrue(!LocalInspectionMode.current) { heightIn(max = maxHeight, min = minHeight) }) {
            content()
        }
    }
}
