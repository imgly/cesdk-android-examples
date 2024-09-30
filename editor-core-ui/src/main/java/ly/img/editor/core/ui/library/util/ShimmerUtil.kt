package ly.img.editor.core.ui.library.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.valentinilk.shimmer.shimmer
import ly.img.editor.core.theme.LocalShimmer

fun Modifier.shimmerWithLocalShimmer() =
    composed {
        shimmer(LocalShimmer.current)
    }
