package ly.img.editor.core.ui.scope

import android.app.Activity
import android.net.Uri
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import ly.img.editor.core.EditorContext
import ly.img.editor.core.EditorScope
import ly.img.editor.core.LocalEditorScope
import ly.img.editor.core.component.Dock
import ly.img.editor.core.component.InspectorBar
import ly.img.editor.core.event.EditorEventHandler
import ly.img.editor.core.library.AssetLibrary
import ly.img.editor.core.ui.utils.activity
import ly.img.engine.Engine
import ly.img.engine.UnstableEngineApi

@Composable
fun EditorScope(
    editorScope: EditorScope,
    content: @Composable () -> Unit,
) {
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
        LocalEditorScope provides editorScope,
        content = content,
    )
}

class EditorContextImpl(
    override val license: String,
    override val userId: String?,
    override val baseUri: Uri,
    override val navigationIcon: ImageVector,
    override val colorPalette: List<Color>,
    override val assetLibrary: AssetLibrary,
    override val overlay: (@Composable (EditorScope.(Parcelable) -> Unit)?),
    override val dock: (@Composable (EditorScope.() -> Dock))?,
    override val inspectorBar: (@Composable (EditorScope.() -> InspectorBar))?,
) : EditorContext {
    @OptIn(UnstableEngineApi::class)
    override val engine: Engine by lazy {
        Engine.getInstance(id = "ly.img.editor").also {
            it.idlingEnabled = true
        }
    }

    private var _activity: Activity? = null
    override val activity: Activity
        get() = requireNotNull(_activity) { "Activity is not initialized yet." }

    private var _eventHandler: EditorEventHandler? = null
    override val eventHandler: EditorEventHandler
        get() = requireNotNull(_eventHandler) { "EditorEventHandler is not initialized yet." }

    fun init(
        activity: Activity,
        eventHandler: EditorEventHandler,
    ) {
        _activity = activity
        _eventHandler = eventHandler
    }

    fun clear() {
        _activity = null
        _eventHandler = null
    }
}
