package ly.img.editor

import android.os.Parcelable
import androidx.compose.runtime.Composable
import ly.img.editor.core.EditorScope
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.theme.EditorTheme
import ly.img.editor.core.ui.scope.EditorScope
import ly.img.editor.design.DesignUi

/**
 * Built to support versatile editing capabilities for a broad range of design applications.
 * Toggling from edit and preview modes enables users to evaluate their design within the full context of their design project.
 * A floating action button at the bottom of the editor features only the most essential editing options in order of relevance
 * allowing users to overlay text, add images, shapes, stickers and upload new image assets.
 *
 * @param engineConfiguration the configuration object of the [ly.img.engine.Engine]. Check [EngineConfiguration] documentation
 * on how to create the object. It is recommended to use the default builder for [DesignEditor]: [EngineConfiguration.rememberForDesign].
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
fun DesignEditor(
    engineConfiguration: EngineConfiguration,
    editorConfiguration: EditorConfiguration<*> = EditorConfiguration.rememberForDesign(),
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
            DesignUi(
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
