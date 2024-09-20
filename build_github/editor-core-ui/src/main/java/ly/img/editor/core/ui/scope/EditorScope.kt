package ly.img.editor.core.ui.scope

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.core.ui.utils.activity

@Composable
fun EditorScope(content: @Composable () -> Unit) {
    val editorViewModelStoreOwner = viewModel<EditorScopeViewModel>()
    val activity = LocalContext.current.activity
    remember {
        object : RememberObserver {
            override fun onAbandoned() {
                editorViewModelStoreOwner.clear()
            }

            override fun onForgotten() {
                if (activity?.isChangingConfigurations == false) {
                    editorViewModelStoreOwner.clear()
                }
            }

            override fun onRemembered() = Unit
        }
    }
    CompositionLocalProvider(
        LocalViewModelStoreOwner provides editorViewModelStoreOwner,
        content = content,
    )
}
