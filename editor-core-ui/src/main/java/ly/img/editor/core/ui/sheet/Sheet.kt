package ly.img.editor.core.ui.sheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Dp
import ly.img.editor.core.component.data.Height
import ly.img.editor.core.sheet.SheetStyle
import ly.img.editor.core.ui.utils.ifTrue

@Composable
inline fun Sheet(
    style: SheetStyle,
    crossinline content: @Composable () -> Unit,
) {
    BoxWithConstraints {
        val maxHeight =
            remember(style) {
                when (val maxHeight = style.maxHeight) {
                    is Height.Exactly -> maxHeight.size
                    is Height.Fraction -> this.maxHeight * maxHeight.fraction
                    null -> Dp.Unspecified
                }
            }
        Box(
            Modifier.ifTrue(!LocalInspectionMode.current) {
                heightIn(
                    max = maxHeight,
                    min = minHeight,
                )
            },
        ) {
            content()
        }
    }
}
