package ly.img.cesdk.core.library.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.valentinilk.shimmer.shimmer
import ly.img.cesdk.core.theme.LocalShimmer

internal fun Modifier.shimmerWithLocalShimmer() = composed {
    shimmer(LocalShimmer.current)
}