import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import ly.img.editor.Editor
import ly.img.editor.core.configuration.EditorConfiguration
import ly.img.editor.core.configuration.remember
import ly.img.engine.DesignBlockType
import ly.img.engine.Engine

@Composable
fun TemplatingEditorSolution(
    license: String? = null,
    onClose: (Throwable?) -> Unit,
) {
    val context = LocalContext.current
    Editor(
        license = license,
        baseUri = "file:///android_asset/".toUri(),
        configuration = {
            EditorConfiguration.remember {
                onCreate = {
                    val engine = editorContext.engine
                    loadPostcardTemplate(engine)

                    // highlight-android-set-variables
                    // Register the variables used by this tropical postcard template.
                    engine.variable.set(key = "first_name", value = "Alice")
                    engine.variable.set(key = "last_name", value = "Smith")
                    engine.variable.set(key = "city", value = "Paris")
                    engine.variable.set(key = "address", value = "10 Rue de Rivoli")
                    // highlight-android-set-variables

                    // highlight-android-discover-variables
                    val variableNames = engine.variable.findAll()
                    // highlight-android-discover-variables
                    Log.d("TemplatingGuide", "Registered scene variables: $variableNames")
                    Log.d(
                        "TemplatingGuide",
                        "Loaded tropical postcard template for ${engine.variable.get("first_name")} ${engine.variable.get("last_name")}",
                    )

                    // highlight-android-discover-placeholders
                    val placeholderBlocks = engine.block.findAllPlaceholders()
                    // highlight-android-discover-placeholders
                    Log.d("TemplatingGuide", "Template placeholders: ${placeholderBlocks.size}")
                    placeholderBlocks.forEach { placeholder ->
                        if (engine.block.supportsPlaceholderControls(placeholder)) {
                            engine.block.setPlaceholderControlsOverlayEnabled(placeholder, enabled = true)
                            engine.block.setPlaceholderControlsButtonEnabled(placeholder, enabled = true)
                        }
                    }
                }
                onError = { throwable ->
                    Toast.makeText(context, throwable.message, Toast.LENGTH_SHORT).show()
                }
            }
        },
        onClose = onClose,
    )
}

private suspend fun loadPostcardTemplate(engine: Engine) {
    // highlight-android-load-template
    engine.scene.load(
        sceneUri = "https://cdn.img.ly/assets/demo/v3/ly.img.template/templates/cesdk_postcard_2.scene".toUri(),
        waitForResources = true,
    )
    // highlight-android-load-template
}

private suspend fun applyPostcardTemplate(engine: Engine) {
    val scene = engine.scene.get() ?: engine.scene.create()
    if (engine.scene.getPages().isEmpty()) {
        val page = engine.block.create(DesignBlockType.Page)
        engine.block.setWidth(block = page, value = 1080F)
        engine.block.setHeight(block = page, value = 1080F)
        engine.block.appendChild(parent = scene, child = page)
    }

    // highlight-android-apply-template
    engine.scene.applyTemplate(
        templateUri = "https://cdn.img.ly/assets/demo/v3/ly.img.template/templates/cesdk_postcard_2.scene".toUri(),
    )
    // highlight-android-apply-template
}
