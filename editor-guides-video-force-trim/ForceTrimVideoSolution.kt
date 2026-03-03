import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ly.img.editor.EditorDefaults
import ly.img.editor.EngineConfiguration
import ly.img.editor.VideoEditor
import kotlin.time.Duration.Companion.seconds

// Add this composable to your NavHost
@Composable
fun ForceTrimVideoSolution(navController: NavHostController) {
    val engineConfiguration = EngineConfiguration.remember(
        license = null,
        onCreate = {
            // highlight-android-oncreate
            EditorDefaults.onCreate(
                editorContext.engine,
                EngineConfiguration.defaultVideoSceneUri,
                editorContext.eventHandler,
            )
            // highlight-android-oncreate
        },
        onLoaded = {
            // highlight-android-constraints
            editorContext.setVideoDurationConstraints(
                minimumVideoDuration = 10.seconds,
                maximumVideoDuration = 20.seconds,
            )
            // highlight-android-constraints
        },
    )

    // highlight-android-editor
    VideoEditor(
        engineConfiguration = engineConfiguration,
    ) {
        navController.popBackStack()
    }
    // highlight-android-editor
}
