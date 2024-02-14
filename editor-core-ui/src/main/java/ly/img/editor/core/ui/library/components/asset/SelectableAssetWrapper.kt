package ly.img.editor.core.ui.library.components.asset

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ly.img.editor.core.ui.utils.ifTrue

@Composable
internal fun SelectableAssetWrapper(
    isSelected: Boolean,
    selectedIcon: ImageVector?,
    selectedIconTint: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        Modifier.ifTrue(isSelected) {
            border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.large)
        }.padding(4.dp).clip(MaterialTheme.shapes.medium),
    ) {
        content()
        if (isSelected && selectedIcon != null) {
            Box(
                Modifier.matchParentSize().background(color = Color(0x66000000)),
            ) { /* EMPTY BOX AS OVERLAY */ }
            Icon(
                selectedIcon,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(32.dp)
                        .align(Alignment.Center),
                tint = selectedIconTint,
            )
        }
    }
}
