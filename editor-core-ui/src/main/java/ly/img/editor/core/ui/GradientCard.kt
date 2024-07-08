package ly.img.editor.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ly.img.editor.compose.foundation.combinedClickable
import ly.img.editor.core.ui.utils.ifTrue

@Composable
fun GradientCard(
    modifier: Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    cornerRadius: Dp = 12.0.dp,
    content: @Composable (BoxScope.() -> Unit)? = null,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
    ) {
        val gradient =
            remember { Brush.linearGradient(listOf(Color(0x14FEFBFF), Color(0x141B1B1F))) }
        Box(
            modifier =
                Modifier
                    .background(Color(0x29ACAAAF))
                    .ifTrue(onClick != null) {
                        combinedClickable(
                            onClick = onClick ?: { },
                            onLongClick = onLongClick,
                        )
                    },
        ) {
            Box(modifier = Modifier.background(gradient)) {
                content?.invoke(this) ?: Spacer(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
