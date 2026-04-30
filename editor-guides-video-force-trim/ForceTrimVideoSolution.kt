import androidx.compose.runtime.Composable
import ly.img.editor.Editor
import ly.img.editor.configuration.video.VideoConfigurationBuilder
import ly.img.editor.configuration.video.callback.onLoaded
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.event.EditorEvent
import kotlin.time.Duration.Companion.seconds

// Add this composable to your NavHost
@Composable
fun ForceTrimVideoSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    // highlight-android-editor
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember(::VideoConfigurationBuilder) {
                onLoaded = {
                    // highlight-android-constraints
                    val event = EditorEvent.ApplyVideoDurationConstraints(
                        minDuration = 1.seconds,
                        maxDuration = 5.seconds,
                    )
                    editorContext.eventHandler.send(event)
                    // highlight-android-constraints
                    onLoaded()
                }
            }
        },
        onClose = onClose,
    )
    // highlight-android-editor
}
