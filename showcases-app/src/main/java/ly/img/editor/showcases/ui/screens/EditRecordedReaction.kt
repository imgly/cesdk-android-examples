package ly.img.editor.showcases.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ly.img.camera.core.CameraResult
import ly.img.editor.EditorConfiguration
import ly.img.editor.EngineConfiguration
import ly.img.editor.VideoEditor
import ly.img.editor.rememberForVideo
import ly.img.editor.showcases.Secrets
import ly.img.editor.showcases.util.createSceneFromReaction

@Composable
fun EditRecordedReaction(
    reaction: CameraResult.Reaction,
    onBack: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val engineConfig = EngineConfiguration.remember(
            license = Secrets.license,
            onCreate = {
                val eventHandler = editorContext.eventHandler
                val engine = editorContext.engine
                engine.createSceneFromReaction(
                    cameraResult = reaction,
                    eventHandler = eventHandler,
                )
            },
        )

        val editorConfig = EditorConfiguration.rememberForVideo()
        VideoEditor(
            engineConfiguration = engineConfig,
            editorConfiguration = editorConfig,
        ) {
            onBack()
        }
    }
}
