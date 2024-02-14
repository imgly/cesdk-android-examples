package ly.img.editor.core.ui

import androidx.compose.runtime.Composable

// https://issuetracker.google.com/issues/252471936
abstract class AnyComposable {
    @Composable
    fun Content() {
        ComposableContent()
    }

    @Composable
    protected abstract fun ComposableContent()
}
