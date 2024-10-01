package ly.img.editor.base.components.scrollbar.controller

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State

@Stable
interface StateController {
    val thumbSizeNormalized: State<Float>
    val thumbOffsetNormalized: State<Float>
    val thumbIsInAction: State<Boolean>
}
