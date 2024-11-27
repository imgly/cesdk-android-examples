package ly.img.editor.core.component.data

import androidx.compose.runtime.Stable

/**
 * For maintenance only. Does not affect the editor.
 */
@Stable
class Nothing internal constructor() {
    override fun toString() = ""
}

val nothing = Nothing()
