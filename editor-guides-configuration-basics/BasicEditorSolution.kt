import android.util.Log
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import ly.img.editor.Editor
import ly.img.editor.EditorUiMode
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.engine.EngineRenderTarget

// Call this composable from your app's navigation layer.
@Composable
fun BasicEditorSolution(
    license: String,
    signedInUserId: String?,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        // highlight-android-license
        license = license,
        // highlight-android-license
        // highlight-android-user-id
        userId = signedInUserId,
        // highlight-android-user-id
        // highlight-android-base-uri
        baseUri = "file:///android_asset/assets/".toUri(),
        // highlight-android-base-uri
        // highlight-android-rendering
        engineRenderTarget = EngineRenderTarget.SURFACE_VIEW,
        uiMode = EditorUiMode.SYSTEM,
        // highlight-android-rendering
        // highlight-android-runtime-configuration
        configuration = {
            EditorConfiguration.remember {
                onLoaded = {
                    val editor = editorContext.engine.editor
                    editor.setRole("Creator")
                    Log.i("ConfigurationGuide", "Current role: ${editor.getRole()}")

                    editor.setSettingBoolean(
                        keypath = "doubleClickToCropEnabled",
                        value = false,
                    )
                    val doubleClickToCropEnabled = editor.getSettingBoolean(
                        keypath = "doubleClickToCropEnabled",
                    )
                    Log.i(
                        "ConfigurationGuide",
                        "Double-click crop enabled: $doubleClickToCropEnabled",
                    )
                }
            }
        },
        // highlight-android-runtime-configuration
        onClose = onClose,
    )
}
