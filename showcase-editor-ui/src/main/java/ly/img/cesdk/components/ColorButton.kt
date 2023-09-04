package ly.img.cesdk.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ly.img.cesdk.engine.SolidFill

@Composable
fun ColorButton(
    color: Color?,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    punchHole: Boolean = false,
    onClick: (() -> Unit)? = null,
    buttonSize: Dp = 40.dp,
    selectionStrokeWidth: Dp = 2.dp,
) = FillButton(fill = remember(color){ color?.let { SolidFill(color) }}, modifier, selected, punchHole, onClick, buttonSize, selectionStrokeWidth)