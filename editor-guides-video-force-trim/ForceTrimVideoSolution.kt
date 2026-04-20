import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import ly.img.editor.Editor
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.event.EditorEvent
import kotlin.time.Duration.Companion.seconds

// Add this composable to your NavHost
@Composable
fun ForceTrimVideoSolution(navController: NavHostController) {
    // highlight-android-editor
    Editor(
        license = null, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                onCreate = {
                    // highlight-android-oncreate
                    val videoRemoteUri = "https://img.ly/static/ubq_video_samples/bbb.mp4".toUri()
                    editorContext.engine.scene.createFromVideo(videoRemoteUri)
                    // highlight-android-oncreate
                }
                onLoaded = {
                    // highlight-android-constraints
                    val event = EditorEvent.ApplyVideoDurationConstraints(
                        minDuration = 10.seconds,
                        maxDuration = 20.seconds,
                    )
                    editorContext.eventHandler.send(event)
                    // highlight-android-constraints
                }
            }
        },
    ) {
        navController.popBackStack()
    }
    // highlight-android-editor
}
