import android.widget.Toast
import androidx.compose.runtime.Composable
import ly.img.editor.Editor
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.editor.core.event.EditorEvent
import ly.img.engine.DesignBlockType

// highlight-configuration-custom-event
object ShowLoading : EditorEvent

object HideLoading : EditorEvent
// highlight-configuration-custom-event

// Add this composable to your NavHost
@Composable
fun UiEventsEditorSolution(
    license: String,
    onClose: (Throwable?) -> Unit,
) {
    Editor(
        license = license, // pass null or empty for evaluation mode with watermark
        configuration = {
            EditorConfiguration.remember {
                // highlight-configuration-onCreate-events
                onCreate = {
                    editorContext.eventHandler.send(ShowLoading)
                    try {
                        val scene = editorContext.engine.scene.create()
                        val page = editorContext.engine.block.create(DesignBlockType.Page)
                        editorContext.engine.block.setWidth(block = page, value = 1080F)
                        editorContext.engine.block.setHeight(block = page, value = 1080F)
                        editorContext.engine.block.appendChild(parent = scene, child = page)
                    } finally {
                        editorContext.eventHandler.send(HideLoading)
                    }
                }
                // highlight-configuration-onCreate-events
                // highlight-configuration-on-event
                onEvent = { event ->
                    when (event) {
                        is ShowLoading, is HideLoading, is EditorEvent.SetViewMode -> {
                            Toast.makeText(editorContext.activity, "Observing events!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                // highlight-configuration-on-event
            }
        },
        onClose = onClose,
    )
}
