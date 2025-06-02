package ly.img.editor.showcases.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import ly.img.camera.core.CameraResult
import ly.img.editor.EditorConfiguration
import ly.img.editor.EditorDefaults
import ly.img.editor.EngineConfiguration
import ly.img.editor.VideoEditor
import ly.img.editor.core.event.EditorEvent
import ly.img.editor.core.library.data.AssetSourceType
import ly.img.editor.rememberForVideo
import ly.img.editor.showcases.Secrets

@Composable
fun EditCameraRecordings(
    recording: CameraResult.Record,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val engineConfig = EngineConfiguration.remember(
            license = Secrets.license,
            onCreate = {
                val eventHandler = editorContext.eventHandler
                EditorDefaults.onCreate(
                    engine = editorContext.engine,
                    eventHandler = editorContext.eventHandler,
                    sceneUri = EngineConfiguration.defaultVideoSceneUri,
                )

                scope.launch {
                    awaitFrame()
                    eventHandler.send(
                        EditorEvent.AddCameraRecordingsToScene(
                            uploadAssetSourceType = AssetSourceType.VideoUploads,
                            recordings = recording.recordings
                                .flatMap { recording ->
                                    recording.videos
                                        .map { it.uri to recording.duration }
                                },
                        ),
                    )
                }
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
