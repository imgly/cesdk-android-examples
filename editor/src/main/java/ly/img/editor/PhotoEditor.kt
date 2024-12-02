package ly.img.editor

import android.os.Parcelable
import androidx.compose.runtime.Composable
import ly.img.editor.core.EditorScope
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.theme.EditorTheme
import ly.img.editor.core.ui.scope.EditorScope
import ly.img.editor.photo.PhotoUi

/**
 * Built to provide versatile photo editing capabilities. Toggling from edit and preview modes enables users to evaluate their
 * edited photo before export. A dock at the bottom of the editor provides quick access to the most essential photo editing
 * tools allowing users to tweak adjustments, crop the photo, and add filters, effects, blur, text, shapes, as well as stickers.
 *
 * @param engineConfiguration the configuration object of the [ly.img.engine.Engine]. Check [EngineConfiguration] documentation
 * on how to create the object. It is recommended to use the default builder for [PhotoEditor]: [EngineConfiguration.rememberForPhoto].
 * @param editorConfiguration the configuration object of the UI. Check [EditorConfiguration] documentation
 * on how to create the object. Note that this parameter is optional. The default behavior is specified in the constructor of the class.
 * @param onClose the callback that is invoked when the editor is closed. Here are some of the recommended implementations:
 * 1. activity.finish() if the editor is launched in a standalone activity.
 * 2. fragmentManager.popBackStack() if the editor is launched in a fragment.
 * 3. navController.popBackStack() if the editor is launched as a new composable destination in NavHost.
 * If the optional parameter [Throwable] is not null, it means that the editor is closed due to an error.
 * The value is propagated from [ly.img.editor.core.event.EditorEvent.CloseEditor]. Unless custom types are sent
 * to this function, the throwable is always going to be either an [EditorException] or [ly.img.engine.EngineException].
 */
@Composable
fun PhotoEditor(
    engineConfiguration: EngineConfiguration,
    editorConfiguration: EditorConfiguration<*> = EditorConfiguration.rememberForPhoto(),
    onClose: (Throwable?) -> Unit,
) {
    EditorTheme(
        useDarkTheme = editorConfiguration.uiMode.useDarkTheme,
    ) {
        val editorScope =
            rememberEditorScope(
                engineConfiguration = engineConfiguration,
                editorConfiguration = editorConfiguration,
            )
        EditorScope(editorScope) {
            @Suppress("UNCHECKED_CAST")
            PhotoUi(
                initialExternalState = editorConfiguration.initialState,
                renderTarget = engineConfiguration.renderTarget,
                editorScope = editorScope,
                onCreate = engineConfiguration.onCreate,
                onExport = engineConfiguration.onExport,
                onUpload = engineConfiguration.onUpload,
                onClose = engineConfiguration.onClose,
                onError = engineConfiguration.onError,
                onEvent = editorConfiguration.onEvent as EditorScope.(Parcelable, EditorEvent) -> Parcelable,
                close = onClose,
            )
        }
    }
}
