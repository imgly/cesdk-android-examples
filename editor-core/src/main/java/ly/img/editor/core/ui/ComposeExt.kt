package ly.img.editor.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

inline fun Modifier.ifTrue(
    predicate: Boolean,
    builder: Modifier.() -> Modifier,
) = if (predicate) this.builder() else this

@Composable
fun Dp.toPx() = with(LocalDensity.current) { this@toPx.toPx() }
