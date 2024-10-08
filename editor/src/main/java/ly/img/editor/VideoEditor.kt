package ly.img.editor

import android.app.Activity
import android.os.Parcelable
import androidx.compose.runtime.Composable
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.theme.EditorTheme
import ly.img.editor.core.ui.scope.EditorScope
import ly.img.editor.video.VideoUi

/**
 * Built to support versatile video editing capabilities for a broad range of video applications.
 *
 * @param engineConfiguration the configuration object of the [ly.img.engine.Engine]. Check [EngineConfiguration] documentation
 * on how to create the object. It is recommended to use the default builder for [PostcardEditor]: [EngineConfiguration.getForPostcard].
 * @param editorConfiguration the configuration object of the UI. Check [EditorConfiguration] documentation
 * on how to create the object. Note that this parameter is optional. The default behavior is specified in the constructor of the class.
 * @param onClose the callback that is invoked when the editor is closed. Here are some of the recommended implementations:
 * 1. activity.finish() if the editor is launched in a standalone activity.
 * 2. fragmentManager.popBackStack() if the editor is launched in a fragment.
 * 3. navController.popBackStack() if the editor is launched as a new composable destination in NavHost.
 */
@Composable
fun VideoEditor(
    engineConfiguration: EngineConfiguration,
    editorConfiguration: EditorConfiguration<*> = EditorConfiguration.getDefault(),
    onClose: (Throwable?) -> Unit,
) {
    EditorTheme(
        useDarkTheme = editorConfiguration.uiMode.useDarkTheme,
    ) {
        EditorScope {
            @Suppress("UNCHECKED_CAST")
            VideoUi(
                initialExternalState = editorConfiguration.initialState,
                license = engineConfiguration.license,
                userId = engineConfiguration.userId,
                renderTarget = engineConfiguration.renderTarget,
                navigationIcon = editorConfiguration.navigationIcon,
                baseUri = engineConfiguration.baseUri,
                colorPalette = editorConfiguration.colorPalette,
                assetLibrary = editorConfiguration.assetLibrary,
                onCreate = engineConfiguration.onCreate,
                onExport = engineConfiguration.onExport,
                onUpload = engineConfiguration.onUpload,
                onClose = engineConfiguration.onClose,
                onError = engineConfiguration.onError,
                onEvent = editorConfiguration.onEvent as (Activity, Parcelable, EditorEvent) -> Parcelable,
                overlay = { state, eventHandler ->
                    (editorConfiguration as EditorConfiguration<Parcelable>).overlay(state, eventHandler)
                },
                close = onClose,
            )
        }
    }
}
