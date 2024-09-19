package ly.img.editor.core.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.toPx() = with(LocalDensity.current) { this@toPx.toPx() }

@Composable
fun Dp.roundToPx() = with(LocalDensity.current) { this@roundToPx.roundToPx() }

@Composable
fun Float.toDp() = with(LocalDensity.current) { this@toDp.toDp() }
