package ly.img.editor

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ly.img.editor.core.EditorContext
import ly.img.editor.core.EditorScope
import ly.img.editor.core.ui.scope.EditorContextImpl

@Composable
internal fun rememberEditorScope(
    engineConfiguration: EngineConfiguration,
    editorConfiguration: EditorConfiguration<*>,
): EditorScope =
    remember(engineConfiguration, editorConfiguration) {
        object : EditorScope() {
            override val impl: EditorContext =
                EditorContextImpl(
                    license = engineConfiguration.license,
                    userId = engineConfiguration.userId,
                    baseUri = engineConfiguration.baseUri,
                    navigationIcon = editorConfiguration.navigationIcon,
                    colorPalette = editorConfiguration.colorPalette,
                    assetLibrary = editorConfiguration.assetLibrary,
                    dock = editorConfiguration.dock,
                    inspectorBar = editorConfiguration.inspectorBar,
                    overlay = (editorConfiguration as EditorConfiguration<Parcelable>).overlay,
                )
        }
    }
