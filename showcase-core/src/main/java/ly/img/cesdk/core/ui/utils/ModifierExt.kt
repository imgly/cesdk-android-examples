package ly.img.cesdk.core.ui.utils

import androidx.compose.ui.Modifier

inline fun Modifier.ifTrue(predicate: Boolean, builder: Modifier.() -> Modifier) =
    then(if (predicate) this.builder() else Modifier)