package ly.img.editor.showcases.ui.screen

import android.net.Uri
import androidx.compose.runtime.Composable
import ly.img.camera.core.CameraResult
import ly.img.editor.Editor
import ly.img.editor.configuration.video.VideoConfigurationBuilder
import ly.img.editor.configuration.video.callback.onCreate
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.configuration.then
import ly.img.editor.showcases.Secrets
import ly.img.editor.showcases.plugin.ShowcasesPlugin
import ly.img.editor.showcases.util.onPostCreateSceneFromReaction

@Composable
fun EditRecordedReactionScreen(
    baseUri: Uri,
    reaction: CameraResult.Reaction,
    onBack: () -> Unit,
) {
    Editor(
        license = Secrets.license,
        baseUri = baseUri,
        configuration = {
            EditorConfiguration.remember(::VideoConfigurationBuilder) {
                onCreate = {
                    val isNewScene = editorContext.engine.scene.get() == null
                    onCreate(
                        postCreateScene = {
                            if (isNewScene) {
                                onPostCreateSceneFromReaction(cameraResult = reaction)
                            }
                        },
                    )
                }
            }.then(::ShowcasesPlugin) {
                this.isVideoScene = true
            }
        },
    ) {
        onBack()
    }
}
