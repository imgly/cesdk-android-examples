package ly.img.editor.core.component.data

import androidx.annotation.FloatRange
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp

/**
 * A class that describes the height of a component.
 */
@Stable
sealed interface Height {
    /**
     * Height as an exact [size].
     */
    @Stable
    data class Exactly(val size: Dp) : Height

    /**
     * Height as a [fraction] of another height.
     */
    @Stable
    data class Fraction(
        @FloatRange(from = 0.0, to = 1.0) val fraction: Float,
    ) : Height
}
